package com.sablednah.mobhealth.bukkit.compat;

import java.util.Collection;

import org.bukkit.entity.Player;

/** A bar riding a mob: text, who sees it, and removal. */
public interface AttachedBar {

    void setText(String legacyText);

    /** Who may see it, where the server can hide entities per player; a no-op before 1.18. */
    void setViewers(Collection<? extends Player> viewers);

    /** False once the server has discarded the entity, or the mob it rode is gone. */
    boolean isValid();

    void remove();
}
