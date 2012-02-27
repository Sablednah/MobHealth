package me.sablednah.MobHealth;

import org.bukkit.craftbukkit.entity.CraftEnderDragonPart;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.garbagemule.MobArena.MobArenaHandler;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;

import cam.Likeaboss;
import cam.boss.BossManager;
import cam.boss.Boss;


public class ServerDamageEntityListener implements Listener {
	public MobHealth plugin;

	public ServerDamageEntityListener(MobHealth instance) {
		this.plugin=instance;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event){
		
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
			
			if(event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;

				if(damageEvent.getDamager() instanceof Projectile) {
					Projectile bullit = (Projectile) damageEvent.getDamager();
					if (bullit.getShooter() instanceof Player) {
						playa = (Player) bullit.getShooter();
					}
				}
				
				if (damageEvent.getDamager() instanceof Player) {
					playa = (Player) damageEvent.getDamager();
				}
				
				if (playa != null) {	
					if(MobHealth.getPluginState(playa)){	
						if((playa.hasPermission("mobhealth.show") && MobHealth.usePermissions ) || (!MobHealth.usePermissions) ) {

							LivingEntity targetMob = null; //(LivingEntity) event.getEntity();
							if (event.getEntity() instanceof ComplexEntityPart) {
								targetMob = ((CraftEnderDragonPart) event.getEntity()).getParent();
							} else if (event.getEntity() instanceof LivingEntity) {
								targetMob = (LivingEntity) event.getEntity();
							}
							targetHealth=targetMob.getHealth();
							

							if (MobHealth.hasLikeABoss) {
								Likeaboss LaB=(Likeaboss) plugin.getServer().getPluginManager().getPlugin("Likeaboss");
								BossManager BM=LaB.getBossManager();
								if(BM != null)  {
									Boss thisBoss = BM.getBoss(targetMob);
									if(thisBoss != null)  {
										targetHealth=thisBoss.getHealth();
									}
								}
							} else if (MobHealth.hasMobArena) {
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

