package me.sablednah.MobHealth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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
            		plugin.reloadConfig();
            		FileConfiguration config = plugin.getConfig();
            		MobHealth.usePermissions=config.getBoolean("usePermissions",false);
            		MobHealth.disableSpout=config.getBoolean("disableSpout",false);
            		MobHealth.enableEasterEggs=config.getBoolean("enableEasterEggs",false);
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
            		if (MobHealth.enableEasterEggs) {
            			MobHealth.logger.info("[" + myName + "] Chat Features Enabled.");
            		}
    				return true;
				}
			}
			return false; 
		}

}
