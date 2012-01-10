package me.sablednah.MobHealth;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;

public class MobHealth extends JavaPlugin {

	public static MobHealth plugin;
	public final ServerChatPlayerListener playerListener = new ServerChatPlayerListener(this);
	public final ServerDamageEntityListener EntityListener  = new ServerDamageEntityListener(this);
	public final static Logger logger = Logger.getLogger("Minecraft");
	
	public static Boolean usePermissions;
	public static Boolean disableSpout;
	public static Boolean enableEasterEggs;
	
	private MobHealthCommandExecutor myExecutor;
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		
		logger.info("[" + pdfFile.getName() + "] --- END OF LINE ---");
		//saveConfig();
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		String myName=pdfFile.getName();
		
		logger.info("[" + myName + "] Version " + pdfFile.getVersion() + " starting.");
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Normal, this);		
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, this.EntityListener, Event.Priority.Normal, this);

		myExecutor = new MobHealthCommandExecutor(this);
		getCommand("MobHealth").setExecutor(myExecutor);
		
		FileConfiguration config = this.getConfig();

		usePermissions=config.getBoolean("usePermissions",false);
		disableSpout=config.getBoolean("disableSpout",false);
		enableEasterEggs=config.getBoolean("enableEasterEggs",false);
		
//		System.out.print("usePermissions "+usePermissions);
//		System.out.print("disableSpout "+disableSpout);
		if (usePermissions) {
			logger.info("[" + myName + "] Using Permissions.");
		} else {
			logger.info("[" + myName + "] Permissions Disabled.");
		}
		if (disableSpout) {
			logger.info("[" + myName + "] Spout Disabled.");
		} else {
			logger.info("[" + myName + "] Spout Enabled.");
		}
		if (enableEasterEggs) {
			logger.info("[" + myName + "] Chat Features Enabled.");
		}		
		config.options().copyDefaults(true);
		saveConfig();
		config.options().copyDefaults(false);
		
		logger.info("[" + myName + "] Online.");
	}

	public Boolean getusePermissions() {
		return usePermissions;
	}
	public void setusePermissions(Boolean usePermissions) {
		MobHealth.usePermissions = usePermissions;
		this.getConfig().set("usePermissions", usePermissions);
		saveConfig();
	}

	public Boolean getdisableSpout() {
		return disableSpout;
	}
	public void setdisableSpout(Boolean disableSpout) {
		MobHealth.disableSpout = disableSpout;
		this.getConfig().set("disableSpout", disableSpout);
		saveConfig();
	}
	
}