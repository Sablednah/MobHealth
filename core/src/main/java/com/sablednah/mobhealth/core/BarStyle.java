package com.sablednah.mobhealth.core;

/** Visual style of the graphical floating bar. Loader-agnostic (no Minecraft imports). */
public enum BarStyle {
    /** A plain filled rectangle. */
    SOLID,
    /** A filled rectangle with rounded/clipped corners (pill-like). */
    ROUNDED,
    /** Discrete chunks with small gaps, like a notched health bar. */
    SEGMENTED,
    /** A lens/leaf shape — tallest in the middle, tapering to the ends. */
    TAPERED
}
