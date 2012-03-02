package me.sablednah.MobHealth;

import java.util.Arrays;

import me.coldandtired.mobs.Main;
import me.coldandtired.mobs.Mob;

import org.bukkit.Material;

import org.bukkit.ChatColor;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import org.getspout.spoutapi.SpoutManager;

import com.garbagemule.MobArena.MobArenaHandler;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.Wave;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.api.WeaponDamageEvent;

import cam.Likeaboss;
import cam.boss.Boss;
import cam.boss.BossManager;



public class MessageScheduler implements Runnable {
	private Player player;
	private EntityDamageByEntityEvent damageEvent;
	private WeaponDamageEvent weaponDamageEvent;
	private LivingEntity targetMob;
	private Entity damagerMob;
	public MobHealth plugin;
	private int HealthBefore;
	private int DamageBefore;

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

		int thisDamange=0, mobsHealth=0, mobsMaxHealth=0, damageTaken=0, damageResisted=0;
		Boolean isPlayer = false, isMonster = false, isAnimal = false, isSpecial =false;
		String damageOutput;

		// Get health/maxhealth and damage for Likeaboss Boss entities
		if (MobHealth.hasLikeABoss) {
			Likeaboss LaB=(Likeaboss) plugin.getServer().getPluginManager().getPlugin("Likeaboss");
			BossManager BM=LaB.getBossManager();
			Boss thisBoss = BM.getBoss(targetMob);
			if(!(thisBoss == null))  {
				isSpecial=true;
				thisDamange = DamageBefore;
				mobsMaxHealth = targetMob.getMaxHealth();
				mobsMaxHealth = (int) (thisBoss.getBossData().getHealthCoef()*mobsMaxHealth);
				mobsHealth = thisBoss.getHealth();
				damageTaken = HealthBefore - mobsHealth;
				damageResisted = thisDamange - damageTaken;
			}
			thisBoss = null;
			BM = null;
			LaB = null;
		} 
		
		//Check if target is in a MobArena.
		if (MobHealth.hasMobArena) {
			MobArenaHandler maHandler = new MobArenaHandler();
			Arena arena = maHandler.getArenaWithPlayer(player);

			if (arena != null) {
				if (targetMob instanceof LivingEntity && maHandler.isMonsterInArena(targetMob)) {
					isSpecial=true;

					if (arena !=null) {
						MABoss thisBoss = arena.getMonsterManager().getBoss(targetMob);
						if (thisBoss != null) {

							thisDamange = DamageBefore;
							mobsMaxHealth=thisBoss.getMaxHealth();
							mobsHealth=thisBoss.getHealth();
							damageTaken = HealthBefore - mobsHealth;
							damageResisted=0;

						} else {

							Wave thisWave=arena.getWave();
							mobsMaxHealth=(int) (targetMob.getMaxHealth()*thisWave.getHealthMultiplier());
							if (damageEvent != null) {
								thisDamange = damageEvent.getDamage();
							} else {
								thisDamange = weaponDamageEvent.getDamage();
							}
							mobsHealth = targetMob.getHealth();
							damageTaken = thisDamange; //HealthBefore - mobsHealth;
							damageResisted = thisDamange - damageTaken;

						}
					}

				} else if (maHandler.isPetInArena(targetMob)) {
					return;  // cancel notification
				}
			}
			arena = null;
			maHandler = null;

		}
		
		//is entity tracked by mob-health.
		if (MobHealth.hasMobs) {
			Main mobs=(Main) plugin.getServer().getPluginManager().getPlugin("Mobs");
			Mob mob = mobs.get_mob(targetMob);
			if (mob != null) {
				isSpecial=true;
				thisDamange = DamageBefore;
				mobsMaxHealth = mob.max_hp;
				mobsHealth = mob.hp;
				damageTaken = HealthBefore - mobsHealth;
				damageResisted = thisDamange - damageTaken;
			}
			mob = null;
			mobs = null;
		}
		
		//I need a Hero!
		if (weaponDamageEvent != null) {
			Heroes heroes = (Heroes) plugin.getServer().getPluginManager().getPlugin("Heroes");
			isSpecial=true;
			thisDamange = weaponDamageEvent.getDamage();
			mobsMaxHealth = heroes.getDamageManager().getEntityMaxHealth(targetMob);
			if (targetMob.isDead()) {
				mobsHealth = HealthBefore-thisDamange;
			} else {
				mobsHealth = heroes.getDamageManager().getEntityHealth(targetMob);
			}
			damageTaken = HealthBefore - mobsHealth;
			damageResisted = thisDamange - damageTaken;
			damagerMob = weaponDamageEvent.getDamager();
			heroes = null;
		}



		// if none of the above special cases for 3rd party plugins apply - get the info 'normally'.
		if (!isSpecial) {
			thisDamange = damageEvent.getDamage();
			mobsMaxHealth = targetMob.getMaxHealth();
			mobsHealth = targetMob.getHealth();
			damageTaken = HealthBefore - mobsHealth;
			damageResisted = thisDamange - damageTaken;
			damagerMob = damageEvent.getDamager();
		}

		if (MobHealth.debugMode) {
			System.out.print("--");

			if (damageEvent != null) { System.out.print("[MobHealth] " + damageEvent.getDamage() +" damageEvent.getDamage();."); }
			if (weaponDamageEvent != null) { System.out.print("[MobHealth] " + weaponDamageEvent.getDamage() +" weaponDamageEvent.getDamage();."); }
			System.out.print("[MobHealth] " + DamageBefore +" DamageBefore.");
			System.out.print("[MobHealth] " + thisDamange +" thisDamange.");
			System.out.print("[MobHealth] " + mobsHealth +" mobsHealth.");
			System.out.print("[MobHealth] " + HealthBefore +" HealthBefore.");
			System.out.print("[MobHealth] " + damageTaken +" damageTaken.");
			System.out.print("[MobHealth] " + damageResisted +" damageResisted.");
			System.out.print("[MobHealth] " + targetMob.getLastDamage() +" targetMob.getLastDamage().");
		}

		String mobtype = new String(targetMob.getClass().getName());

		if (mobtype.indexOf("org.bukkit.craftbukkit.entity.Craft") == -1) {
			if (targetMob instanceof Player) {
				isPlayer=true;
				mobtype=((Player) targetMob).getDisplayName();
			} else {
				System.out.print("[MobHealth] " + mobtype +" unknown.");
				mobtype="unKn0wn";
			}
		} else {
			mobtype=mobtype.replaceAll("org.bukkit.craftbukkit.entity.Craft", "");
			if (Arrays.asList(MobHealth.animalList).contains(mobtype)) isAnimal=true;
			if (Arrays.asList(MobHealth.monsterList).contains(mobtype)) isMonster=true;
			if (MobHealth.entityLookup.get(mobtype) != null) {
				mobtype=MobHealth.entityLookup.get(mobtype);
			}
		}

		switch (MobHealth.damageDisplayType) {
		case 4: //#    4: display damage taken (+amount resisted)
			damageOutput=Integer.toString(damageTaken);
			if (damageResisted>0) damageOutput+= "(+" +  damageResisted + ")";
			break;
		case 3: //#    3: display damage inflicted (-amount resisted)
			damageOutput=Integer.toString(thisDamange);
			if (damageResisted>0) damageOutput+= "(-" +  damageResisted + ")";
			break;
		case 2: //#    2: display damage taken.
			damageOutput=Integer.toString(damageTaken);
			break;
		default: //#    1: display damage inflicted.  
			damageOutput=Integer.toString(thisDamange);
		}

		Boolean spoutUsed=false;
		Boolean checkForZeroDamageHide=true;

		if (damagerMob instanceof Egg && (!(plugin.getLangConfig().getString("chatMessageEgg")==null))) {
			checkForZeroDamageHide=false;
		} else if (damagerMob instanceof Snowball && (!(plugin.getLangConfig().getString("chatMessageSnowball")==null))) {
			checkForZeroDamageHide=false;
		} else if ((MobHealth.hideNoDammage&&(damageTaken>0)) || !MobHealth.hideNoDammage) {
			checkForZeroDamageHide=false;
		}

		if (MobHealth.debugMode) {
			if (isPlayer) { System.out.print("Is Player"); } else { System.out.print("Is not Player"); }
			if (isAnimal) { System.out.print("Is Animal"); } else { System.out.print("Is not Animal"); }
			if (isMonster) { System.out.print("Is Monster"); } else { System.out.print("Is not Monster"); }
		}

		if (
				((MobHealth.disablePlayers&&!isPlayer) || !MobHealth.disablePlayers) 
				&& 
				((MobHealth.disableMonsters&&!isMonster) || !MobHealth.disableMonsters) 
				&& 
				((MobHealth.disableAnimals&&!isAnimal) || !MobHealth.disableAnimals) 
				&&
				(!checkForZeroDamageHide)
				){
			if (!MobHealth.disableSpout) {
				if(player.getServer().getPluginManager().isPluginEnabled("Spout")) {
					if (MobHealth.debugMode) { System.out.print("SpoutPlugin detected"); }
					if(SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
						if (MobHealth.debugMode) { System.out.print("SpoutCraftEnabled"); }
						String title, message = "";
						Material icon;
						if (damagerMob instanceof Projectile) {
							if (damagerMob instanceof Egg) {
								icon = Material.getMaterial(344);
							} else if (damagerMob instanceof Snowball) {
								icon = Material.getMaterial(332);
							} else {
								icon = Material.getMaterial(261);
							}
						} else {
							icon = Material.getMaterial(276);
						}				
						if (damagerMob instanceof Egg && (!(plugin.getLangConfig().getString("spoutEggTitle")==null))) {
							title =  plugin.getLangConfig().getString("spoutEggTitle");
						} else if (damagerMob instanceof Snowball && (!(plugin.getLangConfig().getString("spoutSnowballTitle")==null))) {
							title =  plugin.getLangConfig().getString("spoutSnowballTitle");
						} else {
							title =  plugin.getLangConfig().getString("spoutDamageTitle");
						}

						title=title.replaceAll("%D",damageOutput);
						title=title.replaceAll("%N",mobtype);
						title=title.replaceAll("%M",Integer.toString(mobsMaxHealth));

						for (int chatcntr = 0;chatcntr<16;chatcntr++){
							title=title.replaceAll("&"+Integer.toHexString(chatcntr),(ChatColor.getByChar(Integer.toHexString(chatcntr)))+"");
						}

						if (damagerMob instanceof Egg && (!(plugin.getLangConfig().getString("spoutEggMessage")==null))) {
							message =  plugin.getLangConfig().getString("spoutEggMessage");
						} else if (damagerMob instanceof Snowball && (!(plugin.getLangConfig().getString("spoutSnowballMessage")==null))) {
							message =  plugin.getLangConfig().getString("spoutSnowballMessage");
						} else {
							if (targetMob.isDead()) {
								message =  plugin.getLangConfig().getString("spoutKilledMessage");
							} else {
								message =  plugin.getLangConfig().getString("spoutDamageMessage");
								if ((mobsHealth<2) || (mobsHealth<=(mobsMaxHealth/4)) ) {
									message=message.replaceAll("%H",(ChatColor.DARK_RED) + Integer.toString(mobsHealth) + (ChatColor.WHITE));
								} else {
									message=message.replaceAll("%H",Integer.toString(mobsHealth));
								}
							}
						}
						for (int chatcntr2 = 0;chatcntr2<16;chatcntr2++){
							message=message.replaceAll("&"+Integer.toHexString(chatcntr2),(ChatColor.getByChar(Integer.toHexString(chatcntr2)))+"");
						}
						message=message.replaceAll("%D",damageOutput);
						message=message.replaceAll("%N",mobtype);
						message=message.replaceAll("%M",Integer.toString(mobsMaxHealth));			        
						try {
							spoutUsed=true;
							SpoutManager.getPlayer(player).sendNotification(title, message, icon);
							if (MobHealth.debugMode) { 
								System.out.print("---");
								System.out.print("Title: "+title); 
								System.out.print("Message: "+message); 
								System.out.print("---");
								System.out.print(" ");
							}
						}
						catch (UnsupportedOperationException e) {
							System.err.println(e.getMessage());
							if (MobHealth.debugMode) { 
								System.out.print("Spout error");
								System.out.print(e.getMessage());
							}
							spoutUsed=false;
						}
					}
				}
			}


			if (!spoutUsed) {
				String ChatMessage;
				if (damagerMob instanceof Egg && (!(plugin.getLangConfig().getString("chatMessageEgg")==null))) {
					ChatMessage =  plugin.getLangConfig().getString("chatMessageEgg");
				} else if (damagerMob instanceof Snowball && (!(plugin.getLangConfig().getString("chatMessageSnowball")==null))) {
					ChatMessage =  plugin.getLangConfig().getString("chatMessageSnowball");
				} else {
					if (targetMob.isDead()) {
						ChatMessage = plugin.getLangConfig().getString("chatKilledMessage");
					} else {
						ChatMessage = plugin.getLangConfig().getString("chatMessage");
						if ((mobsHealth<2) || (mobsHealth<=(mobsMaxHealth/4)) ) {
							ChatMessage=ChatMessage.replaceAll("%H",(ChatColor.DARK_RED) + Integer.toString(mobsHealth) + (ChatColor.WHITE));
						} else {
							ChatMessage=ChatMessage.replaceAll("%H",Integer.toString(mobsHealth));
						}
					}
				}
				ChatMessage=ChatMessage.replaceAll("%D",damageOutput);
				ChatMessage=ChatMessage.replaceAll("%N",mobtype);
				ChatMessage=ChatMessage.replaceAll("%M",Integer.toString(mobsMaxHealth));
				for (int chatcntr3 = 0;chatcntr3<16;chatcntr3++){
					ChatMessage=ChatMessage.replaceAll("&"+Integer.toHexString(chatcntr3),(ChatColor.getByChar(Integer.toHexString(chatcntr3)))+"");
				}
				player.sendMessage(ChatMessage);
			}
		}
	}
}