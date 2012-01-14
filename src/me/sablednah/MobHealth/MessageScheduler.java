package me.sablednah.MobHealth;

import org.bukkit.Material;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import org.getspout.spoutapi.SpoutManager;

public class MessageScheduler implements Runnable {
	private Player player;
	private EntityDamageByEntityEvent damageEvent;
	private LivingEntity targetMob;
	public MobHealth plugin;
	
	public MessageScheduler(Player shooter, EntityDamageByEntityEvent damageEvent, LivingEntity targetMob, MobHealth plugin) {
		//this.player = player;
		this.plugin = plugin;
		this.damageEvent = damageEvent;
		this.player = shooter;
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
        	if (targetMob instanceof Player) {
        		mobtype=((Player) targetMob).getDisplayName();
        	} else {
            	System.out.print("[MobHealth] " + mobtype +" unknown.");
            	mobtype="unKn0wn";
        	}
        } else {
        	mobtype=mobtype.replaceAll("org.bukkit.craftbukkit.entity.Craft", "");
        	if (MobHealth.entityLookup.get(mobtype) != null) {
        		mobtype=MobHealth.entityLookup.get(mobtype);
        	}
        }

        Boolean spoutUsed=false;
        
        if (!MobHealth.disableSpout) {
			if(player.getServer().getPluginManager().isPluginEnabled("Spout")) {
				if(SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
					String title =  plugin.getLangConfig().getString("spoutDamageTitle");
					title=title.replaceAll("%D",Integer.toString(thisDamange));
					title=title.replaceAll("%N",mobtype);
					title=title.replaceAll("%M",Integer.toString(mobsMaxHealth));
					

					for (int chatcntr = 0;chatcntr<16;chatcntr++){
						title=title.replaceAll("&"+Integer.toHexString(chatcntr),(ChatColor.getByCode(chatcntr))+"");
					}

					
					String message = "";
					
			        if (targetMob.isDead()) {
			        	message =  plugin.getLangConfig().getString("spoutKilledMessage");
			        } else {
			        	message =  plugin.getLangConfig().getString("spoutDamageMessage");
				        if ((mobsHealth<2) || (mobsHealth<=(mobsMaxHealth/4)) ) {
				        	message=message.replaceAll("%H",(ChatColor.DARK_RED) + Integer.toString(mobsHealth) + (ChatColor.WHITE));
				        } else {
				        	message=message.replaceAll("%H",Integer.toString(mobsHealth));
				        }
			        }
					for (int chatcntr2 = 0;chatcntr2<16;chatcntr2++){
						message=message.replaceAll("&"+Integer.toHexString(chatcntr2),(ChatColor.getByCode(chatcntr2))+"");
					}
			        message=message.replaceAll("%D",Integer.toString(thisDamange));
					message=message.replaceAll("%N",mobtype);
					message=message.replaceAll("%M",Integer.toString(mobsMaxHealth));			        
					try {
						spoutUsed=true;
						Material icon;
						if (damageEvent.getDamager() instanceof Projectile) {
							icon = Material.getMaterial(261);
						} else {
							icon = Material.getMaterial(276);
						}
						SpoutManager.getPlayer(player).sendNotification(title, message, icon);
					}
					catch (UnsupportedOperationException e) {
						System.err.println(e.getMessage());
						spoutUsed=false;
					}
				}
			}
        }

        
		if (!spoutUsed) {
			String ChatMessage;
	        if (targetMob.isDead()) {
				ChatMessage = plugin.getLangConfig().getString("chatKilledMessage");
	        } else {
	        	ChatMessage = plugin.getLangConfig().getString("chatMessage");
				
		        if ((mobsHealth<2) || (mobsHealth<=(mobsMaxHealth/4)) ) {
		        	ChatMessage=ChatMessage.replaceAll("%H",(ChatColor.DARK_RED) + Integer.toString(mobsHealth) + (ChatColor.WHITE));
		        } else {
		        	ChatMessage=ChatMessage.replaceAll("%H",Integer.toString(mobsHealth));
		        }
	        }
			ChatMessage=ChatMessage.replaceAll("%D",Integer.toString(thisDamange));
			ChatMessage=ChatMessage.replaceAll("%N",mobtype);
			ChatMessage=ChatMessage.replaceAll("%M",Integer.toString(mobsMaxHealth));
	        player.sendMessage(ChatMessage);
	        
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