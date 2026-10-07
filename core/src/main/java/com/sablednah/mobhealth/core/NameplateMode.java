package com.sablednah.mobhealth.core;

/** How the nameplate (name-tag) health bar behaves. */
public enum NameplateMode {
    /** Show a bar briefly after each hit, then restore the mob's original name. */
    ON_DAMAGE,
    /** Only augment mobs that already have a custom name; restore it after the timeout. */
    NAMED_ONLY,
    /** Show a bar after a hit and keep it (updated on each hit); do not revert. */
    ALWAYS
}
