package com.sablednah.mobhealth.bukkit.compat;

import java.util.Collection;

import org.bukkit.entity.Player;

/** A boss bar the plugin owns: title, fill, who sees it, and removal. */
public interface BossBarHandle {

    void setTitle(String legacyTitle);

    /** 0..1 */
    void setProgress(double fraction);

    /** Replace the viewer set outright (cheap, and keeps NEARBY audiences right as players move). */
    void setViewers(Collection<? extends Player> viewers);

    /** Hide from everyone and forget. Safe to call twice. */
    void remove();
}
