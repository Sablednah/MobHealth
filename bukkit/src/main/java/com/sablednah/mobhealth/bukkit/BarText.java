package com.sablednah.mobhealth.bukkit;

import com.sablednah.mobhealth.core.BarContent;
import com.sablednah.mobhealth.core.HealthBarFormatter;

import org.bukkit.ChatColor;

/**
 * The coloured text bar and readouts shown by the chat, action bar, nameplate and boss-bar modes,
 * built from the live config. Bridges the loader-agnostic {@link HealthBarFormatter} to Bukkit's
 * legacy {@code §} formatting — the one text format every server from 1.7 to 26.3 understands.
 */
public final class BarText {

    private BarText() {}

    /** Every bar starts with this; it is how a bar is told apart from a real custom name. */
    static final String BAR_PREFIX = ChatColor.GRAY + "[";

    /** {@code §7[§a||||||||||||||§8      §7]}, the fill tinted by health fraction. */
    public static String bar(BukkitConfig cfg, double current, double max) {
        int segments = cfg.barSegments;
        int filled = HealthBarFormatter.filledSegments(current, max, segments);
        StringBuilder sb = new StringBuilder(segments + 12);
        sb.append(BAR_PREFIX);
        sb.append(colourFor(HealthBarFormatter.fraction(current, max)));
        repeat(sb, cfg.barFilledChar, filled);
        if (filled < segments) {
            sb.append(ChatColor.DARK_GRAY);
            repeat(sb, cfg.barEmptyChar, segments - filled);
        }
        sb.append(ChatColor.GRAY).append(']');
        return sb.toString();
    }

    /** {@code §f14/20} or {@code §f70%} per the configured value style, or empty. */
    public static String value(BukkitConfig cfg, double current, double max) {
        String text = HealthBarFormatter.value(current, max, cfg.valueStyle);
        return text.isEmpty() ? "" : ChatColor.WHITE + text;
    }

    /** {@code [bar] value}. */
    public static String barWithValue(BukkitConfig cfg, double current, double max) {
        String val = value(cfg, current, max);
        return val.isEmpty() ? bar(cfg, current, max) : bar(cfg, current, max) + " " + val;
    }

    public static String content(BukkitConfig cfg, double current, double max, BarContent content) {
        switch (content) {
            case BAR:
                return bar(cfg, current, max);
            case NUMBERS:
                return value(cfg, current, max);
            case BOTH:
            default:
                return barWithValue(cfg, current, max);
        }
    }

    /** True if a custom name is one of ours rather than a player-given name. */
    public static boolean looksLikeBar(String customName) {
        return customName != null && customName.contains(BAR_PREFIX);
    }

    /** Format a health number without a trailing {@code .0}. */
    public static String trim(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return Long.toString((long) v);
        }
        return String.valueOf(Math.round(v * 10.0D) / 10.0D);
    }

    private static ChatColor colourFor(double fraction) {
        if (fraction > 0.5D) {
            return ChatColor.GREEN;
        }
        if (fraction > 0.25D) {
            return ChatColor.YELLOW;
        }
        return ChatColor.RED;
    }

    private static void repeat(StringBuilder sb, char c, int count) {
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
    }
}
