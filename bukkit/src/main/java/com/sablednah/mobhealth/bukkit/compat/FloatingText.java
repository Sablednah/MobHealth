package com.sablednah.mobhealth.bukkit.compat;

import org.bukkit.Location;

/** A spawned piece of floating text (a damage number) the plugin moves each tick and then removes. */
public interface FloatingText {

    void moveTo(Location location);

    /** False once the server has discarded the entity (chunk unload, /kill, ...). */
    boolean isValid();

    /** Despawn. Safe to call twice. */
    void remove();
}
