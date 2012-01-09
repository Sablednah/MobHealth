package me.sablednah.MobHealth;

import java.util.logging.Logger;

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
	
	public final Logger logger = Logger.getLogger("Minecraft");

    public void onEntityDamage(EntityDamageEvent event){
        
    	//System.out.print("Entity Defender: " + event.getEntity().getEntityId());
        
        if(event instanceof EntityDamageByEntityEvent) 
        {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
            //System.out.print("Entity Damager " + damageEvent.getDamager().getEntityId());
            if (damageEvent.getDamager() instanceof Player)
            {
                Player attacker = (Player) damageEvent.getDamager();
                LivingEntity targetMob = (LivingEntity) event.getEntity();
                
                int mobsHealth=0; 
                int mobsMaxHealth=0;
                int thisDamange=0;
                thisDamange=event.getDamage();
                
//              attacker.sendMessage(thisDamange + " dammage.");
                
                mobsMaxHealth = targetMob.getMaxHealth();
                mobsHealth = targetMob.getHealth();
                attacker.sendMessage(thisDamange + " damage." + (mobsHealth - thisDamange) + " / "+mobsMaxHealth+" " + "health.");
               
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new MessageScheduler(attacker,thisDamange), 2L);
                
                
            }
        }
        
    }	
	

}
