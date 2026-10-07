package com.sablednah.mobhealth.core;

/** Who receives the targetable displays (chat / boss bar / graphical). */
public enum Audience {
    /** Only the player who dealt the hit (classic MobHealth behaviour). */
    ATTACKER,
    /** Every player within range who can see the mob. */
    NEARBY
}
