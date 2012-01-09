package me.sablednah.MobHealth;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MessageScheduler implements Runnable {
	private Player player;
	private EntityDamageByEntityEvent damageEvent;
	private LivingEntity targetMob;
	public MessageScheduler(EntityDamageByEntityEvent damageEvent, LivingEntity targetMob) {
		//this.player = player;
		
		this.damageEvent=damageEvent;
		this.player = (Player) damageEvent.getDamager();
		this.setTargetMob(targetMob);
	}
	public void run() {

		int thisDamange=0;
        int mobsHealth=0; 
        int mobsMaxHealth=0;
        //int mobID;
        
        thisDamange=damageEvent.getDamage();
        mobsMaxHealth = targetMob.getMaxHealth();
        mobsHealth = targetMob.getHealth();
        //mobID=targetMob.getEntityId();

        String mobtype = new String(targetMob.getClass().getName());

        if (mobtype.indexOf("org.bukkit.craftbukkit.entity.Craft") == -1) {
        	mobtype="unKn0wn";
        } else {
        	mobtype=mobtype.replaceAll("org.bukkit.craftbukkit.entity.Craft", "");
        }
        
        if (targetMob.isDead()) {
        	player.sendMessage((ChatColor.WHITE) + mobtype+" took " + thisDamange + " damage. " + (ChatColor.RED) + "Killed.");
//        	targetMob.remove();
        } else {
	        if ((mobsHealth<2) || (mobsHealth<=(mobsMaxHealth/4)) ) {
	        	player.sendMessage((ChatColor.WHITE) + mobtype+" took " + thisDamange + " damage. " + (ChatColor.RED) + mobsHealth + (ChatColor.WHITE) + " / "+mobsMaxHealth + " health.");
	        } else {
	        	player.sendMessage((ChatColor.WHITE) + mobtype+" took " + thisDamange + " damage. " + mobsHealth + " / "+mobsMaxHealth + " health.");
	        }
        }
	}
	
	// Optional, if you need it
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public LivingEntity getTargetMob() {
		return targetMob;
	}
	public void setTargetMob(LivingEntity targetMob) {
		this.targetMob = targetMob;
	}
	public EntityDamageByEntityEvent getdamageEvent() {
		return damageEvent;
	}
	public void setdamageEvent(EntityDamageByEntityEvent damageEvent) {
		this.damageEvent = damageEvent;
	}
}