package com.sablednah.mobhealth.compat14;

import com.sablednah.mobhealth.compat9.Compat111;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * 1.14: entities have registry keys, so overrides can be written as {@code minecraft:zombified_piglin}
 * exactly as on Forge; and entities and players have persistent data containers, so the
 * "we control this name tag" flag and the per-player mute survive a restart.
 */
public class Compat114 extends Compat111 {

    public Compat114(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String describe() {
        return "Spigot 1.14 (chat, action bar, nameplates, boss bars, armour-stand damage numbers, persistent flags)";
    }

    @Override
    public String entityKey(Entity entity) {
        try {
            return entity.getType().getKey().toString();
        } catch (Throwable t) {
            return super.entityKey(entity); // UNKNOWN / a type with no key on some fork
        }
    }

    @Override
    public boolean flag(Entity entity, String key) {
        Byte v = entity.getPersistentDataContainer().get(key(key), PersistentDataType.BYTE);
        return v != null && v != 0;
    }

    @Override
    public void setFlag(Entity entity, String key, boolean value) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (value) {
            pdc.set(key(key), PersistentDataType.BYTE, (byte) 1);
        } else {
            pdc.remove(key(key));
        }
    }

    @Override
    public boolean supportsPersistentPlayerData() {
        return true;
    }

    @Override
    public Boolean playerFlag(Player player, String key) {
        Byte v = player.getPersistentDataContainer().get(key(key), PersistentDataType.BYTE);
        return v == null ? null : Boolean.valueOf(v != 0);
    }

    @Override
    public void setPlayerFlag(Player player, String key, boolean value) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (value) {
            pdc.set(key(key), PersistentDataType.BYTE, (byte) 1);
        } else {
            pdc.remove(key(key));
        }
    }

    private NamespacedKey key(String key) {
        return new NamespacedKey(plugin, key);
    }
}
