package me.sablednah.MobHealth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import uk.co.jacekk.bukkit.baseplugin.v6.config.PluginConfigKey;
import uk.co.jacekk.bukkit.bloodmoon.BloodMoon;
import uk.co.jacekk.bukkit.bloodmoon.Config;


@SuppressWarnings("unused")
public class BloodClass {
	MobHealth plugin;
	
	public BloodClass(MobHealth p) {
		this.plugin = p;
	}
	
	public static int health(LivingEntity targetMob) {
		BloodMoon BM = (BloodMoon) Bukkit.getServer().getPluginManager().getPlugin("BloodMoon");
		if (BM.isActive(targetMob.getWorld().getName())) {
			if (BM.config.getBoolean(Config.FEATURE_DOUBLE_HEALTH_ENABLED)) {
				if (targetMob instanceof Creature && ((Creature) targetMob).getTarget() instanceof Player) {
					return targetMob.getHealth() * 2;
				}
			}
		}
		BM = null;
		return -1;
	}
	public static int maxhealth(LivingEntity targetMob) {
		BloodMoon BM = (BloodMoon) Bukkit.getServer().getPluginManager().getPlugin("BloodMoon");
		if (BM.isActive(targetMob.getWorld().getName())) {
			if (BM.config.getBoolean(Config.FEATURE_DOUBLE_HEALTH_ENABLED)) {
				if (targetMob instanceof Creature && ((Creature) targetMob).getTarget() instanceof Player) {
					return targetMob.getMaxHealth() * 2;
				}
			}
		}
		BM = null;
		return -1;
		
	}
}
