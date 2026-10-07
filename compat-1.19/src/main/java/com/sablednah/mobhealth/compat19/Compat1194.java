package com.sablednah.mobhealth.compat19;

import java.util.Collection;

import com.sablednah.mobhealth.bukkit.compat.AttachedBar;
import com.sablednah.mobhealth.bukkit.compat.FloatingText;
import com.sablednah.mobhealth.compat18.Compat118;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

/**
 * 1.19.4: text display entities. A damage number is now a piece of text that faces the viewer and
 * has no body at all, instead of an armour stand hiding under a name tag.
 */
public class Compat1194 extends Compat118 {

    public Compat1194(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String describe() {
        return "Spigot 1.19.4+ (chat, action bar, nameplates, boss bars, per-viewer text-display damage numbers and floating bars)";
    }

    @Override
    public FloatingText spawnFloatingText(Location at, String legacyText, Collection<? extends Player> viewers) {
        final TextDisplay text = at.getWorld().spawn(at, TextDisplay.class);
        text.setText(legacyText);
        text.setBillboard(Display.Billboard.CENTER);
        text.setSeeThrough(false);
        text.setShadowed(true);
        text.setDefaultBackground(false);
        text.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        glide(text);
        restrictViewers(text, viewers);
        return new FloatingText() {
            @Override
            public void moveTo(Location location) {
                text.teleport(location);
            }

            @Override
            public boolean isValid() {
                return text.isValid();
            }

            @Override
            public void remove() {
                text.remove();
            }
        };
    }

    @Override
    public boolean supportsAttachedBar() {
        return true;
    }

    /**
     * The bar is a text display riding the mob: it follows without the plugin moving it, and the
     * server already hides passengers' text when the mob is out of view. Shifted up by the mob's
     * configured offset: a passenger already sits at the top of the mob.
     */
    @Override
    public AttachedBar attachBar(LivingEntity mob, String legacyText, double offset, Collection<? extends Player> viewers) {
        final TextDisplay text = mob.getWorld().spawn(mob.getLocation(), TextDisplay.class);
        text.setText(legacyText);
        text.setBillboard(Display.Billboard.CENTER);
        text.setSeeThrough(false);
        text.setShadowed(true);
        text.setDefaultBackground(false);
        text.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        float lift = (float) offset;
        text.setTransformation(new Transformation(new Vector3f(0, lift, 0), new AxisAngle4f(), new Vector3f(1, 1, 1), new AxisAngle4f()));
        mob.addPassenger(text);
        restrictViewers(text, viewers);
        return new AttachedBar() {
            @Override
            public void setText(String t) {
                text.setText(t);
            }

            @Override
            public void setViewers(Collection<? extends Player> v) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (v.contains(player)) {
                        player.showEntity(plugin, text);
                    } else {
                        player.hideEntity(plugin, text);
                    }
                }
            }

            @Override
            public boolean isValid() {
                return text.isValid() && text.getVehicle() == mob;
            }

            @Override
            public void remove() {
                text.remove();
            }
        };
    }

    /**
     * 1.20.2+ can interpolate a display's teleport over a tick, so the number glides rather than
     * steps. Compiled against 1.19.4, so looked up by name; absent, the number steps, which at one
     * block a second is barely visible.
     */
    private static void glide(TextDisplay text) {
        try {
            text.getClass().getMethod("setTeleportDuration", int.class).invoke(text, 1);
        } catch (Throwable ignored) {
            // 1.19.4 - 1.20.1
        }
    }
}
