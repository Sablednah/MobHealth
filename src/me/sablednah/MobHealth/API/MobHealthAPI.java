package me.sablednah.MobHealth.API;

import java.util.List;

import me.coldandtired.api.Mob;
import me.coldandtired.mobs.Main;
import me.sablednah.MobHealth.MobHealth;
import me.sablednah.MobHealth.SpoutNotifications;
import me.sablednah.zombiemod.PutredineImmortui;
import me.sablednah.zombiemod.ZombieMod;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.SpoutManager;

import uk.co.jacekk.bukkit.bloodmoon.BloodMoon;

import blainicus.MonsterApocalypse.MonsterApocalypse;
import blainicus.MonsterApocalypse.healthmanager;
import cam.Likeaboss;
import cam.boss.Boss;
import cam.boss.BossData;
import cam.boss.BossManager;

import com.garbagemule.MobArena.MobArenaHandler;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.Wave;
import com.garbagemule.MobArena.waves.WaveManager;
import com.herocraftonline.heroes.Heroes;

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
						icon = Material.getMaterial(377);
						title = plugin.getLangConfig().getString("heroesSkillSpoutDamageTitle");
						title = title.replaceAll("%S", skillName);

						if (mobsHealth < 1) { // was "targetMob.isDead()" removed to avoid handing target entity over.
							message = plugin.getLangConfig().getString("heroesSkillSpoutKilledMessage");
						} else {
							message = plugin.getLangConfig().getString("heroesSkillSpoutDamageMessage");
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
						if (damagerMob instanceof Egg && (!(plugin.getLangConfig().getString("spoutEggTitle") == null))) {
							title = plugin.getLangConfig().getString("spoutEggTitle");
							message = plugin.getLangConfig().getString("spoutEggMessage");
						} else if (damagerMob instanceof Snowball && (!(plugin.getLangConfig().getString("spoutSnowballTitle") == null))) {
							title = plugin.getLangConfig().getString("spoutSnowballTitle");
							message = plugin.getLangConfig().getString("spoutSnowballMessage");
						} else {
							title = plugin.getLangConfig().getString("spoutDamageTitle");
							if (mobsHealth < 1) { // was "targetMob.isDead()"
								message = plugin.getLangConfig().getString("spoutKilledMessage");
							} else {
								message = plugin.getLangConfig().getString("spoutDamageMessage");
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
						rpg = rpg.replaceAll("%D", damageOutput);
						rpg = rpg.replaceAll("%N", mobtype);
						rpg = rpg.replaceAll("%M", Integer.toString(mobsMaxHealth));
						rpg = rpg.replaceAll("%H", Integer.toString(mobsHealth));
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
			if (damagerMob instanceof Egg && (!(plugin.getLangConfig().getString("chatMessageEgg") == null))) {
				ChatMessage = plugin.getLangConfig().getString("chatMessageEgg");
			} else if (damagerMob instanceof Snowball && (!(plugin.getLangConfig().getString("chatMessageSnowball") == null))) {
				ChatMessage = plugin.getLangConfig().getString("chatMessageSnowball");
			} else {
				if (mobsHealth < 1) { // was "targetMob.isDead()"
					ChatMessage = plugin.getLangConfig().getString("chatKilledMessage");
				} else {
					ChatMessage = plugin.getLangConfig().getString("chatMessage");
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

		targetHealth = targetMob.getHealth();

		if (MobHealth.hasMobs) {
			Main mobs = (Main) plugin.getServer().getPluginManager().getPlugin("Mobs");
			Mob mob = mobs.get_mob((Entity) targetMob);
			if (mob != null) {
				if (mob.getHp() != null) {
					targetHealth = mob.getHp().intValue();
				}
			}
			mob = null;
			mobs = null;
		}
		if (MobHealth.hasMA) {
			MonsterApocalypse ma = (MonsterApocalypse) plugin.getServer().getPluginManager().getPlugin("Monster Apocalypse");
			healthmanager MAHealthManager = ma.getHealthManager();
			if (MAHealthManager != null) {
				targetHealth = MAHealthManager.getmobhp(targetMob);
			}
			MAHealthManager = null;
			ma = null;
		}
		if (MobHealth.hasLikeABoss) {
			Likeaboss LaB = (Likeaboss) plugin.getServer().getPluginManager().getPlugin("Likeaboss");
			BossManager BM = LaB.getBossManager();
			if (BM != null) {
				Boss thisBoss = BM.getBoss(targetMob);
				if (thisBoss != null) {
					targetHealth = thisBoss.getHealth();
				}
				thisBoss = null;
			}
			BM = null;
			LaB = null;
		}
		if (MobHealth.hasMobArena) {
			MobArenaHandler maHandler = new MobArenaHandler();
			// Arena arena = maHandler.getArenaWithPlayer(playa);
			Arena arena = maHandler.getArenaWithMonster(targetMob);
			if (arena != null) {
				MABoss thisBoss = arena.getMonsterManager().getBoss(targetMob);
				if (thisBoss != null) {
					targetHealth = thisBoss.getHealth();
				}
				thisBoss = null;
			}
			arena = null;
			maHandler = null;
		}
		if (MobHealth.hasHeroes) {
			Heroes heroes = (Heroes) plugin.getServer().getPluginManager().getPlugin("Heroes");
			if (heroes != null) {
				targetHealth = heroes.getDamageManager().getHealth(targetMob);
			}
			heroes = null;
		}
		if (MobHealth.hasZM) {
			ZombieMod ZM = (ZombieMod) plugin.getServer().getPluginManager().getPlugin("ZombieMod");
			PutredineImmortui zomb = ZM.getZombie((Entity) targetMob);
			if (zomb != null) {
				targetHealth = zomb.health;
			}
			zomb = null;
			ZM = null;
		}

		
		if (MobHealth.hasBloodMoon) {
			BloodMoon BM = (BloodMoon) plugin.getServer().getPluginManager().getPlugin("BloodMoon");
			if (BM.isActive(targetMob.getWorld().getName())) {
				if (BM.config.getBoolean(uk.co.jacekk.bukkit.bloodmoon.Config.FEATURE_DOUBLE_HEALTH_ENABLED)) {
					if (targetMob instanceof Creature && ((Creature) targetMob).getTarget() instanceof Player) {
						targetHealth = targetMob.getHealth() * 2;
					}
				}
			}
			BM = null;
		}
		return targetHealth;
	}

	public int getMobMaxHealth(LivingEntity targetMob) {
		int targetMaxHealth = 0;

		targetMaxHealth = targetMob.getMaxHealth();

		if (MobHealth.hasMobs) {
			Main mobs = (Main) plugin.getServer().getPluginManager().getPlugin("Mobs");
			Mob mob = mobs.get_mob((Entity) targetMob);
			if (mob != null) {
				if (mob.getHp() != null) {
					targetMaxHealth = mob.getMax_hp().intValue();
				}
			}
			mob = null;
			mobs = null;
		}
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
		if (MobHealth.hasLikeABoss) {
			Likeaboss LaB = (Likeaboss) plugin.getServer().getPluginManager().getPlugin("Likeaboss");
			BossManager BM = LaB.getBossManager();
			if (BM != null) {
				Boss thisBoss = BM.getBoss(targetMob);
				if (thisBoss != null) {
					targetMaxHealth = targetMob.getMaxHealth();
					BossData bd = thisBoss.getBossData();
					double hc = bd.getHealthCoef();
					targetMaxHealth = (int) (hc * targetMaxHealth);
					bd = null;
				}
				thisBoss = null;
			}
			BM = null;
			LaB = null;
		}
		if (MobHealth.hasMobArena) {
			MobArenaHandler maHandler = new MobArenaHandler();
			// Arena arena = maHandler.getArenaWithPlayer(playa);
			Arena arena = maHandler.getArenaWithMonster(targetMob);
			if (arena != null) {
				MABoss thisBoss = arena.getMonsterManager().getBoss(targetMob);
				if (thisBoss != null) {
					targetMaxHealth = thisBoss.getMaxHealth();
				} else {
					WaveManager wm = arena.getWaveManager();
					Wave thisWave = wm.getCurrent();
					if (thisWave != null) {
						targetMaxHealth = (int) (targetMob.getMaxHealth() * thisWave.getHealthMultiplier());
					} else {
						targetMaxHealth = targetMob.getMaxHealth();
					}
				}
				thisBoss = null;
			}
			arena = null;
			maHandler = null;
		}
		if (MobHealth.hasHeroes) {
			Heroes heroes = (Heroes) plugin.getServer().getPluginManager().getPlugin("Heroes");
			if (heroes != null) {
				targetMaxHealth = heroes.getDamageManager().getMaxHealth(targetMob);
			}
			heroes = null;
		}
		if (MobHealth.hasZM) {
			ZombieMod ZM = (ZombieMod) plugin.getServer().getPluginManager().getPlugin("ZombieMod");
			PutredineImmortui zomb = ZM.getZombie((Entity) targetMob);
			if (zomb != null) {
				targetMaxHealth = zomb.maxHealth;
			}
			zomb = null;
			ZM = null;
		}
		if (MobHealth.hasBloodMoon) {
			BloodMoon BM = (BloodMoon) plugin.getServer().getPluginManager().getPlugin("BloodMoon");
			if (BM.isActive(targetMob.getWorld().getName())) {
				if (BM.config.getBoolean(uk.co.jacekk.bukkit.bloodmoon.Config.FEATURE_DOUBLE_HEALTH_ENABLED)) {
					if (targetMob instanceof Creature && ((Creature) targetMob).getTarget() instanceof Player) {
						targetMaxHealth = targetMob.getMaxHealth() * 2;
					}
				}
			}
			BM = null;
		}
		return targetMaxHealth;
	}
}
