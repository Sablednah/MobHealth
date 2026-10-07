package com.sablednah.mobhealth.compat18;

import java.util.Collection;

import com.sablednah.mobhealth.compat14.Compat114;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * 1.18: {@code Player.hideEntity}, so a damage number spawned for one player is invisible to the
 * rest. Before this the number is a real entity everyone nearby can see, which is why the audience
 * setting only fully applies from here.
 */
public class Compat118 extends Compat114 {

    public Compat118(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String describe() {
        return "Spigot 1.18 (chat, action bar, nameplates, boss bars, per-viewer armour-stand damage numbers)";
    }

    @Override
    public boolean perViewerEntities() {
        return true;
    }

    @Override
    protected void restrictViewers(Entity entity, Collection<? extends Player> viewers) {
        // Compiled against 1.18, where getOnlinePlayers() is a Collection; safe in this module.
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!viewers.contains(player)) {
                player.hideEntity(plugin, entity);
            }
        }
    }
}
