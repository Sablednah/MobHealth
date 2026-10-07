package com.sablednah.mobhealth.core;

/** For ranged hits, which item the toast shows as its icon. */
public enum ProjectileIcon {
    /** The projectile itself — arrow, tipped/spectral arrow, or trident. */
    PROJECTILE,
    /** The weapon that fired it — the bow or crossbow (falls back to the projectile if unknown). */
    WEAPON
}
