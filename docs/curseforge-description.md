# MobHealth

**Simple damage reporting.** When you hit a mob, MobHealth tells you how much damage you did and
how much health it has left: in chat, on the action bar, on the mob's name tag, as a boss bar, as
a floating damage number, or as a health bar hovering over the mob. Any combination, and nothing
for players to install.

MobHealth 11 is the classic plugin rebuilt around the NeoForge version's feature set, and it runs
on **every server line from Beta 1.7.3 to 26.3**. Pick the file for your server:

- **`+bukkit1.13-26.3`**: Spigot and Paper, 1.13 through 26.3
- **`+bukkit1.8-1.12`**: 1.8 through 1.12.2
- **`+bukkit1.7`**: CraftBukkit / Spigot 1.7.2 through 1.7.10
- **`+beta1.7.3`**: Beta 1.7.3 (CraftBukkit #1060, Poseidon, UberBukkit, Canyon)

## Display modes

| Mode | Needs |
|---|---|
| Chat: `Zombie [\|\|\|\|\|\|\|\|\|\|] 14/20 (-6)` | any server |
| Action bar: the same readout above the hotbar | 1.8+ |
| Nameplate: a coloured bar on the mob's name tag | 1.7+ |
| Boss bar: the widget at the top of the screen | 1.9+ |
| Damage indicators: red numbers that pop off the mob and rise | 1.8+ |
| Graphical: a floating bar riding above the mob | 1.19.4+ |

Bars go green → yellow → red as health drops. MobHealth says at startup which enabled modes your
server cannot show, so nothing fails quietly.

## Options

- Show for hostile, neutral and passive mobs, players and bosses separately, with per-mob
  overrides (`minecraft:villager=false`).
- Only the attacker sees it, or everyone nearby.
- Bar length, glyphs, numbers as `14/20` or `70%`, how long bars linger, how long after a kill.
- `/mobhealth toggle` lets each player mute it; `mobhealth.see` lets you hide it from a rank.
- The config has the same keys as the NeoForge mod, so one set of docs covers both.

## Commands and permissions

`/mobhealth reload` (`mobhealth.reload`, op) · `/mobhealth toggle [on|off] [player]`
(`mobhealth.toggle`, everyone; `mobhealth.toggle.others`, op) · `mobhealth.see` (everyone)

## Source and issues

https://github.com/Sablednah/MobHealth. The NeoForge version is a separate project, MobHealth
ReForged.
