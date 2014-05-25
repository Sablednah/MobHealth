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

import main.java.me.sablednah.MobHealth.API.MobHealthAPI;
import main.java.me.sablednah.MobHealth.API.PlayerHealthEvent;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class SetHealth {

	public MobHealth				plugin;
	public MobHealthAPI				API			= null;
	public static ScoreboardManager	manager		= null;
	public static Scoreboard		globalboard	= null;
	Objective						objective	= null;

	public SetHealth(MobHealth p) {
		this.plugin = p;
		this.API = new MobHealthAPI(plugin);
		manager = plugin.getServer().getScoreboardManager();
		globalboard = manager.getNewScoreboard();
		objective = globalboard.registerNewObjective("mobhealth", "dummy");

		// objective = globalboard.getObjective("mobhealth");

		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		if (MobHealth.usePercentForPlayer) {
			objective.setDisplayName(MobHealth.playerLabelPercent);
		} else {
			objective.setDisplayName(MobHealth.playerLabel);
		}
		for (Player online : plugin.getServer().getOnlinePlayers()) {
			setPlayer(online);
		}
	}

	@SuppressWarnings("deprecation")
	public void setPlayer(Player p) {
		if (p.isOnline()) {
			int maxHealth = API.getMobMaxHealth(p);
			int health = API.getMobHealth(p);
			Score score = null;
			try {
				String name = p.getName();
				if (name.length() > 16) {
					name = name.substring(0, 16);
				}
				score = objective.getScore(name);
			} catch (NoSuchMethodError e) {
				score = objective.getScore(p);
			}
			PlayerHealthEvent e = new PlayerHealthEvent(health, maxHealth, globalboard, objective, score);
			plugin.getServer().getPluginManager().callEvent(e);
			if (e.isCancelled()) {
				return;
			}
			score = e.getScore();
			objective = e.getObjective();

			p.setScoreboard(globalboard);

			int value = health;
			if (MobHealth.usePercentForPlayer) {
				value = (int) ((health / (float) maxHealth) * 100);
			}
			score.setScore(value);
		}
	}

	public void removePlayer(Player p) {
		// healthInfo.remove(p);
	}
}
