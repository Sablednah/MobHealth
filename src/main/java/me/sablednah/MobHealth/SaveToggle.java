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