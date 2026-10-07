package com.sablednah.mobhealth.core;

/**
 * Builds textual health bars and health readouts. This is the modern, loader-agnostic
 * replacement for the old plugin's "fake" text bars (e.g. {@code [||||  ]}).
 *
 * <p>Contains no Minecraft imports so it is fully portable and unit-testable. Colour is applied
 * by the caller (the display adapter) via section/style codes; this class only produces the
 * glyph structure and numeric text.
 */
public final class HealthBarFormatter {

    private HealthBarFormatter() {}

    /** How the numeric portion of the readout is rendered. */
    public enum ValueStyle {
        /** No numbers, bar only. */
        NONE,
        /** e.g. {@code 14/20}. */
        CURRENT_MAX,
        /** e.g. {@code 70%}. */
        PERCENT
    }

    /**
     * Render a segmented bar such as {@code ||||||||||||||    } for the given health fraction.
     *
     * @param current    current health (clamped to {@code [0, max]})
     * @param max        maximum health (values &le; 0 yield an empty bar)
     * @param segments   total number of segments in the bar (values &lt; 1 are treated as 1)
     * @param filledChar glyph used for a filled segment (e.g. '|' or '█')
     * @param emptyChar  glyph used for an empty segment (e.g. ' ' or '░')
     * @return the bar string, exactly {@code segments} characters long
     */
    public static String bar(double current, double max, int segments, char filledChar, char emptyChar) {
        int total = Math.max(1, segments);
        int filled = filledSegments(current, max, total);
        StringBuilder sb = new StringBuilder(total);
        for (int i = 0; i < total; i++) {
            sb.append(i < filled ? filledChar : emptyChar);
        }
        return sb.toString();
    }

    /**
     * Number of filled segments for the given health, out of {@code segments} total.
     *
     * <p>Never returns 0 for a living mob (so a badly hurt mob still shows a sliver), and never
     * returns {@code segments} for a mob that has taken any damage (so a full-looking bar always
     * means full health). The adapter uses this to colour filled vs empty glyphs independently.
     */
    public static int filledSegments(double current, double max, int segments) {
        int total = Math.max(1, segments);
        int filled = (int) Math.round(fraction(current, max) * total);
        if (filled == 0 && current > 0.0D) {
            filled = 1;
        }
        if (filled == total && current < max) {
            filled = total - 1;
        }
        return filled;
    }

    /**
     * Render just the numeric readout for a health value in the requested style.
     *
     * @return e.g. {@code "14/20"}, {@code "70%"} or an empty string for {@link ValueStyle#NONE}
     */
    public static String value(double current, double max, ValueStyle style) {
        double cur = Math.max(0.0D, Math.min(current, Math.max(0.0D, max)));
        switch (style) {
            case CURRENT_MAX:
                return trim(cur) + "/" + trim(max);
            case PERCENT:
                return Math.round(fraction(current, max) * 100.0D) + "%";
            case NONE:
            default:
                return "";
        }
    }

    /** Clamp {@code current/max} into {@code [0, 1]}, returning 0 when {@code max <= 0}. */
    public static double fraction(double current, double max) {
        if (max <= 0.0D) {
            return 0.0D;
        }
        double f = current / max;
        if (f < 0.0D) {
            return 0.0D;
        }
        return Math.min(f, 1.0D);
    }

    /** Format a health number without a trailing {@code .0} (so 20.0 -> "20", 6.5 -> "6.5"). */
    private static String trim(double v) {
        if (v == Math.floor(v) && !Double.isInfinite(v)) {
            return Long.toString((long) v);
        }
        // One decimal place is plenty for half-hearts and most modded damage.
        return String.valueOf(Math.round(v * 10.0D) / 10.0D);
    }
}
