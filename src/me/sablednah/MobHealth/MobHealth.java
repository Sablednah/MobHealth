/**
 * @author	sable <darren.douglas@gmail.com>
 * @version	3.2
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

import me.sablednah.MobHealth.API.MobHealthAPI;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import org.getspout.spoutapi.gui.Widget;

public class MobHealth extends JavaPlugin {

    public static MobHealth plugin;
    public final ServerDamageEntityListener EntityListener = new ServerDamageEntityListener(this);
    public final HeroesEventListener HeroesDamageEventListener = new HeroesEventListener(this);
    public final static Logger logger = Logger.getLogger("Minecraft");

    public static Boolean usePermissions;
    public static Boolean disableSpout;
    public static Boolean disableChat;
    public static Boolean showRPG;
    public static Boolean showSideNotification;
    public static Boolean disablePlayers;
    public static Boolean disableMonsters;
    public static Boolean disableAnimals;
    public static Boolean disablePets;
    public static int damageDisplayType;
    public static Boolean hideNoDammage;
    public static Boolean defaultToggle;
    public static Boolean debugMode;

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
    public static String RPGnotify;

    public static String heroesSkillSpoutDamageTitle;
    public static String heroesSkillSpoutDamageMessage;
    public static String heroesSkillSpoutKilledMessage;
    public static String heroesSkillChatMessage;
    public static String heroesSkillChatKilledMessage;

    private MobHealthCommandExecutor myExecutor;
    private String VersionNew;
    private String VersionCurrent;

    private FileConfiguration LangConfig = null;
    private File LangConfigurationFile = null;

    public static FileConfiguration PlayerConfig = null;
    public static File PlayerConfigurationFile = null;

    public static Map<Player, Boolean> pluginEnabled = null;
    public static Map<Player, Widget> hesGotAWidget = new HashMap<Player, Widget>();
    public static Map<String, String> entityLookup = new HashMap<String, String>();
    public static Map<Player, Widget> hesGotASideWidget = new HashMap<Player, Widget>();
    public static Map<Player, Widget> hesGotASideGradient = new HashMap<Player, Widget>();
    public static Map<Player, Widget> hesGotASideIcon = new HashMap<Player, Widget>();

    public static String[] animalList = { "Pig", "Sheep", "Cow", "Chicken","MushroomCow", "Golem", "IronGolem", "Snowman", "Squid","Villager", "Wolf", "Ocelot" };
    public static String[] monsterList = { "Blaze", "Zombie", "Creeper","Skeleton", "Spider", "Ghast", "MagmaCube", "Slime", "CaveSpider","EnderDragon", "EnderMan", "Giant", "PigZombie", "SilverFish","Spider" };

    public String[] entityList = concat(animalList, monsterList);

    public static Boolean hasLikeABoss;
    public static Boolean hasHeroes;
    public static Boolean hasMobArena;
    public static Boolean hasMobs;
    public static Boolean hasMA;
    public static Boolean hasZM;

    public static int notifications = 0;

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        PluginDescriptionFile pdfFile = this.getDescription();
        logger.info("[" + pdfFile.getName() + "] --- END OF LINE ---");
    }

    @Override
    public void onEnable() {
        plugin = this;

        PluginDescriptionFile pdfFile = this.getDescription();
        String myName = pdfFile.getName();
        VersionCurrent = getDescription().getVersion();

        // logger.info("[" + myName + "] Version " + pdfFile.getVersion() +
        // " starting.");

        PluginManager pm = getServer().getPluginManager();

        myExecutor = new MobHealthCommandExecutor(this);
        getCommand("MobHealth").setExecutor(myExecutor);

        loadConfiguration();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "SimpleNotice");

        hasLikeABoss = this.getServer().getPluginManager().isPluginEnabled("Likeaboss");
        hasHeroes = this.getServer().getPluginManager().isPluginEnabled("Heroes");
        hasMobArena = this.getServer().getPluginManager().isPluginEnabled("MobArena");
        hasMobs = this.getServer().getPluginManager().isPluginEnabled("Mobs");
        hasMA = this.getServer().getPluginManager().isPluginEnabled("Monster Apocalypse");
        hasZM = this.getServer().getPluginManager().isPluginEnabled("ZombieMod");

        if (hasHeroes) {
            pm.registerEvents(this.HeroesDamageEventListener, this);
        } else {
            pm.registerEvents(this.EntityListener, this);
        }

        if (hasLikeABoss) {
            logger.info("[" + myName + "] Likeaboss Support Enabled");
        }
        if (hasMobArena) {
            logger.info("[" + myName + "] MobArena Support Enabled");
        }
        if (hasHeroes) {
            logger.info("[" + myName + "] Heroes Support Enabled");
        }
        if (hasMobs) {
            logger.info("[" + myName + "] Mobs Support Enabled");
        }
        /*
 		if (hasHeroes && hasMobs) {
			logger.info("[" + myName + "] Using 'Mobs' and 'Heroes' together is NOT recommended by either plugin.");
		}
         */
        if (hasMA) {
            logger.info("[" + myName + "] Monster Apocalypse Support Enabled");
        }

        if (debugMode) {
            logger.info("[" + myName + "] DebugMode Enabled.");
        }
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
        if (disablePets) {
            logger.info("[" + myName + "] Pet Notifications Disabled.");
        } else {
            logger.info("[" + myName + "] Pet Notifications Enabled.");
        }


        // Enable Metrics
        try {
            Metrics metrics = new Metrics(this);

            // Plot the total amount of Notifications
            metrics.addCustomData(new Metrics.Plotter("Notifications") {

                @Override
                public int getValue() {
                    return MobHealth.notifications;
                }

            });

            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }

        /**
         * Schedule a version check every 6 hours for update notification .
         */
        this.getServer().getScheduler()
        .scheduleAsyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    VersionNew = getNewVersion(VersionCurrent);
                    String VersionOld = getDescription().getVersion();
                    if (!VersionNew.contains(VersionOld)) {
                        logger.warning("[MobHealth] " + VersionNew + " is available. You're using " + VersionOld);
                        logger.warning("[MobHealth] http://dev.bukkit.org/server-mods/mobhealth/");
                    }

                } catch (Exception e) {
                    // ignore exceptions
                }
            }
        }, 0, 432000);

        logger.info("[" + myName + "] Online.");
    }

    /**
     * Initialise config file
     */
    public void loadConfiguration() {
        getConfig().options().copyDefaults(true);

        String headertext;
        headertext = "Default MobHealth Config file\r\n\r\n";
        headertext += "disableSpout: [true|false] - force messages to display in chat even if spout is present.\r\n";
        headertext += "disableChat: [true|false] - force messages to display only if spout is present.\r\n";
        headertext += "setting both these to true will cause no notifications to appear!  \r\n";
        headertext += "\r\n";
        headertext += "showRPG: [true|false] - show RPG style notification (requires SpoutCraft).\r\n";
        headertext += "\r\n";
        headertext += "usePermissions: [true|false] - true requires MobHealth.show (or MobHealth.*) to show message to player.\r\n";
        headertext += "\r\n";
        headertext += "disablePlayers: [true|false] - disable notifications for player hits.\r\n";
        headertext += "disableMonsters: [true|false] - disable notifications for 'monster' hits.\r\n";
        headertext += "disableAnimals: [true|false] - disable notifications for 'animal' hits.\r\n";
        headertext += "damageDisplayType: [1|2|3|4]\r\n";
        headertext += "    1: display damage inflicted.  \r\n";
        headertext += "    2: display damage taken.\r\n";
        headertext += "    3: display damage inflicted (-amount resisted)\r\n";
        headertext += "    4: display damage taken (+amount resisted)\r\n";
        headertext += "hideNoDammage: [true|false] Hide notifications that inflict 0 damage.  Custom Egg and Snowball notifications are exempt.\r\n";
        headertext += "debugMode: [true|false] Enable extra debug info in logs.\r\n";
        headertext += "\r\n";

        getConfig().options().header(headertext);
        getConfig().options().copyHeader(true);

        usePermissions = getConfig().getBoolean("usePermissions");

        disableSpout = getConfig().getBoolean("disableSpout");
        disableChat = getConfig().getBoolean("disableChat");
        showRPG = getConfig().getBoolean("showRPG");
        showSideNotification = getConfig().getBoolean("showSideNotification");

        disablePlayers = getConfig().getBoolean("disablePlayers");
        disableMonsters = getConfig().getBoolean("disableMonsters");
        disableAnimals = getConfig().getBoolean("disableAnimals");
        disablePets = getConfig().getBoolean("disablePets");

        damageDisplayType = getConfig().getInt("damageDisplayType");

        hideNoDammage = getConfig().getBoolean("hideNoDammage");

        defaultToggle = getConfig().getBoolean("defaultToggle");

        debugMode = getConfig().getBoolean("debugMode");

        saveConfig();

        getLangConfig();

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

        RPGnotify = getLangConfig().getString("RPGnotify");

        heroesSkillSpoutDamageTitle = getLangConfig().getString("heroesSkillSpoutDamageTitle");
        heroesSkillSpoutDamageMessage = getLangConfig().getString("heroesSkillSpoutDamageMessage");
        heroesSkillSpoutKilledMessage = getLangConfig().getString("heroesSkillSpoutKilledMessage");
        heroesSkillChatMessage = getLangConfig().getString("heroesSkillChatMessage");
        heroesSkillChatKilledMessage = getLangConfig().getString("heroesSkillChatKilledMessage");

        String entityName;

        for (String thisEntity : entityList) {
            entityName = getLangConfig().getString("entity" + thisEntity);
            if (entityName == null) {
                entityName = thisEntity;
            }
            entityLookup.put((thisEntity), entityName);
        }

        saveLangConfig();

        try {
            pluginEnabled = (Map<Player, Boolean>) SaveToggle.load(plugin.getDataFolder() + File.separator + "toggleStates.bin");
        } catch (Exception e) {
            System.out.print(" toggleStates.bin error");
            e.printStackTrace();
        }

    }

    /**
     * Get latest version of plugin from remote server.
     * 
     * @param VersionCurrent
     *            String of current version to compare (returned in cases such
     *            as update server is unavailable).
     * @return returns Latest version as String
     * @throws Exception
     */
    public String getNewVersion(String VersionCurrent) throws Exception {
        String urlStr = "http://sablekisska.co.uk/asp/version.asp";
        try {

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            conn.setRequestProperty("User-agent", "[MobHealth Plugin] " + VersionCurrent);
            String inStr = null;
            inStr = convertStreamToString(conn.getInputStream());
            return inStr;

        } catch (Exception localException) {
            // System.out.print("exp: " + localException);
        }
        return VersionCurrent;
    }

    /**
     * Converts InputStream to String
     * 
     * One-line 'hack' to convert InputStreams to strings.
     * 
     * @param is
     *            The InputStream to convert
     * @return returns a String version of 'is'
     */
    public String convertStreamToString(InputStream is) {
        return new Scanner(is).useDelimiter("\\A").next();
    }

    public void reloadLangConfig() {
        if (LangConfigurationFile == null) {
            LangConfigurationFile = new File(getDataFolder(), "lang.yml");
        }
        LangConfig = YamlConfiguration.loadConfiguration(LangConfigurationFile);
        LangConfig.options().copyDefaults(true);

        // Look for defaults in the jar
        InputStream defConfigStream = getResource("lang.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration .loadConfiguration(defConfigStream);
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
    public static Boolean getPluginState(Player player) {
        if (pluginEnabled.containsKey(player)) {
            return pluginEnabled.get(player);
        }
        return defaultToggle;
    }

    /**
     * Toggle if plugin is enabled for a player
     * 
     * @param player
     */
    public static void togglePluginState(Player player) {
        if (pluginEnabled.containsKey(player)) {
            if (pluginEnabled.get(player)) {
                pluginEnabled.put(player, false);
                player.sendMessage("Notifications disabled.");
            } else {
                pluginEnabled.put(player, true);
                player.sendMessage("Notifications enabled.");
            }
        } else { // use defaultToggle
            // Plugin was enabled by default. - now uses defaultToggle
            pluginEnabled.put(player, !defaultToggle);
            if (defaultToggle) {
                player.sendMessage("Notifications disabled.");
            } else {
                player.sendMessage("Notifications enabled.");
            }
        }

        try {
            SaveToggle.save((HashMap<Player, Boolean>) pluginEnabled,
                    plugin.getDataFolder() + File.separator + "toggleStates.bin");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Get widgets for a player.
     * 
     * @param Player
     * @return widget
     */
    public static Widget getWidget(Player player, int widgetnumber) {
        if (widgetnumber == 3) {
            return hesGotASideIcon.get(player);
        } else if (widgetnumber == 2) {
            return hesGotASideWidget.get(player);
        } else if (widgetnumber == 1) {
            return hesGotASideGradient.get(player);
        } else {
            return hesGotAWidget.get(player);
        }
    }

    /**
     * store the widget!
     * 
     * @param Player
     *            Widget
     */
    public static void putWidget(Player player, Widget widget, int widgetnumber) {
        if (widgetnumber == 3) {
            hesGotASideIcon.put(player, widget);
        } else if (widgetnumber == 2) {
            hesGotASideWidget.put(player, widget);
        } else if (widgetnumber == 1) {
            hesGotASideGradient.put(player, widget);
        } else {
            hesGotAWidget.put(player, widget);
        }
    }

    /**
     * remove a widget!
     * 
     * @param Player
     */
    public static void killWidget(Player player, int widgetnumber) {
        if (widgetnumber == 3) {
            hesGotASideIcon.remove(player);
        } else if (widgetnumber == 2) {
            hesGotASideWidget.remove(player);
        } else if (widgetnumber == 1) {
            hesGotASideGradient.remove(player);
        } else {
            hesGotAWidget.remove(player);
        }
    }

    //

    /**
     * Joins two arrays
     * 
     * @param first
     *            array
     * @param second
     *            array
     * @return Arrays joined
     */
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    
    public MobHealthAPI getAPI(Plugin p) {
        return new MobHealthAPI(p);
        
    }

}
