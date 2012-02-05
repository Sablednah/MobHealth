package me.sablednah.MobHealth;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.garbagemule.MobArena.Arena;
import com.garbagemule.MobArena.MobArenaHandler;
import com.herocraftonline.dev.heroes.Heroes;


import cam.Likeaboss;
import cam.boss.BossManager;
import cam.boss.Boss;


public class ServerDamageEntityListener implements Listener {
	public MobHealth plugin;

	public ServerDamageEntityListener(MobHealth instance) {
		this.plugin=instance;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamage(EntityDamageEvent event){
		
		if (!event.isCancelled()) {
			
			int targetHealth=0;
			
			if (event.getEntity()  instanceof Player) {
				String tmpplay=((Player) event.getEntity()).getDisplayName();
				tmpplay=tmpplay.toLowerCase().toString();
				if (tmpplay.contains("sablednah")) { // || tmpplay.contains("lordsable")
					event.setCancelled(true); event.setDamage(0);return;
				}
			}
			
//			event.setDamage(200);
//			System.out.print("----");
//			System.out.print("Entity Damaged " + event.getEntity());
//			System.out.print("Entity Damage type  " + event.getType());
//			System.out.print("Entity Damage class  " + event.getClass());
//			System.out.print("Entity Damage  " + event.getDamage());
//			if (event.getEntity() instanceof ComplexLivingEntity) System.out.print("Entity Damaged is ComplexLivingEntity ");
			
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
						if((playa.hasPermission("MobHealth.show") && MobHealth.usePermissions ) || (!MobHealth.usePermissions) ) {

							LivingEntity targetMob = (LivingEntity) event.getEntity();
							
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
									if (arena.isBossWave()) {
										if (targetMob instanceof LivingEntity && maHandler.isMonsterInArena(targetMob)) {
											targetHealth=MobHealth.maBossHealthMax;
										} 
									}
								}
							} else if (MobHealth.hasHeroes) { //I need a Hero!
								Heroes heroes = (Heroes) plugin.getServer().getPluginManager().getPlugin("Heroes");
								if(heroes != null)  {
									targetHealth=heroes.getDamageManager().getEntityHealth(targetMob);
								}
							}
							
							plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new MessageScheduler(playa, damageEvent, targetMob, targetHealth, event.getDamage(),plugin), 1L);

						} else {
							System.out.print("Not allowed - "+playa.hasPermission("MobHealth.show")+" "+MobHealth.usePermissions);
						}
					}
				} 
			}
		}
	}	
}

