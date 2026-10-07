package com.sablednah.mobhealth.compat8;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.logging.Level;

import com.sablednah.mobhealth.bukkit.compat.Compat17;
import com.sablednah.mobhealth.bukkit.compat.FloatingText;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * 1.8: armour stands exist, so a floating damage number can be an invisible one with a name; and
 * the action bar exists, though the API will not say so until 1.11 — it is sent as the raw packet.
 *
 * <p>The packet is built by reflection against whatever {@code net.minecraft.server.v1_X_RY} the
 * server carries, as the 9.x plugin did per version by hand. If any step fails (an unexpected
 * fork), the action bar is reported unsupported rather than failing every hit.</p>
 */
public class Compat18 extends Compat17 {

    private final ActionBarPacket actionBar;

    public Compat18(Plugin plugin) {
        super(plugin);
        ActionBarPacket packet;
        try {
            packet = new ActionBarPacket();
        } catch (Throwable t) {
            plugin.getLogger().log(Level.FINE, "No NMS action bar on this server: " + t);
            packet = null;
        }
        this.actionBar = packet;
    }

    @Override
    public String describe() {
        return "Bukkit 1.8 (chat, nameplates, armour-stand damage numbers"
                + (actionBar != null ? ", NMS action bar)" : "; no action bar)");
    }

    // ------------------------------------------------------------------ action bar

    @Override
    public boolean supportsActionBar() {
        return actionBar != null;
    }

    @Override
    public void sendActionBar(Player player, String legacyText) {
        if (actionBar != null) {
            actionBar.send(player, legacyText);
        }
    }

    /** {@code PacketPlayOutChat(IChatBaseComponent, byte 2)} via {@code CraftPlayer.getHandle().playerConnection.sendPacket}. */
    private static final class ActionBarPacket {
        private final Method serialize;
        private final Constructor<?> packet;
        private final Method getHandle;
        private final Field connection;
        private final Method sendPacket;

        ActionBarPacket() throws Exception {
            String pkg = Bukkit.getServer().getClass().getPackage().getName(); // org.bukkit.craftbukkit.v1_8_R3
            String nms = "net.minecraft.server." + pkg.substring(pkg.lastIndexOf('.') + 1) + ".";
            Class<?> component = Class.forName(nms + "IChatBaseComponent");
            serialize = Class.forName(nms + "IChatBaseComponent$ChatSerializer").getMethod("a", String.class);
            packet = Class.forName(nms + "PacketPlayOutChat").getConstructor(component, byte.class);
            getHandle = Class.forName(pkg + ".entity.CraftPlayer").getMethod("getHandle");
            connection = Class.forName(nms + "EntityPlayer").getField("playerConnection");
            sendPacket = Class.forName(nms + "PlayerConnection").getMethod("sendPacket", Class.forName(nms + "Packet"));
        }

        void send(Player player, String legacyText) {
            try {
                Object component = serialize.invoke(null, "{\"text\":\"" + json(legacyText) + "\"}");
                Object chat = packet.newInstance(component, (byte) 2);
                Object handle = getHandle.invoke(player);
                sendPacket.invoke(connection.get(handle), chat);
            } catch (Throwable ignored) {
                // A player mid-disconnect; nothing to do.
            }
        }

        private static String json(String s) {
            StringBuilder sb = new StringBuilder(s.length() + 8);
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                    case '"':  sb.append("\\\""); break;
                    case '\\': sb.append("\\\\"); break;
                    case '\n': sb.append("\\n"); break;
                    default:
                        if (c < 0x20) {
                            sb.append(String.format("\\u%04x", (int) c));
                        } else {
                            sb.append(c);
                        }
                }
            }
            return sb.toString();
        }
    }

    // ------------------------------------------------------------------ floating text

    @Override
    public boolean supportsFloatingText() {
        return true;
    }

    @Override
    public FloatingText spawnFloatingText(Location at, String legacyText, Collection<? extends Player> viewers) {
        final ArmorStand stand = (ArmorStand) at.getWorld().spawnEntity(at, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setSmall(true);
        stand.setCustomName(legacyText);
        stand.setCustomNameVisible(true);
        try {
            stand.setMarker(true);    // no hitbox, so the next swing hits the mob and not the number
            stand.setBasePlate(false);
        } catch (Throwable ignored) {
            // setMarker is 1.8.3+; on 1.8.0 the stand merely has a hitbox for a second
        }
        restrictViewers(stand, viewers);
        return new FloatingText() {
            @Override
            public void moveTo(Location location) {
                stand.teleport(location);
            }

            @Override
            public boolean isValid() {
                return stand.isValid();
            }

            @Override
            public void remove() {
                stand.remove();
            }
        };
    }

    /** Hook for 1.18+, where an entity can be hidden from the players who should not see it. */
    protected void restrictViewers(org.bukkit.entity.Entity entity, Collection<? extends Player> viewers) {
        // Not possible before 1.18: everyone nearby sees the number.
    }
}
