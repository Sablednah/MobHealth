/**
 * @author      sable <darren.douglas@gmail.com>
 * @version     3.2
 * 
 */

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import java.util.*;
import java.util.logging.Logger;

import main.java.me.sablednah.MobHealth.Metrics.Graph;
import main.java.me.sablednah.MobHealth.Updater.UpdateType;
import main.java.me.sablednah.MobHealth.API.MobHealthAPI;

import org.bukkit.ChatColor;
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
    public SaveToggle toggle = new SaveToggle(this);
    public static Logger logger;
    
    public static Boolean usePermissions;
    public static Boolean disableSpout;
    public static Boolean disableChat;
    public static Boolean showRPG;
    public static Boolean showSideNotification;
    
    public static Boolean showPlayerHeadHealth;
    public static Boolean showMobHeadHealth;
    public static Boolean usePercentForPlayer;
    
    public static Boolean useBarForMobs;
    public static Boolean hideBarForNPC;
    public static Boolean hideBarForAnimal;
    
    public static Boolean hideBarForVillager;
    public static Boolean showbarCustomNameOnly;
    public static List<String> forceBarHide = null;
    
    public static Boolean cleanDeathMessages;
    public static Boolean disablePlayers;
    public static Boolean disableMonsters;
    public static Boolean disableAnimals;
    public static Boolean disablePets;
    public static int damageDisplayType;
    public static Boolean hideNoDammage;
    public static Boolean defaultToggle;
    public static Boolean updateCheck;
    public static Boolean doUpdate;
    public static Boolean debugMode;
    public static String healthPrefix;
    public static Boolean alwaysVisable;
    
    public static String chatMessage;
    public static String chatKilledMessage;
    public static String playerLabel;
    public static String playerLabelPercent;
    public static String healthBarCharacter = "|";
    public static int healthBarSize = 20;
    
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
    
    private FileConfiguration LangConfig = null;
    private File LangConfigurationFile = null;
    
    public static FileConfiguration PlayerConfig = null;
    public static File PlayerConfigurationFile = null;
    
    public static Map<UUID, Boolean> pluginEnabled = null;
    public static Map<UUID, Widget> hesGotAWidget = new HashMap<UUID, Widget>();
    public static Map<String, String> entityLookup = new HashMap<String, String>();
    public static Map<UUID, Widget> hesGotASideWidget = new HashMap<UUID, Widget>();
    public static Map<UUID, Widget> hesGotASideGradient = new HashMap<UUID, Widget>();
    public static Map<UUID, Widget> hesGotASideIcon = new HashMap<UUID, Widget>();
    
    public static String[] animalList = { "Donkey", "Mule", "Horse", "Bat", "Pig", "Sheep", "Cow", "Chicken", "MushroomCow", "Golem", "IronGolem", "Snowman", "Squid", "Villager",
            "Wolf", "Ocelot" };
    public static String[] monsterList = { "Witch", "Wither", "Blaze", "Zombie", "ZombieVillagerBaby", "ZombieVillager", "ZombieBaby", "Creeper", "Skeleton", "SkeletonWither",
            "Spider", "Ghast", "MagmaCube", "Slime", "CaveSpider", "EnderDragon", "Enderman", "Giant", "PigZombie", "Silverfish", "Spider" };
    
    public String[] entityList = concat(animalList, monsterList);
    
    public static Boolean hasLikeABoss;
    public static Boolean hasCorruption;
    public static Boolean hasHeroes;
    public static Boolean hasMobArena;
    public static Boolean hasMobs;
    public static Boolean hasMA;
    public static Boolean hasZM;
    public static Boolean hasBloodMoon;
    public static Boolean hasEpicBoss;
    
    public static int notifications = 0;
    
    public static SetHealth setHealths = null;
    
    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelTasks(this);
        PluginDescriptionFile pdfFile = this.getDescription();
        logger.info("[" + pdfFile.getName() + "] --- END OF LINE ---");
    }
    
    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();
        String VersionCurrent = getDescription().getVersion();
        
        PluginManager pm = getServer().getPluginManager();
        
        myExecutor = new MobHealthCommandExecutor(this);
        getCommand("MobHealth").setExecutor(myExecutor);
        
        loadConfiguration();
        
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "SimpleNotice");
        
        hasLikeABoss = this.getServer().getPluginManager().isPluginEnabled("Likeaboss");
        hasCorruption = this.getServer().getPluginManager().isPluginEnabled("Corruption");
        hasHeroes = this.getServer().getPluginManager().isPluginEnabled("Heroes");
        hasMobArena = this.getServer().getPluginManager().isPluginEnabled("MobArena");
        hasMobs = this.getServer().getPluginManager().isPluginEnabled("Mobs");
        hasMA = this.getServer().getPluginManager().isPluginEnabled("Monster Apocalypse");
        hasZM = this.getServer().getPluginManager().isPluginEnabled("ZombieMod");
        hasBloodMoon = this.getServer().getPluginManager().isPluginEnabled("BloodMoon");
        hasEpicBoss = this.getServer().getPluginManager().isPluginEnabled("EpicBossRecoded");
//        hasCitizens = this.getServer().getPluginManager().isPluginEnabled("Citizens");
        
        pm.registerEvents(this.EntityListener, this);
        if (hasHeroes) {
            pm.registerEvents(this.HeroesDamageEventListener, this);
        }
        if (hasLikeABoss) {
            logger.info("Likeaboss Support Enabled");
        }
        if (hasCorruption) {
            logger.info("Corruption Support Enabled");
        }
        if (hasMobArena) {
            logger.info("MobArena Support Enabled");
        }
        if (hasHeroes) {
            logger.info("Heroes Support Enabled");
        }
        if (hasMobs) {
            logger.info("Mobs Support Enabled");
        }
        if (hasMA) {
            logger.info("Monster Apocalypse Support Enabled");
        }
        if (hasZM) {
            logger.info("ZombieMod Support Enabled");
        }
        if (hasBloodMoon) {
            logger.info("BloodMoon Support Enabled");
        }
        if (hasEpicBoss) {
            logger.info("EpicBossReloaded Support Enabled");
        }
        if (debugMode) {
            logger.info("DebugMode Enabled.");
        }
        if (usePermissions) {
            logger.info("Using Permissions.");
        } else {
            logger.info("Permissions Disabled.");
        }
        if (disableSpout) {
            logger.info("Spout Disabled.");
        } else {
            logger.info("Spout Enabled.");
        }
        if (disablePlayers) {
            logger.info("Player Notifications Disabled.");
        } else {
            logger.info("Player Notifications Enabled.");
        }
        if (disableMonsters) {
            logger.info("Monster Notifications Disabled.");
        } else {
            logger.info("Monster Notifications Enabled.");
        }
        if (disableAnimals) {
            logger.info("Animals Notifications Disabled.");
        } else {
            logger.info("Animals Notifications Enabled.");
        }
        if (disablePets) {
            logger.info("Pet Notifications Disabled.");
        } else {
            logger.info("Pet Notifications Enabled.");
        }
        
        if (showPlayerHeadHealth || showMobHeadHealth) {
            // Sanity check: - is the score board actually available?
            try {
                Class.forName("org.bukkit.scoreboard.Scoreboard");
                setHealths = new SetHealth(this);
            } catch (ClassNotFoundException exception) {
                logger.warning("Scoreboard API not detected.");
                logger.warning("Please update craftbukkit or disable showPlayerHeadHealth and showMobHeadHealth in config.");
                logger.warning("Disabling HealthBars...");
                showPlayerHeadHealth = false;
                showMobHeadHealth = false;
            }
        }
        
        // Enable Metrics
        try {
            Metrics metrics = new Metrics(this);
            
            // Plot the total amount of Notifications
            Graph notificationsGraph = metrics.createGraph("Total amount of Notifications");

            notificationsGraph.addPlotter(new Metrics.Plotter("Notifications") {
                
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
         * Schedule a version check every 6 hours for update notification. First check 10 seconds after init. (gives
         * server chance to start
         */
        if (updateCheck) {
            UpdateType upd = Updater.UpdateType.NO_DOWNLOAD;
            
            if (doUpdate) {
                logger.info("Auto updater enabled.");
                upd = Updater.UpdateType.DEFAULT;
            } else {
                logger.info("Auto updater enable disabled.");
            }
            
            Updater updater = new Updater(this, 35545, this.getFile(), upd, true);
            Updater.UpdateResult result = updater.getResult();
            
            String name;
            
            switch (result) {
                case SUCCESS:
                    // Success: The updater found an update, and has readied it to be loaded the next time the server restarts/reloads
                    break;
                case NO_UPDATE:
                    // No Update: The updater did not find an update, and nothing was downloaded.
                    name = updater.getLatestName(); // Get the latest version
                    logger.info(name + " is upto date.");
                    logger.info("http://dev.bukkit.org/server-mods/mobhealth/");
                    break;
                case FAIL_DOWNLOAD:
                    // Download Failed: The updater found an update, but was unable to download it.
                    name = updater.getLatestName(); // Get the latest version
                    logger.info(name + " is available (You're using " + VersionCurrent + ")");
                    logger.warning("Automatic Download Failed please visit");
                    logger.info("http://dev.bukkit.org/server-mods/mobhealth/");
                    break;
                case FAIL_DBO:
                case FAIL_NOVERSION:
                case FAIL_APIKEY:
                    logger.warning("Error during version check - " + result.toString());
                    break;
                case UPDATE_AVAILABLE:
                    name = updater.getLatestName(); // Get the latest version
                    logger.warning(name + " is available (You're using " + VersionCurrent + ")");
                    logger.info("http://dev.bukkit.org/server-mods/mobhealth/");
                    break;
            }
        } else {
            logger.info("Update check disabled.");
        }
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
        showPlayerHeadHealth = getConfig().getBoolean("showPlayerHeadHealth");
        showMobHeadHealth = getConfig().getBoolean("showMobHeadHealth");
        usePercentForPlayer = getConfig().getBoolean("usePercentForPlayer");
        useBarForMobs = getConfig().getBoolean("useBarForMobs");
        hideBarForNPC = getConfig().getBoolean("hideBarForNPC");
        hideBarForAnimal = getConfig().getBoolean("hideBarForAnimal");
        
        hideBarForVillager = getConfig().getBoolean("hideBarForVillager");
        showbarCustomNameOnly = getConfig().getBoolean("showbarCustomNameOnly");
        @SuppressWarnings("unchecked")
        List<String> fbh = (List<String>) getConfig().getList("forceBarHide");
        fbh.add("Horse");
        
        
        HashSet<String> hs = new HashSet<String>();
        hs.addAll(fbh);
        fbh.clear();
        fbh.addAll(hs);
        
        forceBarHide = fbh;
        healthBarSize = getConfig().getInt("healthBarSize", healthBarSize);
        if (healthBarSize < 5) {
            healthBarSize = 5;
        }
        
        cleanDeathMessages = getConfig().getBoolean("cleanDeathMessages");
        
        disablePlayers = getConfig().getBoolean("disablePlayers");
        disableMonsters = getConfig().getBoolean("disableMonsters");
        disableAnimals = getConfig().getBoolean("disableAnimals");
        disablePets = getConfig().getBoolean("disablePets");
        alwaysVisable = getConfig().getBoolean("alwaysVisable");
        
        damageDisplayType = getConfig().getInt("damageDisplayType");
        hideNoDammage = getConfig().getBoolean("hideNoDammage");
        
        defaultToggle = getConfig().getBoolean("defaultToggle");
        
        updateCheck = getConfig().getBoolean("updateCheck",true);
        doUpdate = getConfig().getBoolean("doUpdate",true);
        
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
        
        playerLabel = getLangConfig().getString("playerLabel");
        healthBarCharacter = getLangConfig().getString("healthBarCharacter");
        healthBarCharacter = healthBarCharacter.replace("<3", "❤");
        healthBarCharacter = healthBarCharacter.replace("[X]", "█");
        healthBarCharacter = healthBarCharacter.replace("[x]", "▌");
        healthBarCharacter = healthBarCharacter.replace("[o]", "●");
        healthBarCharacter = healthBarCharacter.replace("[*]", "★");
        
        playerLabelPercent = getLangConfig().getString("playerLabelPercent");
        
        healthPrefix = getLangConfig().getString("healthPrefix", "&r&f&r");
//        logger.info("healthPrefix is:" + healthPrefix);
        healthPrefix = ChatColor.translateAlternateColorCodes('&', healthPrefix);
        logger.info("healthPrefix is:" + healthPrefix);
        logger.info("Example monster bar:" + barGraph(5, 10, MobHealth.healthBarSize, "Mob" + MobHealth.healthPrefix, ""));
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
        	
            pluginEnabled = (Map<UUID, Boolean>) toggle.load(plugin.getDataFolder() + File.separator + "toggleStates.bin");
        } catch (Exception e) {
            System.out.print(" toggleStates.bin error");
            e.printStackTrace();
        }
    }
    
    /**
     * Converts InputStream to String
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
        //InputStream defConfigStream = getResource("lang.yml");
        Reader defConfigStream = getTextResource("lang.yml");
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
    public static Boolean getPluginState(Player p) {
        UUID player = p.getUniqueId();
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
    public void togglePluginState(Player p) {
        UUID player = p.getUniqueId();
        if (pluginEnabled.containsKey(player)) {
            if (pluginEnabled.get(player)) {
                pluginEnabled.put(player, false);
                p.sendMessage("Notifications disabled.");
            } else {
                pluginEnabled.put(player, true);
                p.sendMessage("Notifications enabled.");
            }
        } else { // use defaultToggle
            // Plugin was enabled by default. - now uses defaultToggle
            pluginEnabled.put(player, !defaultToggle);
            if (defaultToggle) {
                p.sendMessage("Notifications disabled.");
            } else {
                p.sendMessage("Notifications enabled.");
            }
        }
        try {
        	
            toggle.save((HashMap<UUID, Boolean>) pluginEnabled, plugin.getDataFolder() + File.separator + "toggleStates.bin");
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
            return hesGotASideIcon.get(player.getUniqueId());
        } else if (widgetnumber == 2) {
            return hesGotASideWidget.get(player.getUniqueId());
        } else if (widgetnumber == 1) {
            return hesGotASideGradient.get(player.getUniqueId());
        } else {
            return hesGotAWidget.get(player.getUniqueId());
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
            hesGotASideIcon.put(player.getUniqueId(), widget);
        } else if (widgetnumber == 2) {
            hesGotASideWidget.put(player.getUniqueId(), widget);
        } else if (widgetnumber == 1) {
            hesGotASideGradient.put(player.getUniqueId(), widget);
        } else {
            hesGotAWidget.put(player.getUniqueId(), widget);
        }
    }
    
    /**
     * remove a widget!
     * 
     * @param Player
     */
    public static void killWidget(Player player, int widgetnumber) {
        if (widgetnumber == 3) {
            hesGotASideIcon.remove(player.getUniqueId());
        } else if (widgetnumber == 2) {
            hesGotASideWidget.remove(player.getUniqueId());
        } else if (widgetnumber == 1) {
            hesGotASideGradient.remove(player.getUniqueId());
        } else {
            hesGotAWidget.remove(player.getUniqueId());
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
    
    public static String barGraph(final int x, final int y, final int scale, final String prefix, final String suffix) {
        final int percent = (int) ((x / (float) y) * scale);
        final StringBuilder mesage = new StringBuilder(12 + scale + prefix.length() + suffix.length());
        mesage.append(ChatColor.WHITE);
        mesage.append(prefix).append(": [");
        mesage.append(ChatColor.GREEN);
        if (percent > 0) {
            mesage.append(stringRepeat(MobHealth.healthBarCharacter, percent));
        }
        mesage.append(ChatColor.RED);
        if (percent < scale) {
            mesage.append(stringRepeat(MobHealth.healthBarCharacter, (scale - percent)));
        }
        mesage.append(ChatColor.WHITE);
        mesage.append("]").append(suffix);
        return mesage.toString();
    }
    
    public static String stringRepeat(final String newString, final int n) {
        final StringBuilder builder = new StringBuilder(n * newString.length());
        for (int x = 0; x < n; x++) {
            builder.append(newString);
        }
        return builder.toString();
    }
    
    public MobHealthAPI getAPI(Plugin p) {
        return new MobHealthAPI(p);
    }
    
    public static String cleanName(String name) {
        if (name == null) { return name; }
        String newname = name;
        String searchcode = MobHealth.healthPrefix;
        if (newname.contains(searchcode)) {
            int loc = newname.indexOf(searchcode);
            int start = 0;
            if (newname.startsWith("Â§f")) {
                start = 2;
            }
            if (loc > -1) {
                newname = newname.substring(start, loc);
            }
        }
        return newname;
    }

	public Reader getTextResourcePublic(String string) {
		return getTextResource(string);
	}
}