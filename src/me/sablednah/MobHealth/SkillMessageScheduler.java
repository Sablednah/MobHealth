package me.sablednah.MobHealth;

import java.util.Arrays;

import me.sablednah.MobHealth.API.MobHealthAPI;
import me.sablednah.zombiemod.PutredineImmortui;
import me.sablednah.zombiemod.ZombieMod;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.herocraftonline.heroes.api.events.SkillDamageEvent;

public class SkillMessageScheduler implements Runnable {

	private Player				player;
	private SkillDamageEvent	skillDamageEvent;
	private LivingEntity		targetMob;
	public MobHealth			plugin;
	private int					HealthBefore;
	private int					DamageBefore;

	public SkillMessageScheduler(Player shooter, SkillDamageEvent skillDamageEvent, LivingEntity targetMob, int HealthBefore, int DamageBefore, MobHealth plugin) {
		this.plugin = plugin;
		this.skillDamageEvent = skillDamageEvent;
		this.player = shooter;
		this.targetMob = targetMob;
		this.HealthBefore = HealthBefore;
		this.DamageBefore = DamageBefore;
	}

	public void run() {

		int thisDamange = 0, mobsHealth = 0, mobsMaxHealth = 0, damageTaken = 0, damageResisted = 0;
		Boolean isPlayer = false, isMonster = false, isAnimal = false;
		String damageOutput;

		MobHealthAPI API = new MobHealthAPI(plugin);

		/*
		 * thisDamange = damageEvent.getDamage(); if (thisDamange > 200) { thisDamange = DamageBefore; }
		 */
		mobsMaxHealth = API.getMobMaxHealth(targetMob);
		mobsHealth = API.getMobHealth(targetMob);
		
		thisDamange = DamageBefore;
		damageTaken = HealthBefore - mobsHealth;
		if (damageTaken > 9950) { // heroes hacky fix
			damageTaken = thisDamange;
		}
		damageResisted = thisDamange - damageTaken;

		
		if (mobsHealth < -50) { // hack to deal with mods using overkill
			mobsHealth = 0;
		}

		String skillName = skillDamageEvent.getSkill().getName();

		if (MobHealth.debugMode) {
			System.out.print("--");
			System.out.print("[MobHealth] " + skillDamageEvent.getDamage() + " skillDamageEvent.getDamage();.");
			System.out.print("[MobHealth] " + DamageBefore + " DamageBefore.");
			System.out.print("[MobHealth] " + thisDamange + " thisDamange.");
			System.out.print("[MobHealth] " + mobsHealth + " mobsHealth.");
			System.out.print("[MobHealth] " + HealthBefore + " HealthBefore.");
			System.out.print("[MobHealth] " + damageTaken + " damageTaken.");
			System.out.print("[MobHealth] " + damageResisted + " damageResisted.");
			System.out.print("[MobHealth] " + targetMob.getLastDamage() + " targetMob.getLastDamage().");
		}

		String mobtype = new String(targetMob.getClass().getName());

		if (mobtype.indexOf("org.bukkit.craftbukkit.entity.Craft") == -1) {
			if (targetMob instanceof Player) {
				isPlayer = true;
				mobtype = ((Player) targetMob).getDisplayName();
			} else {
				System.out.print("[MobHealth] " + mobtype + " unknown.");
				mobtype = "unKn0wn";
			}
		} else {
			mobtype = mobtype.replaceAll("org.bukkit.craftbukkit.entity.Craft", "");
			if (Arrays.asList(MobHealth.animalList).contains(mobtype))
				isAnimal = true;
			if (Arrays.asList(MobHealth.monsterList).contains(mobtype))
				isMonster = true;
			if (MobHealth.entityLookup.get(mobtype) != null) {
				mobtype = MobHealth.entityLookup.get(mobtype);
			}
			// is entity tracked by ZombieMod.
			if (MobHealth.hasZM) {
				ZombieMod ZM = (ZombieMod) plugin.getServer().getPluginManager().getPlugin("ZombieMod");
				PutredineImmortui zomb = ZM.getZombie((Entity) targetMob);
				if (zomb != null) {
					mobtype = zomb.commonName;
				}
				zomb = null;
				ZM = null;
			}
		}

		switch (MobHealth.damageDisplayType) {
			case 4: // # 4: display damage taken (+amount resisted)
				damageOutput = Integer.toString(damageTaken);
				if (damageResisted > 0)
					damageOutput += "(+" + damageResisted + ")";
				break;
			case 3: // # 3: display damage inflicted (-amount resisted)
				damageOutput = Integer.toString(thisDamange);
				if (damageResisted > 0)
					damageOutput += "(-" + damageResisted + ")";
				break;
			case 2: // # 2: display damage taken.
				damageOutput = Integer.toString(damageTaken);
				break;
			default: // # 1: display damage inflicted.
				damageOutput = Integer.toString(thisDamange);
		}

		Boolean checkForZeroDamageHide = true;

		if ((MobHealth.hideNoDammage && (damageTaken > 0)) || !MobHealth.hideNoDammage) {
			checkForZeroDamageHide = false;
		}

		if (MobHealth.debugMode) {
			if (isPlayer) {
				System.out.print("Is Player");
			} else {
				System.out.print("Is not Player");
			}
			if (isAnimal) {
				System.out.print("Is Animal");
			} else {
				System.out.print("Is not Animal");
			}
			if (isMonster) {
				System.out.print("Is Monster");
			} else {
				System.out.print("Is not Monster");
			}
		}

		if (((MobHealth.disablePlayers && !isPlayer) || !MobHealth.disablePlayers) && ((MobHealth.disableMonsters && !isMonster) || !MobHealth.disableMonsters) && ((MobHealth.disableAnimals && !isAnimal) || !MobHealth.disableAnimals)
				&& (!checkForZeroDamageHide)) {
			API.showNotification(player, damageOutput, mobtype, mobsHealth, mobsMaxHealth, skillName);
		}
	}
}