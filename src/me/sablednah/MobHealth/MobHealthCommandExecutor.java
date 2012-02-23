package me.sablednah.MobHealth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;


public class MobHealthCommandExecutor implements CommandExecutor {
	public MobHealth plugin;

	public MobHealthCommandExecutor(MobHealth instance) {
		this.plugin=instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("mobhealth")){

			PluginDescriptionFile pdfFile = plugin.getDescription();
			String myName=pdfFile.getName();

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
	
					MobHealth.disablePlayers = plugin.getConfig().getBoolean("disablePlayers");
					MobHealth.disableMonsters = plugin.getConfig().getBoolean("disableMonsters");
					MobHealth.disableAnimals = plugin.getConfig().getBoolean("disableAnimals");
					MobHealth.damageDisplayType = plugin.getConfig().getInt("damageDisplayType");
					MobHealth.hideNoDammage = plugin.getConfig().getBoolean("hideNoDammage");
					MobHealth.debugMode = plugin.getConfig().getBoolean("debugMode");
				
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
						MobHealth.logger.info("[" + myName + "] DebugMode enabled.");
					}
					if (MobHealth.usePermissions) {
						MobHealth.logger.info("[" + myName + "] Using Permissions.");
					} else {
						MobHealth.logger.info("[" + myName + "] Permissions Disabled.");
					}
					if (MobHealth.disableSpout) {
						MobHealth.logger.info("[" + myName + "] Spout Disabled.");
					} else {
						MobHealth.logger.info("[" + myName + "] Spout Enabled.");
					}
					if (MobHealth.disablePlayers) {
						MobHealth.logger.info("[" + myName + "] Player Notifications Disabled.");
					} else {
						MobHealth.logger.info("[" + myName + "] Player Notifications Enabled.");
					}
					if (MobHealth.disableMonsters) {
						MobHealth.logger.info("[" + myName + "] Monster Notifications Disabled.");
					} else {
						MobHealth.logger.info("[" + myName + "] Monster Notifications Enabled.");
					}
					if (MobHealth.disableAnimals) {
						MobHealth.logger.info("[" + myName + "] Animals Notifications Disabled.");
					} else {
						MobHealth.logger.info("[" + myName + "] Animals Notifications Enabled.");
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
				Player other = (Bukkit.getServer().getPlayer(args[1]));
				if (other == null) {
					sender.sendMessage(ChatColor.RED + args[1] + " is not online!");
					return true;
				}
				MobHealth.togglePluginState(other);
				return true;
			}
			if (args.length<2) {
				if(((sender.hasPermission("mobhealth.command.toggle") || sender.hasPermission("mobhealth.commands")) && MobHealth.usePermissions ) || (!MobHealth.usePermissions) ) {
					MobHealth.togglePluginState((Player) sender);
				} else {
					sender.sendMessage("You do not have permission to toggle.");
				}
			} else {
				if(((sender.hasPermission("mobhealth.command.toggle.others") || sender.hasPermission("mobhealth.commands")) && MobHealth.usePermissions ) || (!MobHealth.usePermissions) ) {
					Player other = (Bukkit.getServer().getPlayer(args[1]));
					if (other == null) {
						sender.sendMessage(ChatColor.RED + args[1] + " is not online!");
						return true;
					}
					MobHealth.togglePluginState(other);
				} else {
					sender.sendMessage("You do not have permission to toggle other players.");
				}			
			}
			return true;
		}
		return false; 
	}
}
