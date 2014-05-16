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

package main.java.me.sablednah.MobHealth.API;

import java.util.List;

import main.java.me.sablednah.MobHealth.MobHealth;
import main.java.me.sablednah.MobHealth.SpoutNotifications;
import me.sablednah.zombiemod.PutredineImmortui;
import me.sablednah.zombiemod.ZombieMod;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.plugin.Plugin;

import org.getspout.spoutapi.SpoutManager;

import blainicus.MonsterApocalypse.MonsterApocalypse;
import blainicus.MonsterApocalypse.healthmanager;

import cam.entity.LabEntity;
import cam.entity.LabEntityData;
import cam.entity.LabEntityManager;

import com.mcdr.corruption.CorruptionAPI;

import com.garbagemule.MobArena.MobArenaHandler;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.Wave;
import com.garbagemule.MobArena.waves.WaveManager;

public class MobHealthAPI {

	private MobHealth	plugin;

	public MobHealthAPI(Plugin p) {
		this.plugin = (MobHealth) p;
	}

	public void showNotification(Player player, String damageOutput, String mobtype, int mobsHealth, int mobsMaxHealth, String skillName) {
		showNotification(player, damageOutput, mobtype, mobsHealth, mobsMaxHealth, skillName, null);
	}

	public void showNotification(Player player, String damageOutput, String mobtype, int mobsHealth, int mobsMaxHealth, Entity damagerMob) {
		showNotification(player, damageOutput, mobtype, mobsHealth, mobsMaxHealth, null, damagerMob);
	}

	public void showNotification(Player player, String damageOutput, String mobtype, int mobsHealth, int mobsMaxHealth, String skillName, Entity damagerMob) {

		boolean spoutUsed = false;

		if (!MobHealth.disableSpout || MobHealth.showRPG || MobHealth.showSideNotification) {
			if (player.getServer().getPluginManager().isPluginEnabled("Spout")) {
				if (MobHealth.debugMode) {
					System.out.print("SpoutPlugin detected");
				}
				if (SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
					if (MobHealth.debugMode) {
						System.out.print("SpoutCraftEnabled");
					}

					String title, message = "";
					Material icon;

					if (skillName != null) {
						icon = Material.BLAZE_POWDER;
						title = MobHealth.heroesSkillSpoutDamageTitle;
						title = title.replaceAll("%S", skillName);

						if (mobsHealth < 1) { // was "targetMob.isDead()" removed to avoid handing target entity over.
							message = MobHealth.heroesSkillSpoutKilledMessage;
						} else {
							message = MobHealth.heroesSkillSpoutDamageMessage;
						}

					} else {

						if (damagerMob instanceof Projectile) {
							if (damagerMob instanceof Egg) {
								icon = Material.EGG;
							} else if (damagerMob instanceof Snowball) {
								icon = Material.SNOW_BALL;
							} else if (damagerMob instanceof ThrownPotion) {
								icon = Material.POTION;
								// 16396 splash damage
							} else {
								icon = Material.ARROW; // arrow
							}
						} else if (damagerMob instanceof Tameable) {
							if (damagerMob instanceof Wolf) {
								icon = Material.BONE;
							} else if (damagerMob instanceof Ocelot) {
								icon = Material.RAW_FISH;
							} else {
								icon = Material.BONE; // arrow
							}
						} else {
							icon = player.getItemInHand().getType();
							if (icon == null || icon == Material.AIR) {
								icon = Material.STICK; // was diamond sword 276
							}
						}
						if (damagerMob instanceof Egg && (!(MobHealth.spoutEggTitle == null))) {
							title = MobHealth.spoutEggTitle;
							message = MobHealth.spoutEggMessage;
						} else if (damagerMob instanceof Snowball && (!(MobHealth.spoutSnowballTitle == null))) {
							title = MobHealth.spoutSnowballTitle;
							message = MobHealth.spoutSnowballMessage;
						} else {
							title = MobHealth.spoutDamageTitle;
							if (mobsHealth < 1) { // was "targetMob.isDead()"
								message = MobHealth.spoutKilledMessage;
							} else {
								message = MobHealth.spoutDamageMessage;
							}
						}
					}

					title = title.replaceAll("%D", damageOutput);
					title = title.replaceAll("%N", mobtype);
					title = title.replaceAll("%M", Integer.toString(mobsMaxHealth));
					title = ChatColor.translateAlternateColorCodes('&', title);

					if ((mobsHealth < 2) || (mobsHealth <= (mobsMaxHealth / 4))) {
						message = message.replaceAll("%H", (ChatColor.DARK_RED) + Integer.toString(mobsHealth) + (ChatColor.WHITE));
					} else {
						message = message.replaceAll("%H", Integer.toString(mobsHealth));
					}
					message = message.replaceAll("%D", damageOutput);
					message = message.replaceAll("%N", mobtype);
					message = message.replaceAll("%M", Integer.toString(mobsMaxHealth));
					message = ChatColor.translateAlternateColorCodes('&', message);

					if (!MobHealth.disableSpout) {
						if (MobHealth.debugMode) {
							System.out.print("attempting notification");
						}
						spoutUsed = SpoutNotifications.showAchievement(player, title, message, icon);
						if (MobHealth.debugMode) {
							System.out.print("spoutUsed = " + spoutUsed);
						}

					}

					if (MobHealth.showRPG) {
						String rpg = MobHealth.RPGnotify;
						for (int chatcntr2 = 0; chatcntr2 < 16; chatcntr2++) {
							rpg = rpg.replaceAll("&" + Integer.toHexString(chatcntr2), (ChatColor.getByChar(Integer.toHexString(chatcntr2))) + "");
						}
						if (skillName != null) {
							rpg = rpg.replaceAll("%S", skillName);
						} else {
							rpg = rpg.replaceAll("%S", "");
						}
						rpg = rpg.replaceAll("%D", damageOutput);
						rpg = rpg.replaceAll("%N", mobtype);
						rpg = rpg.replaceAll("%M", Integer.toString(mobsMaxHealth));
						rpg = rpg.replaceAll("%H", Integer.toString(mobsHealth));
						rpg = rpg.trim();
						rpg = ChatColor.translateAlternateColorCodes('&', rpg);

						spoutUsed = SpoutNotifications.showRPG(player, rpg, icon);
					}

					if (MobHealth.showSideNotification) {
						spoutUsed = SpoutNotifications.showSideWidget(player, title, message, icon);
					}
				}
			}
		}

		if (spoutUsed) {
			MobHealth.notifications++;
		}

		boolean useSimpleNotice = player.getListeningPluginChannels().contains("SimpleNotice");
		if (!spoutUsed && (!MobHealth.disableChat || useSimpleNotice)) {
			String ChatMessage;
			if (damagerMob instanceof Egg && (!(MobHealth.chatMessageEgg == null))) {
				ChatMessage = MobHealth.chatMessageEgg;
			} else if (damagerMob instanceof Snowball && (!(MobHealth.chatMessageSnowball == null))) {
				ChatMessage = MobHealth.chatMessageSnowball;
			} else {
				if (mobsHealth < 1) { // was "targetMob.isDead()"
					if (skillName == null) {
						ChatMessage = MobHealth.chatKilledMessage;
					} else {
						ChatMessage = MobHealth.heroesSkillChatKilledMessage;
						ChatMessage = ChatMessage.replaceAll("%S", skillName);
					}
				} else {
					if (skillName == null) {
						ChatMessage = MobHealth.chatMessage;
					} else {
						ChatMessage = MobHealth.heroesSkillChatMessage;
						ChatMessage = ChatMessage.replaceAll("%S", skillName);
					}
					if ((mobsHealth < 2) || (mobsHealth <= (mobsMaxHealth / 4))) {
						ChatMessage = ChatMessage.replaceAll("%H", (ChatColor.DARK_RED) + Integer.toString(mobsHealth) + (ChatColor.WHITE));
					} else {
						ChatMessage = ChatMessage.replaceAll("%H", Integer.toString(mobsHealth));
					}
				}
			}
			ChatMessage = ChatMessage.replaceAll("%D", damageOutput);
			ChatMessage = ChatMessage.replaceAll("%N", mobtype);
			ChatMessage = ChatMessage.replaceAll("%M", Integer.toString(mobsMaxHealth));
			ChatMessage = ChatColor.translateAlternateColorCodes('&', ChatMessage);

			if (useSimpleNotice) {
				player.sendPluginMessage(plugin, "SimpleNotice", ChatMessage.getBytes(java.nio.charset.Charset.forName("UTF-8")));
			} else {
				player.sendMessage(ChatMessage);
			}
			MobHealth.notifications++;
		}

	}

	/**
	 * @return Notifications...
	 */
	public int getNotifications() {
		return MobHealth.notifications;
	}

	/**
	 * Get the current health level of an LivingEntity.
	 * 
	 * @param targetMob
	 *            LivingEntity of which to get health.
	 * @return
	 */
	public int getMobHealth(LivingEntity targetMob) {
		int targetHealth = 0;

		targetHealth = (int) targetMob.getHealth();

		if (MobHealth.hasMA) {
			MonsterApocalypse ma = (MonsterApocalypse) plugin.getServer().getPluginManager().getPlugin("Monster Apocalypse");
			healthmanager MAHealthManager = ma.getHealthManager();
			if (MAHealthManager != null) {
				targetHealth = (int) MAHealthManager.getmobhp(targetMob);
			}
			MAHealthManager = null;
			ma = null;
		}
		if (MobHealth.hasMobArena) {
			MobArenaHandler maHandler = new MobArenaHandler();
			// Arena arena = maHandler.getArenaWithPlayer(playa);
			Arena arena = maHandler.getArenaWithMonster(targetMob);
			if (arena != null) {
				MABoss thisBoss = arena.getMonsterManager().getBoss(targetMob);
				if (thisBoss != null) {
					targetHealth = (int) thisBoss.getHealth();
				}
				thisBoss = null;
			}
			arena = null;
			maHandler = null;
		}

		return targetHealth;
	}

	public int getMobMaxHealth(LivingEntity targetMob) {
		int targetMaxHealth = 0;

		targetMaxHealth = (int) targetMob.getMaxHealth();

		if (MobHealth.hasMA) {
			MonsterApocalypse ma = (MonsterApocalypse) plugin.getServer().getPluginManager().getPlugin("Monster Apocalypse");
			if (ma != null) {
				List<?> worldlist = ma.getConfig().getList("Worlds");
				if (worldlist.contains(targetMob.getWorld().getName())) {
					targetMaxHealth = ma.getMobHealth(targetMob);
				}
				worldlist = null;
			}
			ma = null;
		}
		if (MobHealth.hasMobArena) {
			MobArenaHandler maHandler = new MobArenaHandler();
			// Arena arena = maHandler.getArenaWithPlayer(playa);
			Arena arena = maHandler.getArenaWithMonster(targetMob);
			if (arena != null) {
				MABoss thisBoss = arena.getMonsterManager().getBoss(targetMob);
				if (thisBoss != null) {
					targetMaxHealth = (int) targetMob.getMaxHealth();
				} else {
					WaveManager wm = arena.getWaveManager();
					Wave thisWave = wm.getCurrent();
					if (thisWave != null) {
						targetMaxHealth = (int) (targetMob.getMaxHealth() * thisWave.getHealthMultiplier());
					} else {
						targetMaxHealth = (int) targetMob.getMaxHealth();
					}
				}
				thisBoss = null;
			}
			arena = null;
			maHandler = null;
		}

		return targetMaxHealth;
	}

	public String getMobName(LivingEntity targetMob) {
		String mobtype;
		String trackedname = MobHealth.getMetaCustomName(targetMob);
System.out.print("trackedname: "+trackedname);
		if (trackedname == null || !trackedname.isEmpty()) {
			if (targetMob instanceof Player) {
				mobtype = ((Player) targetMob).getDisplayName();
			} else if (targetMob instanceof Zombie) {
				if (targetMob instanceof PigZombie) {
					mobtype = "PigZombie";
				} else {
					mobtype = "Zombie";
				}
				if (((Zombie) targetMob).isVillager()) {
					mobtype = mobtype + "Villager";
				}
				if (((Zombie) targetMob).isBaby()) {
					mobtype = mobtype + "Baby";
				}
			} else if (targetMob instanceof Skeleton) {
				mobtype = "Skeleton";
				if (((Skeleton) targetMob).getSkeletonType() == SkeletonType.WITHER) {
					mobtype = mobtype + "Wither";
				}
			} else if (targetMob instanceof Horse) {
				mobtype = "Horse";
				Variant typ = ((Horse) targetMob).getVariant();
				switch (typ) {
					case HORSE:
						mobtype = "Horse";
						break;
					case SKELETON_HORSE:
						mobtype = "Skeleton Horse";
						break;
					case UNDEAD_HORSE:
						mobtype = "Undead Horse";
						break;
					case DONKEY:
						mobtype = "Doney";
						break;
					case MULE:
						mobtype = "Mule";
						break;
				}

			} else {
				mobtype = new String(targetMob.getClass().getSimpleName());
				mobtype = mobtype.replaceAll("Craft", "");
			}

			if (MobHealth.entityLookup.get(mobtype) != null) {
				mobtype = MobHealth.entityLookup.get(mobtype);
			}

			if (targetMob.getCustomName() != null) {
				mobtype = MobHealth.cleanName(targetMob.getCustomName());
			}

			// is entity tracked by plugin.
			if (MobHealth.hasZM) {
				ZombieMod ZM = (ZombieMod) plugin.getServer().getPluginManager().getPlugin("ZombieMod");
				PutredineImmortui zomb = ZM.getZombie((Entity) targetMob);
				if (zomb != null) {
					mobtype = zomb.commonName;
				}
				zomb = null;
				ZM = null;
			}

			if (MobHealth.hasLikeABoss) {
				LabEntity le = LabEntityManager.getLabEntity(targetMob);
				if (le != null) {
					LabEntityData thisData = le.getLabEntityData();
					if (thisData != null) {
						mobtype = thisData.getName();
					}
					thisData = null;
				}
				le = null;
			}

			if (MobHealth.hasCorruption) {
				if (CorruptionAPI.isBoss(targetMob)) {
					mobtype = CorruptionAPI.getName(targetMob);
				}
			}
			
			MobHealth.setMetaCustomName(targetMob, mobtype);			
			return mobtype;

		} else {
			return trackedname;
		}
	}

	public void showBar(LivingEntity targetMob) {
		if (targetMob != null) {
			if (targetMob instanceof Player) {
				if (MobHealth.showPlayerHeadHealth) {
					if (MobHealth.setHealths != null) {
						MobHealth.setHealths.setPlayer((Player) targetMob);
					}
				}
			} else {
				if (MobHealth.showMobHeadHealth) {
					Boolean showBar = true;
					if (targetMob instanceof Villager) {
						if (MobHealth.hideBarForVillager) {
							showBar = false;
						}
					} else if (targetMob instanceof Animals) {
						if (MobHealth.hideBarForAnimal) {
							showBar = false;
						}
					}
					if (targetMob.hasMetadata("NPC")) {
						if (MobHealth.hideBarForNPC) {
							showBar = false;
						}
					}
					String thisType = targetMob.getType().toString();
					if (MobHealth.forceBarHide.contains(thisType)) {
						showBar = false;
					}

					if (showBar) {
						String headText = null;
						int maxHealth = getMobMaxHealth(targetMob);
						int health = getMobHealth(targetMob);
						if (MobHealth.useBarForMobs) {
							headText = MobHealth.barGraph(health, maxHealth, MobHealth.healthBarSize, getMobName(targetMob) + MobHealth.healthPrefix, "");

						} else {
							headText = getMobName(targetMob) + MobHealth.healthPrefix + " " + health + "/" + maxHealth;
						}
						targetMob.setCustomName(headText);
						if (MobHealth.alwaysVisable) {
							targetMob.setCustomNameVisible(true);
						} else {
							targetMob.setCustomNameVisible(false);
						}
					}
				}
			}
		}
	}
}
