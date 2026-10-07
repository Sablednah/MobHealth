#!/usr/bin/env bash
# Upload one built jar to the CurseForge (dev.bukkit.org) project.
#
#   CURSEFORGE_TOKEN=xxx CURSEFORGE_PROJECT_ID=35545 \
#     ./scripts/curseforge-upload.sh <jar> <changelog-file> [release-type]
#
# Normally run for you by .github/workflows/curseforge.yml when a GitHub release is published.
# Runnable by hand for a re-upload.
#
# Which Bukkit versions a jar is tagged with comes from its filename, which is why the "+" suffix
# is load-bearing:
#   mobhealth-10.0.0+bukkit1.13-26.3.jar   every numeric version from 1.13 to 26.3 inclusive
#   mobhealth-10.0.0+bukkit1.8-1.12.jar    every numeric version from 1.8 to 1.12 inclusive
#   mobhealth-10.0.0+bukkit1.7.jar         1.7.2, 1.7.4 and the CB 1.7.x-R0.x names
#   mobhealth-10.0.0+beta1.7.3.jar         "Beta 1.7.3" and "CB 1060"
#
# Bukkit plugins have their OWN game-version list on CurseForge, separate from the mods one: no
# modloader, Client/Server or Java tags, and patch releases are often collapsed (there is a "1.12"
# but no "1.12.2"). CurseForge wants numeric version IDs rather than names, and the list grows, so
# it is fetched from the API every run instead of being hardcoded here.
#
# API reference: https://support.curseforge.com/en/support/solutions/articles/9000197321
set -euo pipefail

BASE="https://dev.bukkit.org"

JAR="${1:?usage: curseforge-upload.sh <jar> <changelog-file> [release-type]}"
CHANGELOG_FILE="${2:?missing changelog file}"
RELEASE_TYPE="${3:-release}"

: "${CURSEFORGE_TOKEN:?set CURSEFORGE_TOKEN (create one at https://legacy.curseforge.com/account/api-tokens)}"
: "${CURSEFORGE_PROJECT_ID:?set CURSEFORGE_PROJECT_ID (shown on the CurseForge project page)}"

[ -f "$JAR" ] || { echo "!! No such jar: $JAR" >&2; exit 1; }
[ -f "$CHANGELOG_FILE" ] || { echo "!! No such changelog: $CHANGELOG_FILE" >&2; exit 1; }

NAME="$(basename "$JAR" .jar)"
PLUGIN_VERSION="$(sed -n 's/^mobhealth-\(.*\)+.*$/\1/p' <<<"$NAME")"
SUFFIX="$(sed -n 's/^mobhealth-.*+\(.*\)$/\1/p' <<<"$NAME")"
if [ -z "$PLUGIN_VERSION" ] || [ -z "$SUFFIX" ]; then
    echo "!! Cannot read a version and a +suffix from $NAME" >&2
    exit 1
fi

api() { curl -sS --max-time 120 -H "X-Api-Token: $CURSEFORGE_TOKEN" "$@"; }

echo ">> Resolving CurseForge Bukkit version IDs for $SUFFIX"
VERSIONS_JSON="$(api "$BASE/api/game/versions")"
if ! jq -e 'type == "array"' >/dev/null 2>&1 <<<"$VERSIONS_JSON"; then
    echo "!! Unexpected response from $BASE/api/game/versions — is the token valid?" >&2
    head -c 400 <<<"$VERSIONS_JSON" >&2; echo >&2
    exit 1
fi

# Compare dotted versions numerically: "1.8.3" < "1.12" < "26.1".
vernum() { awk -F. '{ printf "%d%03d%03d\n", $1, $2, $3 }' <<<"$1"; }

case "$SUFFIX" in
    bukkit*-*)
        LO="${SUFFIX#bukkit}"; LO="${LO%-*}"; HI="${SUFFIX##*-}"
        LABEL="Bukkit $LO – $HI"
        # Every plain numeric name in [LO, HI]. Names like "CB 1.7.9-R0.2" and "Beta 1.7.3" are not
        # numeric and are not in a range jar's remit.
        NAMES="$(jq -r '.[].name' <<<"$VERSIONS_JSON" | grep -E '^[0-9]+(\.[0-9]+){1,2}$' | while read -r v; do
            n="$(vernum "$v")"
            [ "$n" -ge "$(vernum "$LO")" ] && [ "$n" -le "$(vernum "$HI")" ] && echo "$v"
        done)"
        ;;
    bukkit1.7)
        LABEL="Bukkit 1.7"
        NAMES="$(printf '%s\n' '1.7.2' '1.7.4' 'CB 1.7.2-R0.3' 'CB 1.7.9-R0.1' 'CB 1.7.9-R0.2')"
        ;;
    beta1.7.3)
        LABEL="Beta 1.7.3"
        NAMES="$(printf '%s\n' 'Beta 1.7.3' 'CB 1060')"
        ;;
    *)
        echo "!! Unknown jar suffix '+$SUFFIX'; the publish script knows bukkit<lo>-<hi>, bukkit1.7 and beta1.7.3." >&2
        exit 1
        ;;
esac

IDS=()
MISSING=()
while read -r name; do
    [ -n "$name" ] || continue
    id="$(jq -r --arg v "$name" 'map(select(.name == $v)) | .[0].id // empty' <<<"$VERSIONS_JSON")"
    if [ -n "$id" ]; then IDS+=("$id"); else MISSING+=("$name"); fi
done <<<"$NAMES"

if [ ${#IDS[@]} -eq 0 ]; then
    echo "!! None of the wanted version names exist on CurseForge: $(tr '\n' ' ' <<<"$NAMES")" >&2
    echo "!! Names it does know that look related:" >&2
    jq -r '.[].name' <<<"$VERSIONS_JSON" | grep -E '^(CB |Beta |[0-9])' | sort -V | tail -40 | tr '\n' ' ' >&2; echo >&2
    exit 1
fi
[ ${#MISSING[@]} -eq 0 ] || echo "   (not on CurseForge, skipped: ${MISSING[*]})"
echo "   $(wc -l <<<"$NAMES" | tr -d ' ') names -> ${#IDS[@]} ids: $(tr '\n' ' ' <<<"$NAMES")"

GAME_VERSIONS="[$(IFS=,; echo "${IDS[*]}")]"
DISPLAY_NAME="MobHealth $PLUGIN_VERSION / $LABEL"

METADATA="$(jq -n \
    --rawfile changelog "$CHANGELOG_FILE" \
    --arg displayName "$DISPLAY_NAME" \
    --arg releaseType "$RELEASE_TYPE" \
    --argjson gameVersions "$GAME_VERSIONS" \
    '{changelog: $changelog, changelogType: "markdown", displayName: $displayName,
      releaseType: $releaseType, gameVersions: $gameVersions}')"

if [ -n "${CURSEFORGE_DEBUG:-}" ]; then
    echo ">> metadata:"
    jq . <<<"$METADATA" | sed 's/^/     /'
fi

echo ">> Uploading $(basename "$JAR") to project $CURSEFORGE_PROJECT_ID ($RELEASE_TYPE) as \"$DISPLAY_NAME\""
# --form-string, not -F, for the metadata: curl gives ';', '@' and '<' special meaning inside an -F
# value and a changelog containing any of them is silently mangled into "Invalid JSON".
RESPONSE="$(curl -sS --max-time 600 -w '\n%{http_code}' \
    -H "X-Api-Token: $CURSEFORGE_TOKEN" \
    --form-string "metadata=$METADATA" \
    -F "file=@$JAR" \
    "$BASE/api/projects/$CURSEFORGE_PROJECT_ID/upload-file")"

STATUS="$(tail -n1 <<<"$RESPONSE")"
BODY="$(sed '$d' <<<"$RESPONSE")"

if [ "$STATUS" = "200" ]; then
    FILE_ID="$(jq -r '.id // empty' <<<"$BODY" 2>/dev/null || true)"
    echo ">> Uploaded${FILE_ID:+ as file $FILE_ID}"
    # A 200 means CurseForge accepted the file, not that it is published: moderation runs afterwards
    # and can still reject it, most often as a duplicate, because CurseForge dedupes by content.
    echo ">> Note: moderation runs after this. Check the project's file list if it does not appear:"
    echo "   https://authors.curseforge.com/#/projects/$CURSEFORGE_PROJECT_ID/files"
    exit 0
fi

echo "!! CurseForge rejected the upload (HTTP $STATUS)" >&2
echo "$BODY" >&2
exit 1
