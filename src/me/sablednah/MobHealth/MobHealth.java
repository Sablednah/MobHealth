package me.sablednah.MobHealth;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

public class MobHealth extends JavaPlugin {

	public static MobHealth plugin;
	protected FileConfiguration config;
	public final ServerChatPlayerListener playerListener = new ServerChatPlayerListener(this);
	public final ServerDamageEntityListener EntityListener  = new ServerDamageEntityListener(this);
	public final Logger logger = Logger.getLogger("Minecraft");
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		
		logger.info("[" + pdfFile.getName() + "] --- END OF LINE ---");
		saveConfig();
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		
		logger.info("[" + pdfFile.getName() + "] Version " + pdfFile.getVersion() + " starting.");
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Normal, this);		
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, this.EntityListener, Event.Priority.Normal, this);
		
		config = getConfig();
		
		logger.info("[" + pdfFile.getName() + "] Online.");
	}
	
}