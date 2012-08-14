package me.sablednah.MobHealth;

import me.sablednah.MobHealth.API.MobHealthAPI;

import org.bukkit.craftbukkit.entity.CraftEnderDragonPart;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.herocraftonline.heroes.api.events.*;
import com.herocraftonline.heroes.characters.CharacterTemplate;

public class HeroesEventListener implements Listener {
	public MobHealth	plugin;

	public HeroesEventListener(MobHealth instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void WeaponDamageEvent(WeaponDamageEvent event) {

		if (!event.isCancelled()) {

			int targetHealth = 0;

			if (MobHealth.debugMode) {
				// event.setDamage(200);
				System.out.print("----");
				System.out.print("Entity Damaged " + event.getEntity());
				System.out.print("Entity getEventName  " + event.getEventName());
				System.out.print("Entity Damage class  " + event.getClass());
				System.out.print("Entity Damage  " + event.getDamage());
				if (event.getEntity() instanceof ComplexLivingEntity)
					System.out.print("Entity Damaged is ComplexLivingEntity ");
			}

			Player playa = null;

			if (event instanceof WeaponDamageEvent) {
				WeaponDamageEvent damageEvent = event;

				if (MobHealth.debugMode) {
					System.out.print("damageEvent.getDamager()  " + damageEvent.getDamager());
				}

				if (damageEvent.getDamager() instanceof Projectile) {
					Projectile bullit = (Projectile) damageEvent.getDamager();
					if (bullit.getShooter() instanceof Player) {
						playa = (Player) bullit.getShooter();
					}
				}

				if (damageEvent.getDamager() instanceof Player) {
					playa = (Player) damageEvent.getDamager();
				}
				if (damageEvent.getDamager() instanceof CharacterTemplate) {
					if (damageEvent.getDamager().getEntity() instanceof Player) {
						playa = (Player) damageEvent.getDamager().getEntity();
					}
				}

				if (MobHealth.debugMode) {
					System.out.print("playa  " + playa);
				}

				if (playa != null) {
					if (MobHealth.getPluginState(playa)) {
						if ((playa.hasPermission("mobhealth.show") && MobHealth.usePermissions) || (!MobHealth.usePermissions)) {

							LivingEntity targetMob = null;

							if (event.getEntity() instanceof ComplexEntityPart) {
								targetMob = ((ComplexEntityPart) event.getEntity()).getParent();
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

	@EventHandler(priority = EventPriority.LOWEST)
	public void SkillDamageEvent(SkillDamageEvent event) {

		if (!event.isCancelled()) {

			int targetHealth = 0;

			if (MobHealth.debugMode) {
				// event.setDamage(200);
				System.out.print("----");
				System.out.print("Entity Damaged " + event.getEntity());
				System.out.print("Entity getEventName  " + event.getEventName());
				System.out.print("Entity Damage class  " + event.getClass());
				System.out.print("Entity Damage  " + event.getDamage());
				if (event.getEntity() instanceof ComplexLivingEntity)
					System.out.print("Entity Damaged is ComplexLivingEntity ");
			}

			Player playa = null;

			if (event instanceof SkillDamageEvent) {
				SkillDamageEvent damageEvent = event;

				if (damageEvent.getDamager().getEntity() instanceof Player) {
					playa = (Player) damageEvent.getDamager().getEntity();
				}

				System.out.print("playa - " + playa);

				if (playa != null) {
					if (MobHealth.getPluginState(playa)) {
						if ((playa.hasPermission("mobhealth.show") && MobHealth.usePermissions) || (!MobHealth.usePermissions)) {

							LivingEntity targetMob = null;
							if (event.getEntity() instanceof ComplexEntityPart) {
								targetMob = ((CraftEnderDragonPart) event.getEntity()).getParent();
							} else if (event.getEntity() instanceof LivingEntity) {
								targetMob = (LivingEntity) event.getEntity();
							}

							if (targetMob != null) {
								MobHealthAPI API = new MobHealthAPI(plugin);
								targetHealth = API.getMobHealth(targetMob);

								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new SkillMessageScheduler(playa, damageEvent, targetMob, targetHealth, event.getDamage(), plugin), 1L);
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