package me.sablednah.MobHealth;

import me.sablednah.zombiemod.PutredineImmortui;
import me.sablednah.zombiemod.ZombieMod;

import org.bukkit.craftbukkit.entity.CraftEnderDragonPart;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import cam.Likeaboss;
import cam.boss.Boss;
import cam.boss.BossManager;


import com.garbagemule.MobArena.MobArenaHandler;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.*;
import com.herocraftonline.heroes.characters.CharacterTemplate;


public class HeroesEventListener implements Listener {
	public MobHealth plugin;
	
	public HeroesEventListener(MobHealth instance) {
		this.plugin=instance;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void WeaponDamageEvent(WeaponDamageEvent event){

		if (!event.isCancelled()) {
			
			int targetHealth=0;
			
			if (MobHealth.debugMode) {
//				event.setDamage(200);
				System.out.print("----");
				System.out.print("Entity Damaged " + event.getEntity());
				System.out.print("Entity getEventName  " + event.getEventName());
				System.out.print("Entity Damage class  " + event.getClass());
				System.out.print("Entity Damage  " + event.getDamage());
				if (event.getEntity() instanceof ComplexLivingEntity) System.out.print("Entity Damaged is ComplexLivingEntity ");
			}

			
			Player playa = null;
			
			if(event instanceof WeaponDamageEvent) {
				WeaponDamageEvent damageEvent = event;

				if (MobHealth.debugMode) {
					System.out.print("damageEvent.getDamager()  " + damageEvent.getDamager());
				}
				
				if(damageEvent.getDamager() instanceof Projectile) {
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
					if(MobHealth.getPluginState(playa)){	
						if((playa.hasPermission("mobhealth.show") && MobHealth.usePermissions ) || (!MobHealth.usePermissions) ) {

							LivingEntity targetMob = null;
							
							if (event.getEntity() instanceof ComplexEntityPart) {
								targetMob = ((ComplexEntityPart) event.getEntity()).getParent();
							} else if (event.getEntity() instanceof LivingEntity) {
								targetMob = (LivingEntity) event.getEntity();
							}
							if (targetMob!=null) {
								targetHealth=targetMob.getHealth();

								Heroes heroes = (Heroes) plugin.getServer().getPluginManager().getPlugin("Heroes");
								if(heroes != null)  {
									targetHealth=heroes.getDamageManager().getHealth(targetMob);
								}
								
								if (MobHealth.hasZM) {
									ZombieMod ZM=(ZombieMod) plugin.getServer().getPluginManager().getPlugin("ZombieMod");
									PutredineImmortui zomb = ZM.getZombie((Entity) targetMob);
									if (zomb != null) {
										targetHealth=zomb.health;
									}
								}
								
								if (MobHealth.hasLikeABoss) {
									Likeaboss LaB=(Likeaboss) plugin.getServer().getPluginManager().getPlugin("Likeaboss");
									BossManager BM=LaB.getBossManager();
									if(BM != null)  {
										Boss thisBoss = BM.getBoss(targetMob);
										if(thisBoss != null)  {
											targetHealth=thisBoss.getHealth();
										}
									}
								} 
								
								if (MobHealth.hasMobArena) {
									MobArenaHandler maHandler = new MobArenaHandler();
									Arena arena = maHandler.getArenaWithPlayer(playa);
									if (arena !=null) {
										MABoss thisBoss = arena.getMonsterManager().getBoss(targetMob);
										if (thisBoss != null) {
											targetHealth=thisBoss.getHealth();
										}
									}
								}
								
								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new MessageScheduler(playa, damageEvent, targetMob, targetHealth, event.getDamage(),plugin), 2L);
							}
						} else {
							if (MobHealth.debugMode) {
								System.out.print("Not allowed - mobhealth.show is "+playa.hasPermission("mobhealth.show")+" - usePermissions set to "+MobHealth.usePermissions);
							}
						}
					}
				} 
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void SkillDamageEvent(SkillDamageEvent event){

		if (!event.isCancelled()) {
			
			int targetHealth=0;
			
			if (MobHealth.debugMode) {
//				event.setDamage(200);
				System.out.print("----");
				System.out.print("Entity Damaged " + event.getEntity());
				System.out.print("Entity getEventName  " + event.getEventName());
				System.out.print("Entity Damage class  " + event.getClass());
				System.out.print("Entity Damage  " + event.getDamage());
				if (event.getEntity() instanceof ComplexLivingEntity) System.out.print("Entity Damaged is ComplexLivingEntity ");
			}

			
			Player playa = null;
			
			if(event instanceof SkillDamageEvent) {
				SkillDamageEvent damageEvent = event;

				if (damageEvent.getDamager().getEntity() instanceof Player) {
					playa = (Player) damageEvent.getDamager().getEntity();
				}
				
				System.out.print("playa - " + playa);
				
				if (playa != null) {	
					if(MobHealth.getPluginState(playa)){	
						if((playa.hasPermission("mobhealth.show") && MobHealth.usePermissions ) || (!MobHealth.usePermissions) ) {

							LivingEntity targetMob = null;
							if (event.getEntity() instanceof ComplexEntityPart) {
								targetMob = ((CraftEnderDragonPart) event.getEntity()).getParent();
							} else if (event.getEntity() instanceof LivingEntity) {
								targetMob = (LivingEntity) event.getEntity();
							}
							targetHealth=targetMob.getHealth();

							Heroes heroes = (Heroes) plugin.getServer().getPluginManager().getPlugin("Heroes");
							if(heroes != null)  {
								targetHealth=heroes.getDamageManager().getHealth(targetMob);
							}
							
							plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new SkillMessageScheduler(playa, damageEvent, targetMob, targetHealth, event.getDamage(),plugin), 1L);

						} else {
							if (MobHealth.debugMode) {
								System.out.print("Not allowed - mobhealth.show is "+playa.hasPermission("mobhealth.show")+" - usePermissions set to "+MobHealth.usePermissions);
							}
						}
					}
				} 
			}
		}
	}
	
	
}