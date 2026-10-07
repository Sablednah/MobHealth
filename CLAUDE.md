# CLAUDE.md

Working notes for this repo. `README.md` says what the plugin does; `docs/VERSIONS.md` says how the
build is shaped and what has been run. This file is only the conventions those two do not state.

## One branch, four jars

Unlike the Forge mod there is **no branch per Minecraft version**: `main` builds every jar, and
the version knowledge lives in `bukkit/.../compat/`. `master` is the 9.x plugin, frozen.

- **`bukkit` compiles against Bukkit 1.7.2-R0.3 on purpose.** Anything newer goes in a
  `compat-*` module compiled against the API step that introduced it, loaded by name from
  `CompatFactory` only when the server is new enough. Do not raise the floor to make a call
  compile; add the adapter.
- **Never call `Bukkit.getOnlinePlayers()` from `bukkit`.** It changed return type in 1.8 (array
  to `Collection`), so one compiled call cannot run on both sides. `World.getPlayers()` instead.
- **Type receivers as specifically as you know them** (`LivingEntity`, `Player`), because the
  compiler records the receiver's static type and methods have moved between interfaces over the
  years.
- **Name entity types, never reference them.** `EntityCategorizer` lists neutrals and odd hostiles
  by `EntityType` *name* string; a reference to a constant a 1.7 server lacks refuses to load the
  class there.
- `core` is a verbatim copy of MobHealth-Forge's `core` package. Diff before a release; do not
  edit it here.
- `./gradlew apiCheck` recompiles the plugin against five later API lines. Run it before pushing.
- Build with **JDK 25** (`JAVA_HOME=../MobHealth-Forge/tools/jdk25` works): the 26.x API class
  files need it. Output is Java 8 bytecode regardless.

## Versioning

`plugin_version` in `gradle.properties` is the one version (continues the Bukkit line: 9.1.0 →
10.0.0). A jar is `mobhealth-<version>+<suffix>.jar` and **the `+suffix` is load-bearing**:
`scripts/curseforge-upload.sh` maps `bukkit<lo>-<hi>`, `bukkit1.7` and `beta1.7.3` to the Bukkit
game versions on CurseForge, which has its own list (no `1.8.8`, no `1.12.2`; `CB 1.7.9-R0.2`,
`Beta 1.7.3`).

## Build stamp

Same format as every Sablednah mod: `/mobhealth/build.properties` in the jar, `Build-Commit` /
`Build-Branch` / `Build-Time` on the manifest, and a startup line
`MobHealth 10.0.0 (build a1b2c3d4 on main, 2026-…)`. The time is the **commit's** time, never the
wall clock. Change the format everywhere or nowhere.

## Testing

`tools/bot/` is a player: `mobhealth-bot.js` (mineflayer, 1.8.8 → 26.1) and `mobhealth-bot-17.js`
(raw protocol, 1.7.10). `tools/vivo/` are the test-server scripts that live in
`~/mc/mobhealth-bukkit/` on the Vivo box (`192.168.7.246`, port 25588, one server at a time,
display `:4` for the vanilla client). `run.sh <version>` picks the JDK and the jar; `console.sh`
types into the server; `stop.sh`.

- Servers are started with stdin on a FIFO. An old server reading EOF spins its console thread
  printing "Unknown command" and starves the login handler; this cost an hour.
- Old servers (≤1.16) **delete monsters outright** when `spawn-monsters=false`; the test
  properties set it true and stop natural spawns by gamerule or `kill @e[type=!player]`.
- Paper 26.x: whitelist on by default; the mob-spawning gamerule's name is not any of the obvious
  ones. Kill instead.
- mineflayer's `/gamemode` is `creative` on 1.13+, `1` before; summon names are `Zombie` on
  ≤1.10 and `zombie` after. The bot handles both.

## Publishing

A published GitHub release fans its jars out to dev.bukkit.org (project 35545) via
`.github/workflows/curseforge.yml`; a GitHub **pre-release** goes up as a CurseForge **beta**.
Attach **all four jars**. Never read or echo the token. `docs/curseforge-description.md` is the
store page, pasted in by hand.

Two things the first upload (v10.0.0-beta.1, 2026-10-07) taught:

- dev.bukkit.org's `/api/game/versions` returns the **whole** CurseForge catalogue (7,500 entries:
  Java, Forge, NeoForge builds), and naming one of those in an upload is refused as "belongs to an
  invalid dependency". The script keeps only `gameVersionTypeID` 1, "Bukkit" (125 entries).
- `workflow_dispatch` with `only=<substring>` re-uploads one jar; CurseForge rejects a re-upload
  of a jar it already has, so after a partial failure use `only`, never the whole tag.
