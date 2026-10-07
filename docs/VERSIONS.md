# One plugin from Beta 1.7.3 to 26.3

How the build is shaped, what the research behind it found, and what has actually been run.
Written 2026-10-07 as the rewrite landed; the measurements are from that day.

## The shape

Bukkit's API is stable in a way NeoForge's is not: a plugin compiled against 1.7.2 still loads on
26.3, because almost nothing it calls has moved. So where the Forge mod carries a branch per
Minecraft line, this repo carries **one plugin body and a handful of adapters**, and the version
knowledge lives in exactly one place.

| Module | Compiled against | Carries |
|---|---|---|
| `core` | nothing | the Forge mod's loader-agnostic package, copied verbatim |
| `bukkit` | Bukkit **1.7.2-R0.3** | the whole plugin: config, listener, display manager, commands |
| `compat-1.8` | spigot-api 1.8.8 | armour-stand damage numbers; the pre-1.11 action bar as a reflected NMS packet |
| `compat-1.9` | spigot-api 1.12.2 | boss bars (1.9), the Spigot action bar call (1.11) |
| `compat-1.14` | spigot-api 1.14.4 | registry keys for overrides, persistent data containers |
| `compat-1.18` | spigot-api 1.18.2 | `hideEntity`, which makes numbers and bars per viewer |
| `compat-1.19` | spigot-api 1.19.4 | text displays: the damage number and the floating bar |
| `legacy-beta` | Bukkit **1.7.3-R3** (Beta) | its own tiny plugin: int health, old event system, chat only |

`CompatFactory` picks the newest adapter whose minimum version the server meets, by **class
name**: an adapter missing from the jar, or one the server cannot load, falls through to the next.
So a jar can carry adapters for servers it will never meet and they are inert there.

Four jars come out, because `api-version` is per jar and declaring `1.13` on a jar that must also
load on 1.8 stops it loading there:

| Jar | `api-version` | Adapters |
|---|---|---|
| `+bukkit1.13-26.3` | 1.13 | all |
| `+bukkit1.8-1.12` | none | 1.8, 1.9 |
| `+bukkit1.7` | none | none |
| `+beta1.7.3` | none | its own plugin |

The `+suffix` is load-bearing: `scripts/curseforge-upload.sh` reads it to tag the file with the
right Bukkit game versions.

## Why the plugin compiles against 1.7.2 and not 1.8

The two differ in one thing the plugin would otherwise use. `Bukkit.getOnlinePlayers()` returned
an **array** until 1.8 and a `Collection` after, and that is a different method descriptor: one
compiled call cannot run on both sides of it. The plugin uses `World.getPlayers()` for its nearby
scan instead, which has returned a `List` since forever, and the one adapter that needs the
online list is compiled against 1.18 where the shape is settled.

The other trap is method *movement*. `setCustomName` is declared on `Entity` in 1.7.2 and the
compiler records the receiver's static type, not the declaring interface, so calls on a
`LivingEntity` resolve wherever a later API puts the method. Keep receivers typed as specifically
as the code knows them and this takes care of itself.

## Measured, not predicted

`apiCheck` compiles the `bukkit` sources against spigot-api 1.13.2, 1.16.5, 1.20.4, 1.21.11 and
26.3. **Zero errors on all five** on 2026-10-07: nothing the plugin uses has been removed in
twelve years of API.

Then it was run. `tools/bot/mobhealth-bot.js` is a mineflayer player that joins, summons a zombie,
hits it three times and reports what the server showed it; `mobhealth-bot-17.js` does the same
over the raw protocol for 1.7.10, which mineflayer no longer speaks. Paper of each version on the
Vivo box:

| Server | Jar | Chat | Action bar | Boss bar | Numbers | Nameplate set → reverted | Floating bar |
|---|---|---|---|---|---|---|---|
| 26.3 (vanilla client, screenshots) | 1.13-26.3 | ✔ | ✔ | ✔ | ✔ | ✔ (entity data) | ✔ |
| 26.1.2 | 1.13-26.3 | ✔ | ✔ | ✔ | ✔ text display | ✔ | ✔ |
| 1.21.11 | 1.13-26.3 | ✔ | ✔ | ✔ | ✔ text display | ✔ | ✔ |
| 1.16.5 (Java 8) | 1.13-26.3 | ✔ | ✔ | ✔ | ✔ armour stand | ✔ | — |
| 1.12.2 (Java 8) | 1.8-1.12 | ✔ | ✔ | ✔ | ✔ armour stand | ✔ | — |
| 1.8.8 (Java 8) | 1.8-1.12 | ✔ | ✔ NMS packet | — (logged) | ✔ armour stand | ✔ | — |
| 1.7.10 (Java 8) | 1.7 | ✔ | — (logged) | — (logged) | — (logged) | ✔ | — |
| Beta 1.7.3 (Poseidon 1.1.8) | beta1.7.3 | loads; not yet hit-tested | | | | | |

"— (logged)" means the startup log said the mode was on but the server could not show it, which
is the behaviour wanted. Beta 1.7.3 needs a Beta-protocol client to hit anything; the plugin
enables and the chat path is the same formatter, but it has not been watched working.

The 26.3 client is a vanilla one on the Vivo box's display `:4`
(`~/mc/mobhealth-bukkit/client.sh`), driven with xdotool. Paper 26.3 has its whitelist **on by
default**, and the mob-spawning gamerule has a name none of `doMobSpawning`,
`do_mob_spawning`, `spawning.mobs` or `mob_spawning` matched; the tests clear mobs with
`kill @e[type=!player]` instead.

## Where the old APIs come from

The research that decided what could be built at all, all verified by fetching on 2026-10-07:

- **Nothing before 1.8 is on any Maven repository** but JitPack, which builds the public
  `github.com/Bukkit/Bukkit` tags on demand: `com.github.Bukkit:Bukkit:1.7.2-R0.3`,
  `1.7.9-R0.2`, `f210234e59` (the 1.7.10 snapshot) and Beta `1.7.3-R3`. The historic Sonatype
  snapshots, repo.bukkit.org, md-5, codemc, dmulloy2 and a dozen others are 1.8+ or gone.
  CraftBukkit itself is DMCA'd on GitHub. BuildTools floors at 1.8.
- Spigot's nexus serves `spigot-api` from 1.8 to 26.3. Paper's serves `paper-api` from 1.17,
  with 26.x versioned `26.3.build.N-beta|stable`; nothing here needs Paper's API.
- Beta 1.7.3's forks: Poseidon publishes `com.legacyminecraft.poseidon:poseidon-craftbukkit` at
  `repository.johnymuffin.com`; UberBukkit and Canyon publish nothing. Compiling against the
  stock 1.7.3-R3 API is what makes one jar run on all of them.
- Release 1.7.2-R0.3 already has double health, `Damageable`, custom names and `@EventHandler`,
  but **no `getFinalDamage()`**: hence the hit is measured a tick later rather than computed.

## Publishing

dev.bukkit.org is a different game on CurseForge from Minecraft mods, with its **own version
list**: no modloader, Client/Server or Java tags, and patch releases are collapsed (there is a
`1.12` but no `1.12.2`, a `1.8.3` but no `1.8.8`). 1.7 is spelled `1.7.2`, `1.7.4` and
`CB 1.7.9-R0.2`; Beta is `Beta 1.7.3` and `CB 1060`. The upload script tags a range jar with
every numeric name between its bounds and the two legacy jars with the fixed names.

## Not yet done

- A Beta 1.7.3 client to hit something with, so the beta jar is watched rather than trusted.
- 26.2 has not been run; 26.1.2 and 26.3 have, and the API between them is byte-identical for
  everything this plugin touches (see the Forge repo's VERSIONS.md for that diff).
- Toasts: possible on 1.12+ by loading a throwaway advancement per hit, but that reloads the
  advancement tree each time on Spigot. Not worth the lag; left out deliberately.
