package main.java.me.sablednah.MobHealth;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map.Entry;


import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SaveToggle {
	public static void save(HashMap<Player, Boolean> pluginEnabled,String path) throws Exception {

		for (Entry<Player, Boolean> entry : pluginEnabled.entrySet()){
			if (entry.getValue() != null && entry.getKey() != null) {
				getPlayerConfig().set("players." + entry.getKey().getName(), entry.getValue());
			}
		}
		getPlayerConfig().save(MobHealth.PlayerConfigurationFile);
	}

	public static HashMap<Player,Boolean> load(String path) throws Exception {
		HashMap<Player, Boolean> out = new HashMap<Player,Boolean>();
		if (getPlayerConfig().getConfigurationSection("players") != null) {
			for (String key : getPlayerConfig().getConfigurationSection("players").getKeys(false)) {
				out.put(Bukkit.getServer().getPlayerExact(key), getPlayerConfig().getBoolean("players." + key));
			}
			return out;
		} else {
			return new HashMap<Player,Boolean>();
		}
	}

	public static void reloadPlayerConfig() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("MobHealth");
		if (MobHealth.PlayerConfigurationFile == null) {
			MobHealth.PlayerConfigurationFile = new File(plugin.getDataFolder(), "players.yml");
		}
		MobHealth.PlayerConfig = YamlConfiguration.loadConfiguration(MobHealth.PlayerConfigurationFile);
		MobHealth.PlayerConfig.options().copyDefaults(true);

		// Look for defaults in the jar
		InputStream defConfigStream = plugin.getResource("players.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration .loadConfiguration(defConfigStream);
			MobHealth.PlayerConfig.setDefaults(defConfig);
		}
	}

	public static FileConfiguration getPlayerConfig() {
		if (MobHealth.PlayerConfig == null) {
			reloadPlayerConfig();
		}
		return MobHealth.PlayerConfig;
	}

	public void savePlayeronfig() {
		if (MobHealth.PlayerConfig == null || MobHealth.PlayerConfigurationFile == null) {
			return;
		}
		try {
			MobHealth.PlayerConfig.save(MobHealth.PlayerConfigurationFile);
		} catch (IOException ex) {
			MobHealth.logger.severe("Could not save Player config to " + MobHealth.PlayerConfigurationFile + " " + ex);
		}
	}
}