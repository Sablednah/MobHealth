/*
 * This file is part of MobHealth.
 * Copyright (C) 2012-2013 Darren Douglas - darren.douglas@gmail.com
 *
 * MobHealth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MobHealth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MobHealth.  If not, see <http://www.gnu.org/licenses/>.
 */

package main.java.me.sablednah.MobHealth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MobHealthCommandExecutor implements CommandExecutor {
	public MobHealth plugin;

	public MobHealthCommandExecutor(MobHealth instance) {
		this.plugin=instance;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("mobhealth")){

			if (args.length > 0 && args[0].toLowerCase().equals("reload")) {
				Boolean doReload = false;
				
				if (sender instanceof Player) {
					if(((sender.hasPermission("mobhealth.command.reload") || sender.hasPermission("mobhealth.commands")) && MobHealth.usePermissions ) || (!MobHealth.usePermissions) ) {
						doReload = true;
					} else {
						sender.sendMessage("You do not have permission to reload.");
						return true;
					}
		
				
				} else {
					doReload = true;
				}

				if (doReload) {
					plugin.reloadConfig();
					MobHealth.usePermissions = plugin.getConfig().getBoolean("usePermissions");
					
					MobHealth.disableSpout = plugin.getConfig().getBoolean("disableSpout");
					MobHealth.disableChat = plugin.getConfig().getBoolean("disableChat");
					MobHealth.showRPG = plugin.getConfig().getBoolean("showRPG");
	
					MobHealth.disablePlayers = plugin.getConfig().getBoolean("disablePlayers");
					MobHealth.disableMonsters = plugin.getConfig().getBoolean("disableMonsters");
					MobHealth.disableAnimals = plugin.getConfig().getBoolean("disableAnimals");
					MobHealth.disablePets = plugin.getConfig().getBoolean("disablePets");
					
					MobHealth.damageDisplayType = plugin.getConfig().getInt("damageDisplayType");
					MobHealth.hideNoDammage = plugin.getConfig().getBoolean("hideNoDammage");
					MobHealth.defaultToggle = plugin.getConfig().getBoolean("defaultToggle");
					
					MobHealth.debugMode = plugin.getConfig().getBoolean("debugMode");
					MobHealth.showSideNotification = plugin.getConfig().getBoolean("showSideNotification");
				
					plugin.reloadLangConfig();
					
					MobHealth.chatMessage = plugin.getLangConfig().getString("chatMessage");
					MobHealth.spoutDamageMessage = plugin.getLangConfig().getString("spoutDamageMessage");
					MobHealth.spoutDamageTitle = plugin.getLangConfig().getString("spoutDamageTitle");
					MobHealth.chatKilledMessage = plugin.getLangConfig().getString("chatKilledMessage");
					MobHealth.spoutKilledMessage = plugin.getLangConfig().getString("spoutKilledMessage");
	
					MobHealth.chatMessageEgg = plugin.getLangConfig().getString("chatMessageEgg");
					MobHealth.chatMessageSnowball = plugin.getLangConfig().getString("chatMessageSnowball");
					MobHealth.spoutEggTitle = plugin.getLangConfig().getString("spoutEggTitle");
					MobHealth.spoutEggMessage = plugin.getLangConfig().getString("spoutEggMessage");
					MobHealth.spoutSnowballTitle = plugin.getLangConfig().getString("spoutSnowballTitle");
					MobHealth.spoutSnowballMessage = plugin.getLangConfig().getString("spoutSnowballMessage");
					
					MobHealth.RPGnotify = plugin.getLangConfig().getString("RPGnotify");
	
					MobHealth.heroesSkillSpoutDamageTitle = plugin.getLangConfig().getString("heroesSkillSpoutDamageTitle");
			    	MobHealth.heroesSkillSpoutDamageMessage = plugin.getLangConfig().getString("heroesSkillSpoutDamageMessage");
			    	MobHealth.heroesSkillSpoutKilledMessage = plugin.getLangConfig().getString("heroesSkillSpoutKilledMessage");
			    	MobHealth.heroesSkillChatMessage = plugin.getLangConfig().getString("heroesSkillChatMessage");
			    	MobHealth.heroesSkillChatKilledMessage = plugin.getLangConfig().getString("heroesSkillChatKilledMessage");
			  
					String entityName;
	
					for(String thisEntity : plugin.entityList) {
						entityName=plugin.getLangConfig().getString("entity"+thisEntity);
						if (entityName == null ) { 
							entityName=thisEntity;
						}
						MobHealth.entityLookup.put((thisEntity), entityName);
						if (MobHealth.debugMode) {
							MobHealth.logger.info(thisEntity+" - "+entityName);
						}
					}
	
					if (MobHealth.debugMode) {
						MobHealth.logger.info("DebugMode enabled.");
					}
					if (MobHealth.usePermissions) {
						MobHealth.logger.info("Using Permissions.");
					} else {
						MobHealth.logger.info("Permissions Disabled.");
					}
					if (MobHealth.disableSpout) {
						MobHealth.logger.info("Spout Disabled.");
					} else {
						MobHealth.logger.info("Spout Enabled.");
					}
					if (MobHealth.disablePlayers) {
						MobHealth.logger.info("Player Notifications Disabled.");
					} else {
						MobHealth.logger.info("Player Notifications Enabled.");
					}
					if (MobHealth.disableMonsters) {
						MobHealth.logger.info("Monster Notifications Disabled.");
					} else {
						MobHealth.logger.info("Monster Notifications Enabled.");
					}
					if (MobHealth.disableAnimals) {
						MobHealth.logger.info("Animals Notifications Disabled.");
					} else {
						MobHealth.logger.info("Animals Notifications Enabled.");
					}
					if (MobHealth.disablePets) {
						MobHealth.logger.info("Pet Notifications Disabled.");
					} else {
						MobHealth.logger.info("Pet Notifications Enabled.");
					}
					return true;
				}
			}
		}

		if (args.length > 0 && args[0].toLowerCase().equals("toggle")) {
			if (!(sender instanceof Player)) {
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED + "You need to specify a player to toggle!");
					return true;
				}
				@SuppressWarnings("deprecation")
				Player other = (Bukkit.getServer().getPlayer(args[1]));
				if (other == null) {
					sender.sendMessage(ChatColor.RED + args[1] + " is not online!");
					return true;
				}
				plugin.togglePluginState(other);
				return true;
			}
			if (args.length<2) {
				if(((sender.hasPermission("mobhealth.command.toggle") || sender.hasPermission("mobhealth.commands")) && MobHealth.usePermissions ) || (!MobHealth.usePermissions) ) {
					plugin.togglePluginState((Player) sender);
				} else {
					sender.sendMessage("You do not have permission to toggle.");
				}
			} else {
				if(((sender.hasPermission("mobhealth.command.toggle.others") || sender.hasPermission("mobhealth.commands")) && MobHealth.usePermissions ) || (!MobHealth.usePermissions) ) {
					@SuppressWarnings("deprecation")
					Player other = (Bukkit.getServer().getPlayer(args[1]));
					if (other == null) {
						sender.sendMessage(ChatColor.RED + args[1] + " is not online!");
						return true;
					}
					plugin.togglePluginState(other);
				} else {
					sender.sendMessage("You do not have permission to toggle other players.");
				}			
			}
			return true;
		}
		return false; 
	}
}
