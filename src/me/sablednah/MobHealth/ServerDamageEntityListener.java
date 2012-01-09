package me.sablednah.MobHealth;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class ServerDamageEntityListener extends EntityListener  {
	public MobHealth plugin;
	
	public ServerDamageEntityListener(MobHealth instance) {
		this.plugin=instance;
	}
	
    public void onEntityDamage(EntityDamageEvent event){
        
    	//System.out.print("Entity Defender: " + event.getEntity().getEntityId());
        
        if(event instanceof EntityDamageByEntityEvent) 
        {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
            //System.out.print("Entity Damager " + damageEvent.getDamager().getEntityId());
            if (damageEvent.getDamager() instanceof Player)
            {
                LivingEntity targetMob = (LivingEntity) event.getEntity();
                 plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new MessageScheduler(damageEvent, targetMob), 1L);
             }
        }
    }	
}

