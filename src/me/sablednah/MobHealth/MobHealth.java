/**
 * @author	sable <darren.douglas@gmail.com>
 * @version	1.8
 * 
 */
package me.sablednah.MobHealth;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;

public class MobHealth extends JavaPlugin {

	public static MobHealth plugin;
	public final ServerChatPlayerListener playerListener = new ServerChatPlayerListener(this);
	public final ServerDamageEntityListener EntityListener  = new ServerDamageEntityListener(this);
	public final static Logger logger = Logger.getLogger("Minecraft");
	
	public static Boolean usePermissions;
	public static Boolean disableSpout;
	public static Boolean enableEasterEggs;

	public static List<String> langProfanity;
	public static String profanityMessage;
	public static String eleven;
	public static String chatMessage;
	public static String chatKilledMessage;
	public static String spoutDamageMessage;
	public static String spoutKilledMessage;
	public static String spoutDamageTitle;
	
	private MobHealthCommandExecutor myExecutor;
    private String VersionNew;
    private String VersionCurrent;

	private FileConfiguration LangConfig = null;
	private File LangConfigurationFile = null;
    
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.info("[" + pdfFile.getName() + "] --- END OF LINE ---");
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		String myName=pdfFile.getName();
		VersionCurrent = getDescription().getVersion().substring(0, 3);
		
		logger.info("[" + myName + "] Version " + pdfFile.getVersion() + " starting.");
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Normal, this);		
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, this.EntityListener, Event.Priority.Normal, this);

		myExecutor = new MobHealthCommandExecutor(this);
		getCommand("MobHealth").setExecutor(myExecutor);
		
		loadConfiguration();
		
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
		
        /**
         *  Schedule a version check every 6 hours for update notification .
         */
        this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                	VersionNew = getNewVersion(VersionCurrent);
                    String VersionOld = getDescription().getVersion().substring(0, 3);
                    if (!VersionNew.contains(VersionOld)) {
                    	logger.warning(VersionNew + " is available. You're using " + VersionOld);
                    	logger.warning("http://dev.bukkit.org/server-mods/mobhealth/");
                    }
                } catch (Exception e) {
                    // ignore exceptions
                }
            }
        }, 0, 5184000);
		
		logger.info("[" + myName + "] Online.");
	}

	
	/**
	 * Initialise config file 
	 */
    @SuppressWarnings("unchecked")
	public void loadConfiguration() {
        getConfig().options().copyDefaults(true);
        
        String headertext;
        headertext="Default MobHealth Config file\r\n\r\n";
        headertext+="disableSpout: [true|false] - force messages to display in chat even if spout is present.\r\n";
        headertext+="usePermissions: [true|false] - true requires MobHealth.show (or MobHealth.*) to show message to player.\r\n";
        headertext+="enableEasterEggs: [true|false] - turns on 'extra chat features'.  (Basic Profanity filter - and message when people mention 11/eleven.)\r\n";
        headertext+="\r\n";
        
        getConfig().options().header(headertext);
        getConfig().options().copyHeader(true);
 
		usePermissions = getConfig().getBoolean("usePermissions");
		disableSpout = getConfig().getBoolean("disableSpout");
		enableEasterEggs = getConfig().getBoolean("enableEasterEggs");
        
        saveConfig();
   
        getLangConfig();

    	langProfanity = getLangConfig().getList("profanity");
    	profanityMessage = getLangConfig().getString("profanityMessage");
    	eleven = getLangConfig().getString("eleven");
    	chatMessage = getLangConfig().getString("chatMessage");
    	chatKilledMessage = getLangConfig().getString("chatKilledMessage");
    	spoutKilledMessage = getLangConfig().getString("spoutKilledMessage");
    	spoutDamageMessage = getLangConfig().getString("spoutDamageMessage");
    	spoutDamageTitle = getLangConfig().getString("spoutDamageTitle");

        saveLangConfig();
    }

    /**
     * Get latest version of plugin from remote server.
     * 
     * @param VersionCurrent  String of current version to compare (returned in cases such as update server is unavailable).
     * @return returns Latest version as String
     * @throws Exception
     */
	public String getNewVersion(String VersionCurrent) throws Exception {
		String urlStr = "http://sablekisska.co.uk/asp/version.asp";
		try {
			
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			conn.setRequestProperty ( "User-agent", "[MobHealth Plugin] "+VersionCurrent);
			String inStr = null;
			inStr = convertStreamToString(conn.getInputStream());
			return inStr;
			
		}
		catch (Exception localException) {}
		return VersionCurrent;
	}
	
	/**
	 * Converts InputStream to String
	 * 
	 * One-line 'hack' to convert InputStreams to strings.
	 * 
	 * @param	is  The InputStream to convert
	 * @return	returns a String version of 'is'
	 */
	public String convertStreamToString(InputStream is) { 
	    return new Scanner(is).useDelimiter("\\A").next();
	}




	public void reloadLangConfig() {
		if (LangConfigurationFile  == null) {
			LangConfigurationFile  = new File(getDataFolder(), "lang.yml");
	    }
		LangConfig  = YamlConfiguration.loadConfiguration(LangConfigurationFile);
        LangConfig.options().copyDefaults(true);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = getResource("lang.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        LangConfig.setDefaults(defConfig);
	    }
	}

	public FileConfiguration getLangConfig() {
	    if (LangConfig == null) {
	        reloadLangConfig();
	    }
	    return LangConfig;
	}

	public void saveLangConfig() {
	    if (LangConfig == null || LangConfigurationFile == null) {
	    	return;
	    }
	    try {
	    	LangConfig.save(LangConfigurationFile);
	    } catch (IOException ex) {
	    	logger.severe("Could not save Lang config to " + LangConfigurationFile + " " + ex);
	    }
	}

}