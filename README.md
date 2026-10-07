# MobHealth (Bukkit)

When you damage a mob, MobHealth shows how much damage you dealt and how much health the mob has
left. This is the classic Bukkit plugin rebuilt around the
[NeoForge rewrite](https://github.com/Sablednah/MobHealth-Forge)'s feature set, for **every server
line from Beta 1.7.3 to 26.3**: Spigot, Paper, CraftBukkit and the Beta forks.

| | |
|---|---|
| **Servers** | Beta 1.7.3 · 1.7.2–1.7.10 · 1.8–1.12.2 · 1.13 → 26.3 |
| **Players** | nothing to install; it all works on vanilla clients |
| **Java** | whatever your server runs (the jars are Java 8 bytecode) |
| **License** | MIT |

The 9.x plugin (Bukkit 1.8–1.10, Spout, Heroes) lives on the `master` branch.

---

## Display modes

Every mode can be enabled independently and combined freely. Not every server can show every
mode, and MobHealth says at startup which enabled modes the server it is on cannot carry.

| Mode | What it looks like | Needs | Config key |
|------|--------------------|-------|-----------|
| **Chat** | `Zombie [\|\|\|\|\|\|\|\|\|\|] 14/20 (-6)` sent to you when you hit a mob | any | `display.chat` |
| **Action bar** | The same readout on the text line above your hotbar | 1.8+ | `display.actionBar` |
| **Nameplate** | A coloured health bar on the mob's name tag above its head | 1.7+ | `display.nameplate` |
| **Boss bar** | The vanilla boss-bar widget at the top of your screen | 1.9+ | `display.bossBar` |
| **Damage indicators** | A red number that pops off the mob on each hit, rises and vanishes | 1.8+ | `display.damageIndicators` |
| **Graphical** | A floating health bar riding above the mob | 1.19.4+ | `display.graphical` |

Bars are coloured by remaining health: **green → yellow → red**.

Two notes on how a server (rather than a client mod) does these:

- **Damage indicators and the graphical bar are entities.** A text display on 1.19.4+, an
  invisible armour stand with a name before that. They are shown only to the players who should
  see them from 1.18 on; older servers cannot hide an entity from one player, so there everyone
  nearby sees them.
- **The nameplate is a single shared property of the mob**, so *everyone* nearby sees the same
  one. The `audience` setting, the personal mute (`/mobhealth toggle`) and the `mobhealth.see`
  permission apply to the other modes but cannot hide a nameplate from an individual player.

### Which server gets what

| | Beta 1.7.3 | 1.7.x | 1.8 | 1.9–1.12 | 1.13–1.17 | 1.18–1.19.3 | 1.19.4 → 26.3 |
|---|---|---|---|---|---|---|---|
| Chat | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ |
| Action bar | | | ✔ | ✔ | ✔ | ✔ | ✔ |
| Nameplate | | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ |
| Boss bar | | | | ✔ | ✔ | ✔ | ✔ |
| Damage indicators | | | everyone nearby | everyone nearby | everyone nearby | per viewer | per viewer |
| Graphical bar | | | | | | | ✔ |
| Per-mob overrides by registry id | | enum name | enum name | enum name | enum name | ✔ | ✔ |
| Mute survives a restart | players.yml | players.yml | players.yml | players.yml | ✔ | ✔ | ✔ |

Beta 1.7.3 has an `int` health, no maximum to ask for, no name tags and no action bar, so there
MobHealth is chat only, with a table of what each creature spawned with.

---

## Installation

Download the jar for your server line and drop it into `plugins/`:

| Jar | Servers |
|---|---|
| `mobhealth-<version>+bukkit1.13-26.3.jar` | Spigot / Paper 1.13 through 26.3 |
| `mobhealth-<version>+bukkit1.8-1.12.jar` | 1.8 through 1.12.2 |
| `mobhealth-<version>+bukkit1.7.jar` | CraftBukkit / Spigot 1.7.2-R0.3 through 1.7.10 |
| `mobhealth-<version>+beta1.7.3.jar` | Beta 1.7.3: CraftBukkit #1060, Poseidon, UberBukkit, Canyon |

Start the server once to generate `plugins/MobHealth/config.yml`.

---

## Commands

| Command | Who | Description |
|---------|-----|-------------|
| `/mobhealth reload` | `mobhealth.reload` (op) | Re-read `config.yml`. |
| `/mobhealth toggle` | `mobhealth.toggle` (everyone) | Toggle **your own** displays on/off. Saved across logouts and deaths. |
| `/mobhealth toggle on\|off` | everyone | Set your displays explicitly. |
| `/mobhealth toggle on\|off <player>` | `mobhealth.toggle.others` (op) | Set another player's. |

Aliases: `/mh`, `/mobh`, `/mhealth`.

## Permissions

| Node | Default | Controls |
|------|---------|----------|
| `mobhealth.see` | everyone | Whether a player **receives** displays at all. Deny it to hide MobHealth from a rank. |
| `mobhealth.toggle` | everyone | The personal toggle. |
| `mobhealth.toggle.others` | op | Toggling other players. |
| `mobhealth.reload` | op | The reload command. |
| `mobhealth.*` | op | All of the above. |

---

## Configuration

`plugins/MobHealth/config.yml` has the same sections and keys as the NeoForge mod's
`mobhealth-common.toml`, so [its documentation](https://github.com/Sablednah/MobHealth-Forge#configuration)
applies here, with these differences:

- `display.toast` and `[graphicalEnforce]` do not exist: both need a client mod.
- `display.graphical` is a server-side floating bar (a text display riding the mob), with its own
  `graphical.content` and `graphical.verticalOffset`.
- `damageindicators` gains `durationTicks` and `rise`, because the server draws the numbers here.
- `targets.overrides` ids are registry keys (`minecraft:zombified_piglin`) on 1.14+ and the
  `EntityType` constant in lower case (`minecraft:pig_zombie`) before that.

The sections, in brief:

| Section | What it decides |
|---|---|
| `display` | the master switch for each mode |
| `audience` | `ATTACKER` (only the player who hit the mob) or `NEARBY` within `nearbyRadius` |
| `targets` | `hostile` / `neutral` / `passive` / `players` / `bosses`, `hideUntilDamaged`, and per-entity `overrides` |
| `chat`, `actionbar`, `nameplate`, `graphical` | what each shows: `BAR`, `NUMBERS` or `BOTH`; the nameplate's `mode` (`ON_DAMAGE`, `NAMED_ONLY`, `ALWAYS`) |
| `bossbar` | colour |
| `damageindicators` | `minDamage`, `markKillingBlow`, `durationTicks`, `rise` |
| `bar` | the text bar: `segments`, `filledChar`, `emptyChar`, `valueStyle` (`NONE`, `CURRENT_MAX`, `PERCENT`) |
| `timing` | `displayTicks` (per group too) and `deathTicks` |

---

## Building from source

```
./gradlew build
```

needs a JDK 25 on the path (to read the 26.x API class files); every jar is Java 8 bytecode. The
four jars land in `build/libs/`. `./gradlew apiCheck` recompiles the plugin against spigot-api
1.13.2, 1.16.5, 1.20.4, 1.21.11 and 26.3, so a method a newer API removed is a compile error here
rather than a `NoSuchMethodError` on somebody's server.

How the build is shaped, and why: `docs/VERSIONS.md`.

## License

MIT. The 9.x plugin on `master` was GPLv3.
