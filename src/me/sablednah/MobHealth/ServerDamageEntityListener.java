package me.sablednah.MobHealth;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityListener;

public class ServerDamageEntityListener extends EntityListener  {
	public MobHealth plugin;

	public final Logger logger = Logger.getLogger("Minecraft");

	public ServerDamageEntityListener(MobHealth instance) {
		this.plugin=instance;
	}
	
	public void onEntityDamage(EntityDamageByEntityEvent event){
		Entity targetMob = event.getEntity();
		Entity sourceMob = event.getDamager();
		
		int mobsHealth = 0;
		
		ChatColor BLUE = ChatColor.BLUE;
		ChatColor WHITE = ChatColor.WHITE;

		this.logger.info("[" + pdfFile.getName() + "] " + event.getDamage() + " damage.");

		if(sourceMob instanceof Player){
			
		//If the entity is being damaged by a player...
			Player p = (Player) event.getDamager();
			p.sendMessage(BLUE + "[MobHealth] " + WHITE + event.getDamage() + " damage.");

			mobsHealth = targetMob.getHealth();
			p.sendMessage(BLUE + "[MobHealth] " + WHITE + mobsHealth + " health.");
			
		}
	}


	
//		Player p = chat.getPlayer();
//		p.getName() 
//		p.sendMessage(BLUE + "[MobHealth] " + WHITE + "That's ridiculous, it's not even funny.");

//		String message = chat.getMessage();
//		String message_lower = message.toLowerCase();

//		if(message_lower.contains("11") || message_lower.contains("eleven")) {
//			ChatColor BLUE = ChatColor.BLUE;
//			ChatColor WHITE = ChatColor.WHITE;
//			plugin.getServer().broadcastMessage(WHITE + "That's rediculous, it's not even funny.");
//			chat.setCancelled(true);
//		}
		
}
