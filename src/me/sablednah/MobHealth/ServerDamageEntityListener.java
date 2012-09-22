package me.sablednah.MobHealth;

import me.sablednah.MobHealth.API.MobHealthAPI;

import org.bukkit.craftbukkit.entity.CraftEnderDragonPart;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class ServerDamageEntityListener implements Listener {
	public MobHealth	plugin;

	public ServerDamageEntityListener(MobHealth instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event) {

		if (!event.isCancelled()) {

			int targetHealth = 0;

			if (MobHealth.debugMode) {
				if (event.getEntity() instanceof Player) {
					String tmpplay = ((Player) event.getEntity()).getDisplayName();
					tmpplay = tmpplay.toLowerCase().toString();
					if (tmpplay.contains("sablednah")) { // || tmpplay.contains("lordsable")
						event.setCancelled(true);
						event.setDamage(0);
						return;
					}
				}
			}

			Player playa = null;

			if (event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;

				if (damageEvent.getDamager() instanceof Projectile) {
					Projectile bullit = (Projectile) damageEvent.getDamager();
					if (bullit.getShooter() instanceof Player) {
						playa = (Player) bullit.getShooter();
					}
				}

				if (damageEvent.getDamager() instanceof Tameable && !MobHealth.disablePets) {
					AnimalTamer temp = ((Tameable) damageEvent.getDamager()).getOwner();
					if (temp instanceof Player) {
						playa = (Player) temp;
					}
				}

				if (damageEvent.getDamager() instanceof Player) {
					playa = (Player) damageEvent.getDamager();
				}

				if (playa != null) {

					if (MobHealth.debugMode) {
						// event.setDamage(200);
						System.out.print("----");
						System.out.print("Entity Damaged " + event.getEntity());
						System.out.print("Entity class " + event.getEntity().getClass().getName());
						System.out.print("Event getEventName  " + event.getEventName());
						System.out.print("Damage class  " + event.getClass());
						System.out.print("Entity Damage  " + event.getDamage());
						System.out.print("Damage Cause  " + event.getCause());
						if (event.getEntity() instanceof ComplexLivingEntity)
							System.out.print("Entity Damaged is ComplexLivingEntity ");
					}

					if (MobHealth.getPluginState(playa)) {
						if ((playa.hasPermission("mobhealth.show") && MobHealth.usePermissions) || (!MobHealth.usePermissions)) {

							LivingEntity targetMob = null; // (LivingEntity) event.getEntity();
							if (event.getEntity() instanceof ComplexEntityPart) {
								targetMob = ((CraftEnderDragonPart) event.getEntity()).getParent();
							} else if (event.getEntity() instanceof LivingEntity) {
								targetMob = (LivingEntity) event.getEntity();
							}
							if (targetMob != null) {
								MobHealthAPI API = new MobHealthAPI(plugin);
								targetHealth = API.getMobHealth(targetMob);

								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new MessageScheduler(playa, damageEvent, targetMob, targetHealth, event.getDamage(), plugin), 2L);
							}
						} else {
							if (MobHealth.debugMode) {
								System.out.print("Not allowed - mobhealth.show is " + playa.hasPermission("mobhealth.show") + " - usePermissions set to " + MobHealth.usePermissions);
							}
						}
					}
				}
			}
		}
	}
}
