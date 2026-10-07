package com.sablednah.mobhealth.core;

/**
 * The classic MobHealth entity groupings. The loader adapter maps a concrete entity to one of
 * these; the config toggles visibility per group (with per-entity overrides on top).
 *
 * <p>Loader-agnostic (no Minecraft imports).
 */
public enum MobCategory {
    /** Hostile monsters (zombies, skeletons, creepers, ...). */
    HOSTILE,
    /** Neutral mobs — passive until provoked (endermen, wolves, bees, ...). */
    NEUTRAL,
    /** Passive animals (cows, sheep, chickens, ...). */
    PASSIVE,
    /** Other players (PvP). */
    PLAYER,
    /** Anything that doesn't fit the above (e.g. armor stands, unknown modded entities). */
    OTHER
}
