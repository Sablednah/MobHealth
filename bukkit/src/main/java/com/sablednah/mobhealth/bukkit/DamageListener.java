package com.sablednah.mobhealth.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

/**
 * Watches for player-dealt damage and hands it to the {@link DisplayManager}.
 *
 * <p>Bukkit fires the damage event <em>before</em> the hit lands, and the API this plugin is
 * compiled against (1.7.2) has no {@code getFinalDamage()} to say what armour and enchantments
 * will leave of it. So the hit is measured rather than predicted: the health is noted here and
 * read again one tick later, and the difference is what the mob actually lost. That is also right
 * for absorption, resistance and anything a plugin does to the damage after us.</p>
 */
public final class DamageListener implements Listener {

    private final Plugin plugin;
    private final DisplayManager displays;

    public DamageListener(Plugin plugin, DisplayManager displays) {
        this.plugin = plugin;
        this.displays = displays;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        final Player attacker = attackingPlayer(event.getDamager());
        if (attacker == null) {
            return; // classic MobHealth: only the damage YOU cause
        }
        final LivingEntity victim = EntityCategorizer.livingTarget(event.getEntity());
        if (victim == null || victim == attacker) {
            return;
        }
        final double before = victim.getHealth();
        final double max = victim.getMaxHealth();
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                double after = victim.isDead() ? 0.0D : victim.getHealth();
                double damage = before - after;
                if (damage <= 0.0D) {
                    return; // blocked, absorbed or healed straight back: nothing to report
                }
                displays.onHit(victim, attacker, damage, after, max);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(EntityDeathEvent event) {
        displays.onDeath(event.getEntity());
    }

    /** The player behind a hit: the player, or the player who fired the projectile. */
    private static Player attackingPlayer(Entity damager) {
        if (damager instanceof Player) {
            return (Player) damager;
        }
        if (damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }
        return null;
    }
}
