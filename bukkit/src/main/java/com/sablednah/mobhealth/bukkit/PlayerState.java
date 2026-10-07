package com.sablednah.mobhealth.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import com.sablednah.mobhealth.bukkit.compat.Compat;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Per-player state: the personal mute from {@code /mobhealth toggle}, which has to survive logouts
 * and deaths. Kept in the player's own save data where the server can (1.14+), and in
 * {@code players.yml} in the plugin folder before that.
 */
public final class PlayerState {

    private static final String MUTED = "muted";

    private final Plugin plugin;
    private final Compat compat;
    private final File file;
    private YamlConfiguration yaml;

    public PlayerState(Plugin plugin, Compat compat) {
        this.plugin = plugin;
        this.compat = compat;
        this.file = new File(plugin.getDataFolder(), "players.yml");
    }

    public boolean isMuted(Player player) {
        if (compat.supportsPersistentPlayerData()) {
            Boolean v = compat.playerFlag(player, MUTED);
            return v != null && v;
        }
        return yaml().getBoolean(player.getUniqueId().toString() + "." + MUTED, false);
    }

    public void setMuted(Player player, boolean muted) {
        if (compat.supportsPersistentPlayerData()) {
            compat.setPlayerFlag(player, MUTED, muted);
            return;
        }
        String path = player.getUniqueId().toString() + "." + MUTED;
        if (muted) {
            yaml().set(path, Boolean.TRUE);
        } else {
            yaml().set(player.getUniqueId().toString(), null); // drop the whole section: default is unmuted
        }
        try {
            yaml().save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not save " + file.getName(), e);
        }
    }

    /** May this player receive the per-viewer displays right now? */
    public boolean receivesDisplays(Player player) {
        return !isMuted(player) && player.hasPermission("mobhealth.see");
    }

    private YamlConfiguration yaml() {
        if (yaml == null) {
            yaml = YamlConfiguration.loadConfiguration(file);
        }
        return yaml;
    }
}
