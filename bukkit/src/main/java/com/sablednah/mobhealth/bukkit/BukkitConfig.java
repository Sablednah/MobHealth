package com.sablednah.mobhealth.bukkit;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import com.sablednah.mobhealth.core.Audience;
import com.sablednah.mobhealth.core.BarContent;
import com.sablednah.mobhealth.core.HealthBarFormatter.ValueStyle;
import com.sablednah.mobhealth.core.MobCategory;
import com.sablednah.mobhealth.core.NameplateMode;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * The plugin's settings, read once from {@code config.yml} (and again on {@code /mobhealth reload}).
 *
 * <p>The sections and keys are those of the NeoForge mod's {@code mobhealth-common.toml}, minus the
 * two that only mean something with a client mod ({@code graphicalEnforce}, {@code toast}), so the
 * one set of docs describes both. Plain public fields: this is a value holder the plugin reads on
 * every hit, not an API.</p>
 */
public final class BukkitConfig {

    // display
    public boolean chat, actionBar, nameplate, bossBar, damageIndicators;
    // audience
    public Audience audience;
    public int nearbyRadius;
    // targets
    public boolean hostile, neutral, passive, players, bosses, hideUntilDamaged;
    private Map<String, Boolean> overrides = Collections.emptyMap();
    // chat / action bar / nameplate / boss bar
    public BarContent chatContent, actionBarContent, nameplateContent;
    public NameplateMode nameplateMode;
    public String bossBarColor;
    // damage indicators
    public double indicatorMinDamage;
    public boolean indicatorMarkKill;
    public int indicatorDurationTicks;
    public double indicatorRise;
    // bar
    public int barSegments;
    public char barFilledChar, barEmptyChar;
    public ValueStyle valueStyle;
    // timing
    public int displayTicks, displayTicksHostile, displayTicksNeutral, displayTicksPassive, deathTicks;

    public void load(FileConfiguration c, Logger log) {
        chat = c.getBoolean("display.chat", true);
        actionBar = c.getBoolean("display.actionBar", true);
        nameplate = c.getBoolean("display.nameplate", false);
        bossBar = c.getBoolean("display.bossBar", false);
        damageIndicators = c.getBoolean("display.damageIndicators", true);

        audience = enumOr(c.getString("audience.audience"), Audience.ATTACKER, log, "audience.audience");
        nearbyRadius = clamp(c.getInt("audience.nearbyRadius", 32), 4, 128);

        hostile = c.getBoolean("targets.hostile", true);
        neutral = c.getBoolean("targets.neutral", true);
        passive = c.getBoolean("targets.passive", true);
        players = c.getBoolean("targets.players", false);
        bosses = c.getBoolean("targets.bosses", true);
        hideUntilDamaged = c.getBoolean("targets.hideUntilDamaged", true);
        overrides = parseOverrides(c.getStringList("targets.overrides"), log);

        chatContent = enumOr(c.getString("chat.chatContent"), BarContent.BOTH, log, "chat.chatContent");
        actionBarContent = enumOr(c.getString("actionbar.actionBarContent"), BarContent.BOTH, log, "actionbar.actionBarContent");
        nameplateMode = enumOr(c.getString("nameplate.mode"), NameplateMode.ON_DAMAGE, log, "nameplate.mode");
        nameplateContent = enumOr(c.getString("nameplate.content"), BarContent.BOTH, log, "nameplate.content");
        bossBarColor = c.getString("bossbar.color", "RED");

        indicatorMinDamage = clamp(c.getDouble("damageindicators.minDamage", 0.0D), 0.0D, 100.0D);
        indicatorMarkKill = c.getBoolean("damageindicators.markKillingBlow", true);
        indicatorDurationTicks = clamp(c.getInt("damageindicators.durationTicks", 24), 4, 100);
        indicatorRise = clamp(c.getDouble("damageindicators.rise", 1.0D), 0.0D, 6.0D);

        barSegments = clamp(c.getInt("bar.segments", 20), 1, 100);
        barFilledChar = firstChar(c.getString("bar.filledChar", "|"), '|');
        barEmptyChar = firstChar(c.getString("bar.emptyChar", "|"), '|');
        valueStyle = enumOr(c.getString("bar.valueStyle"), ValueStyle.CURRENT_MAX, log, "bar.valueStyle");

        displayTicks = clamp(c.getInt("timing.displayTicks", 100), 20, 1200);
        displayTicksHostile = clamp(c.getInt("timing.displayTicksHostile", -1), -1, 1200);
        displayTicksNeutral = clamp(c.getInt("timing.displayTicksNeutral", -1), -1, 1200);
        displayTicksPassive = clamp(c.getInt("timing.displayTicksPassive", -1), -1, 1200);
        deathTicks = clamp(c.getInt("timing.deathTicks", 20), 0, 600);
    }

    /** Override wins; then bosses use their own toggle; otherwise the group toggle applies. */
    public boolean shouldDisplay(String entityKey, MobCategory category, boolean boss) {
        Boolean override = overrides.get(entityKey.toLowerCase(Locale.ROOT));
        if (override != null) {
            return override;
        }
        if (boss) {
            return bosses;
        }
        switch (category) {
            case HOSTILE:
                return hostile;
            case NEUTRAL:
                return neutral;
            case PASSIVE:
                return passive;
            case PLAYER:
                return players;
            default:
                return false;
        }
    }

    /** Display lifetime for a group, in ticks: the per-group override if set, else the default. */
    public int displayTicks(MobCategory category) {
        int specific;
        switch (category) {
            case HOSTILE:
                specific = displayTicksHostile;
                break;
            case NEUTRAL:
                specific = displayTicksNeutral;
                break;
            case PASSIVE:
                specific = displayTicksPassive;
                break;
            default:
                specific = -1;
        }
        return specific >= 0 ? specific : displayTicks;
    }

    private static Map<String, Boolean> parseOverrides(List<String> raw, Logger log) {
        Map<String, Boolean> out = new HashMap<String, Boolean>();
        for (String entry : raw) {
            int eq = entry.lastIndexOf('=');
            if (eq <= 0) {
                log.warning("Ignoring targets.overrides entry '" + entry + "': expected namespace:id=true|false");
                continue;
            }
            String id = entry.substring(0, eq).trim().toLowerCase(Locale.ROOT);
            String value = entry.substring(eq + 1).trim().toLowerCase(Locale.ROOT);
            if (!value.equals("true") && !value.equals("false")) {
                log.warning("Ignoring targets.overrides entry '" + entry + "': value must be true or false");
                continue;
            }
            if (id.indexOf(':') < 0) {
                id = "minecraft:" + id;
            }
            out.put(id, Boolean.valueOf(value));
        }
        return out;
    }

    private static <E extends Enum<E>> E enumOr(String raw, E fallback, Logger log, String key) {
        if (raw == null) {
            return fallback;
        }
        try {
            return Enum.valueOf(fallback.getDeclaringClass(), raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            log.warning("Unknown value '" + raw + "' for " + key + "; using " + fallback);
            return fallback;
        }
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static char firstChar(String s, char fallback) {
        return (s == null || s.isEmpty()) ? fallback : s.charAt(0);
    }
}
