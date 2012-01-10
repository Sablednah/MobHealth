package me.sablednah.MobHealth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class ServerChatPlayerListener extends PlayerListener  {
	public MobHealth plugin;
	
	public ServerChatPlayerListener(MobHealth instance) {
		this.plugin=instance;
	}
	
	public void onPlayerChat(PlayerChatEvent chat) {
		String message = chat.getMessage();
		String message_lower = message.toLowerCase();

		ChatColor BLUE = ChatColor.BLUE;
		ChatColor WHITE = ChatColor.WHITE;
		
		if (MobHealth.enableEasterEggs) {	
			if(message_lower.contains("11") || message_lower.contains("eleven")) {
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				    public void run() {
						ChatColor WHITE = ChatColor.WHITE;
				    	plugin.getServer().broadcastMessage(WHITE + "That's rediculous, it's not even funny.");
				    }
				}, 2L);
			}
			if(message_lower.contains("shit") || message_lower.contains("fuck") || message_lower.contains("cunt") || message_lower.contains("piss")) {
				Player p = chat.getPlayer();
				p.sendMessage(BLUE + "[MobHealth] " + WHITE + "Oi " + p.getName() + "!!  Mind your language!");
				chat.setCancelled(true);
			}
		}
	}
}

