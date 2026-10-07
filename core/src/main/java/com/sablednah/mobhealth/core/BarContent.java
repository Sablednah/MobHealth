package com.sablednah.mobhealth.core;

/** What a display shows: the bar, the numbers, or both. */
public enum BarContent {
    /** Just the segmented bar. */
    BAR,
    /** Just the numeric readout (e.g. 14/20 or 70%). */
    NUMBERS,
    /** Bar followed by numbers. */
    BOTH
}
