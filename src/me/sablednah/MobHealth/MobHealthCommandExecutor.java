package me.sablednah.MobHealth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class MobHealthCommandExecutor implements CommandExecutor {
		public MobHealth plugin;
		
		public MobHealthCommandExecutor(MobHealth instance) {
			this.plugin=instance;
		}
	 
		@SuppressWarnings("unchecked")
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if(command.getName().equalsIgnoreCase("mobhealth")){

				PluginDescriptionFile pdfFile = plugin.getDescription();
				String myName=pdfFile.getName();
				
				if (args.length > 0 && args[0].toLowerCase().equals("reload")) {
					
            		plugin.reloadConfig();
            		MobHealth.usePermissions=plugin.getConfig().getBoolean("usePermissions");
            		MobHealth.disableSpout=plugin.getConfig().getBoolean("disableSpout");
            		MobHealth.enableEasterEggs=plugin.getConfig().getBoolean("enableEasterEggs");

            		plugin.reloadLangConfig();
            		MobHealth.langProfanity = plugin.getLangConfig().getList("profanity");
                	MobHealth.profanityMessage = plugin.getLangConfig().getString("profanityMessage");
                	MobHealth.eleven = plugin.getLangConfig().getString("eleven");
                	MobHealth.chatMessage = plugin.getLangConfig().getString("chatMessage");
                	MobHealth.spoutDamageMessage = plugin.getLangConfig().getString("spoutDamageMessage");
                	MobHealth.spoutDamageTitle = plugin.getLangConfig().getString("spoutDamageTitle");
                	MobHealth.chatKilledMessage = plugin.getLangConfig().getString("chatKilledMessage");
                	MobHealth.spoutKilledMessage = plugin.getLangConfig().getString("spoutKilledMessage");

                	String entityName;
                	
                	for(String thisEntity : plugin.entityList) {
                		entityName=plugin.getLangConfig().getString("entity"+thisEntity);
                		if (entityName == null ) { 
                			entityName=thisEntity;
                		}
                		MobHealth.entityLookup.put((thisEntity), entityName);
                		MobHealth.logger.info(thisEntity+" - "+entityName);
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
            		if (MobHealth.enableEasterEggs) {
            			MobHealth.logger.info("[" + myName + "] Chat Features Enabled.");
            		}
    				return true;
				}
			}
			return false; 
		}

}
