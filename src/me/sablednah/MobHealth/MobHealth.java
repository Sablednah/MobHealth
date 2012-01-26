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
import java.util.*;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;



public class MobHealth extends JavaPlugin {

	public static MobHealth plugin;
	public final ServerChatPlayerListener playerListener = new ServerChatPlayerListener(this);
	public final ServerDamageEntityListener EntityListener  = new ServerDamageEntityListener(this);
	public final static Logger logger = Logger.getLogger("Minecraft");
	
	public static Boolean usePermissions;
	public static Boolean disableSpout;
	public static Boolean enableEasterEggs;
	public static Boolean disablePlayers;
	public static Boolean disableMonsters;
	public static Boolean disableAnimals;
	public static int damageDisplayType;
	public static Boolean hideNoDammage;
	
	public static List<Object> langProfanity;
	public static String profanityMessage;
	public static List<Object> langTriggers;
	public static String eleven;

	public static String chatMessage;
	public static String chatKilledMessage;
	public static String chatMessageEgg;
	public static String chatMessageSnowball;
	
	public static String spoutDamageTitle;
	public static String spoutDamageMessage;
	public static String spoutKilledMessage;
	public static String spoutEggTitle;
	public static String spoutEggMessage;
	public static String spoutSnowballTitle;
	public static String spoutSnowballMessage;
	
	private MobHealthCommandExecutor myExecutor;
    private String VersionNew;
    private String VersionCurrent;

	private FileConfiguration LangConfig = null;
	private File LangConfigurationFile = null;
    
	public static Map<Player, Boolean> pluginEnabled = new HashMap<Player, Boolean>();
	
	public static Map<String, String> entityLookup = new HashMap<String, String>();

	public static String[] animalList = { "Pig","Sheep","Cow","Chicken","MushroomCow","Snowman","Squid","Villager","Wolf" };
	public static String[] monsterList = { "Blaze","Zombie","Creeper","Skeleton","Spider","Ghast","MagmaCube","Slime","CaveSpider","EnderDragon","EnderMan","Giant","PigZombie","SilverFish","Spider" };
	
	//public String[] entityList={ "Blaze","Pig","Sheep","Cow","Chicken","Zombie","Creeper","Skeleton","Spider","Ghast","MagmaCube","Slime","CaveSpider","EnderDragon","EnderMan","Giant","MushroomCow","PigZombie","SilverFish","Snowman","Spider","Squid","Villager","Wolf" };

    public String[] entityList= concat(animalList,monsterList);
	
    public static Boolean hasLikeABoss;
    
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
		
		//old syntax for events.
		//pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Normal, this);		
		//pm.registerEvent(Event.Type.ENTITY_DAMAGE, this.EntityListener, Event.Priority.Lowest, this);
		pm.registerEvents(this.playerListener, this);		
		pm.registerEvents(this.EntityListener, this);
		
		myExecutor = new MobHealthCommandExecutor(this);
		getCommand("MobHealth").setExecutor(myExecutor);
		
		loadConfiguration();
		
		hasLikeABoss = this.getServer().getPluginManager().isPluginEnabled("Likeaboss");

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
		if (disablePlayers) {
			logger.info("[" + myName + "] Player Notifications Disabled.");
		} else {
			logger.info("[" + myName + "] Player Notifications Enabled.");
		}
		if (disableMonsters) {
			logger.info("[" + myName + "] Monster Notifications Disabled.");
		} else {
			logger.info("[" + myName + "] Monster Notifications Enabled.");
		}
		if (disableAnimals) {
			logger.info("[" + myName + "] Animals Notifications Disabled.");
		} else {
			logger.info("[" + myName + "] Animals Notifications Enabled.");
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
	public void loadConfiguration() {
        getConfig().options().copyDefaults(true);
        
        String headertext;
        headertext="Default MobHealth Config file\r\n\r\n";
        headertext+="disableSpout: [true|false] - force messages to display in chat even if spout is present.\r\n";
        headertext+="usePermissions: [true|false] - true requires MobHealth.show (or MobHealth.*) to show message to player.\r\n";
        headertext+="enableEasterEggs: [true|false] - turns on 'extra chat features'.  (Basic Profanity filter - and message when people mention 11/eleven.)\r\n";
        headertext+="\r\n";
        headertext+="disablePlayers: [true|false] - disable notifications for player hits.\r\n";
        headertext+="disableMonsters: [true|false] - disable notifications for 'monster' hits.\r\n";
        headertext+="disableAnimals: [true|false] - disable notifications for 'animal' hits.\r\n";
        headertext+="damageDisplayType: [1|2|3|4]\r\n";
        headertext+="    1: display damage inflicted.  \r\n";
        headertext+="    2: display damage taken.\r\n";
        headertext+="    3: display damage inflicted (-amount resisted)\r\n";
        headertext+="    4: display damage taken (+amount resisted)\r\n";
        headertext+="hideNoDammage: [true|false] Hide notifications that inflict 0 damage.  Custom Egg and Snowball notifications are exempt.\r\n";
        headertext+="\r\n";
        
        getConfig().options().header(headertext);
        getConfig().options().copyHeader(true);
 
		usePermissions = getConfig().getBoolean("usePermissions");
		disableSpout = getConfig().getBoolean("disableSpout");
		enableEasterEggs = getConfig().getBoolean("enableEasterEggs");
        
		disablePlayers = getConfig().getBoolean("disablePlayers");
		disableMonsters = getConfig().getBoolean("disableMonsters");
		disableAnimals = getConfig().getBoolean("disableAnimals");
		damageDisplayType = getConfig().getInt("damageDisplayType");
		
		hideNoDammage = getConfig().getBoolean("hideNoDammage");
		
        saveConfig();
   
        getLangConfig();

        langProfanity = getLangConfig().getList("profanity");
        profanityMessage = getLangConfig().getString("profanityMessage");
    	langTriggers = getLangConfig().getList("triggers");
        eleven = getLangConfig().getString("eleven");
    	chatMessage = getLangConfig().getString("chatMessage");
    	chatKilledMessage = getLangConfig().getString("chatKilledMessage");
    	spoutKilledMessage = getLangConfig().getString("spoutKilledMessage");
    	spoutDamageMessage = getLangConfig().getString("spoutDamageMessage");
    	spoutDamageTitle = getLangConfig().getString("spoutDamageTitle");

    	chatMessageEgg = getLangConfig().getString("chatMessageEgg");
    	chatMessageSnowball = getLangConfig().getString("chatMessageSnowball");
       	spoutEggTitle = getLangConfig().getString("spoutEggTitle");
    	spoutEggMessage = getLangConfig().getString("spoutEggMessage");
    	spoutSnowballTitle = getLangConfig().getString("spoutSnowballTitle");
    	spoutSnowballMessage = getLangConfig().getString("spoutSnowballMessage");
    	
    	String entityName;
    	
    	for(String thisEntity : entityList) {
    		entityName=getLangConfig().getString("entity"+thisEntity);
    		if (entityName == null ) { 
    			entityName=thisEntity;
    		}
    		entityLookup.put((thisEntity), entityName);
//    		logger.info(thisEntity+" - "+entityName);
    	}

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
	
	
	/**
	 * Get if plugin is enabled for a player.
	 * 
	 * @param player
	 * @return Boolean
	 */
	public static Boolean getPluginState(Player player){	
	    if(pluginEnabled.containsKey(player)){
	        return pluginEnabled.get(player);
	    }
	    return true;
	}

	/**
	 * Toggle if plugin is enabled for a player
	 * 
	 * @param player
	 */
	public static void togglePluginState(Player player){		
	    if(pluginEnabled.containsKey(player)){
	        if(pluginEnabled.get(player)){
	            pluginEnabled.put(player, false);
	            player.sendMessage("Notifications disabled.");
	        } else {
	            pluginEnabled.put(player, true);
	            player.sendMessage("Notifications enabled.");
	        }
	    } else {
	        pluginEnabled.put(player, false); //Plugin enabled by default.
	        player.sendMessage("Notifications disabled.");
	    }
	}

	/**
	 * Joins two arrays
	 * 
	 * @param first array
	 * @param second array
	 * @return Arrays joined
	 */
	public static <T> T[] concat(T[] first, T[] second) {
		  T[] result = Arrays.copyOf(first, first.length + second.length);
		  System.arraycopy(second, 0, result, first.length, second.length);
		  return result;
		}
}
