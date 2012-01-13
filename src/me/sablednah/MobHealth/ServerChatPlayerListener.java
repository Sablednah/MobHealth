package me.sablednah.MobHealth;

import java.util.Iterator;

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
				    	plugin.getServer().broadcastMessage(WHITE + MobHealth.eleven);
				    }
				}, 2L);
			}

			Iterator<String> iter = MobHealth.langProfanity.iterator();
            while (iter.hasNext()) {
                String swear;
                swear = (iter.next());
    			if(message_lower.contains(swear)) {
    				Player p = chat.getPlayer();
    				String outMessage;
    				outMessage=MobHealth.profanityMessage.replaceAll("%N", p.getName());
    				
    				p.sendMessage(BLUE + "[MobHealth] " + WHITE + outMessage);
    				chat.setCancelled(true);
    				break;
    			}               
            }
			

		}
	}
}

