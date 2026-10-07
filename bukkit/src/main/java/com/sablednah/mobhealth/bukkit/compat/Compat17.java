package com.sablednah.mobhealth.bukkit.compat;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

/**
 * The floor: what Bukkit 1.7.2-R0.3 offers with no help. Chat and nameplates work; there is no
 * action bar, no boss bar, no spare entity worth using as floating text, and entity flags live in
 * the (non-persistent) metadata store. Every later adapter extends this and overrides what its API
 * added.
 */
public class Compat17 implements Compat {

    protected final Plugin plugin;

    public Compat17(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String describe() {
        return "Bukkit 1.7 (chat, nameplates)";
    }

    @Override
    public boolean supportsActionBar() {
        return false;
    }

    @Override
    public void sendActionBar(Player player, String legacyText) {
        // Nothing to send it with before 1.8.
    }

    @Override
    public boolean supportsBossBar() {
        return false;
    }

    @Override
    public BossBarHandle createBossBar(String title, String colourName) {
        return null;
    }

    @Override
    public boolean supportsFloatingText() {
        return false;
    }

    @Override
    public FloatingText spawnFloatingText(Location at, String legacyText, Collection<? extends Player> viewers) {
        return null;
    }

    @Override
    public boolean perViewerEntities() {
        return false;
    }

    @Override
    public String entityKey(Entity entity) {
        return "minecraft:" + entity.getType().name().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean flag(Entity entity, String key) {
        List<MetadataValue> values = entity.getMetadata(metaKey(key));
        for (MetadataValue value : values) {
            if (value.getOwningPlugin() == plugin) {
                return value.asBoolean();
            }
        }
        return false;
    }

    @Override
    public void setFlag(Entity entity, String key, boolean value) {
        if (value) {
            entity.setMetadata(metaKey(key), new FixedMetadataValue(plugin, Boolean.TRUE));
        } else {
            entity.removeMetadata(metaKey(key), plugin);
        }
    }

    @Override
    public boolean supportsPersistentPlayerData() {
        return false;
    }

    @Override
    public Boolean playerFlag(Player player, String key) {
        return null; // PlayerState keeps its own file instead
    }

    @Override
    public void setPlayerFlag(Player player, String key, boolean value) {
        // see playerFlag
    }

    protected static String metaKey(String key) {
        return "mobhealth_" + key;
    }
}
