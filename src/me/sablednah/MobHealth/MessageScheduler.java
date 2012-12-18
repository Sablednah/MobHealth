package me.sablednah.MobHealth;

import me.sablednah.MobHealth.API.MobHealthAPI;

import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Golem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.herocraftonline.heroes.api.events.*;

public class MessageScheduler implements Runnable {
	private Player						player;
	private EntityDamageByEntityEvent	damageEvent;
	private WeaponDamageEvent			weaponDamageEvent;
	private LivingEntity				targetMob;
	private Entity						damagerMob;
	public MobHealth					plugin;
	private int							HealthBefore;
	private int							DamageBefore;

	public MessageScheduler(Player shooter, EntityDamageByEntityEvent damageEvent, LivingEntity targetMob, int HealthBefore, int DamageBefore, MobHealth plugin) {
		this.plugin = plugin;
		this.damageEvent = damageEvent;
		this.player = shooter;
		this.targetMob = targetMob;
		this.HealthBefore = HealthBefore;
		this.DamageBefore = DamageBefore;
	}

	public MessageScheduler(Player shooter, WeaponDamageEvent weaponDamageEvent, LivingEntity targetMob, int HealthBefore, int DamageBefore, MobHealth plugin) {
		this.plugin = plugin;
		this.weaponDamageEvent = weaponDamageEvent;
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
		thisDamange = DamageBefore;

		mobsMaxHealth = API.getMobMaxHealth(targetMob);
		mobsHealth = API.getMobHealth(targetMob);
		if (mobsHealth < -50) { // hack to deal with mods using overkill
			mobsHealth = 0;
		}

		damageTaken = HealthBefore - mobsHealth;
		if (damageTaken > 9950) { // heroes hacky fix
			damageTaken = thisDamange;
		}
		damageResisted = thisDamange - damageTaken;

		// I need a Hero!
		if (weaponDamageEvent != null) {
			damagerMob = (Entity) weaponDamageEvent.getDamager().getEntity();
		} else {
			damagerMob = damageEvent.getDamager();
		}

		if (MobHealth.debugMode) {
			System.out.print("--");

			if (damageEvent != null) {
				System.out.print("[MobHealth] " + damageEvent.getDamage() + " damageEvent.getDamage();.");
			}
			if (weaponDamageEvent != null) {
				System.out.print("[MobHealth] " + weaponDamageEvent.getDamage() + " weaponDamageEvent.getDamage();.");
			}
			System.out.print("[MobHealth] " + DamageBefore + " DamageBefore.");
			System.out.print("[MobHealth] " + thisDamange + " thisDamange.");
			System.out.print("[MobHealth] " + mobsHealth + " mobsHealth.");
			System.out.print("[MobHealth] " + mobsMaxHealth + " mobsMaxHealth.");
			System.out.print("[MobHealth] " + HealthBefore + " HealthBefore.");
			System.out.print("[MobHealth] " + damageTaken + " damageTaken.");
			System.out.print("[MobHealth] " + damageResisted + " damageResisted.");
			System.out.print("[MobHealth] " + targetMob.getLastDamage() + " targetMob.getLastDamage().");
			System.out.print("[MobHealth] " + targetMob.getHealth() + " targetMob.getHealth().");
			System.out.print("[MobHealth] " + damagerMob + " damagerMob.");
		}

		String mobtype = API.getMobName(targetMob);

		if (targetMob instanceof Player) {
			isPlayer = true;
		} else {
			if (targetMob instanceof Animals || targetMob instanceof Ambient || targetMob instanceof WaterMob || targetMob instanceof Golem  || targetMob instanceof NPC ) {
				isAnimal = true;
			} else {
				isMonster = true;
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

		if (damagerMob instanceof Egg && (!(plugin.getLangConfig().getString("chatMessageEgg") == null))) {
			checkForZeroDamageHide = false;
		} else if (damagerMob instanceof Snowball && (!(plugin.getLangConfig().getString("chatMessageSnowball") == null))) {
			checkForZeroDamageHide = false;
		} else if ((MobHealth.hideNoDammage && (damageTaken > 0)) || !MobHealth.hideNoDammage) {
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
			if (checkForZeroDamageHide) {
				System.out.print("Hide " + damageTaken + " damage");
			} else {
				System.out.print("Show " + damageTaken + " damage");
			}
		}

		if (((MobHealth.disablePlayers && !isPlayer) || !MobHealth.disablePlayers) && ((MobHealth.disableMonsters && !isMonster) || !MobHealth.disableMonsters) && ((MobHealth.disableAnimals && !isAnimal) || !MobHealth.disableAnimals)
				&& (!checkForZeroDamageHide)) {

			API.showNotification(player, damageOutput, mobtype, mobsHealth, mobsMaxHealth, damagerMob);

		}
	}
}