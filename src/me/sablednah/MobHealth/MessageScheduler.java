package me.sablednah.MobHealth;

import java.util.Arrays;

import org.bukkit.Material;

import org.bukkit.ChatColor;
import org.bukkit.entity.Egg;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import org.getspout.spoutapi.SpoutManager;

public class MessageScheduler implements Runnable {
	private Player player;
	private EntityDamageByEntityEvent damageEvent;
	private LivingEntity targetMob;
	public MobHealth plugin;
	private int HealthBefore;

	public MessageScheduler(Player shooter, EntityDamageByEntityEvent damageEvent, LivingEntity targetMob, int HealthBefore, MobHealth plugin) {
		//this.player = player;
		this.plugin = plugin;
		this.damageEvent = damageEvent;
		this.player = shooter;
		this.setTargetMob(targetMob);
		this.HealthBefore = HealthBefore;
	}
	public void run() {

		int thisDamange=0, mobsHealth=0, mobsMaxHealth=0, damageTaken=0, damageResisted=0;
		Boolean isPlayer = false, isMonster = false, isAnimal = false;
		String damageOutput;

		//int mobID;
		//mobID=targetMob.getEntityId();

		thisDamange = damageEvent.getDamage();
		mobsMaxHealth = targetMob.getMaxHealth();
		mobsHealth = targetMob.getHealth();
		damageTaken = HealthBefore - mobsHealth;
		damageResisted = thisDamange - damageTaken;
		
//		System.out.print("[MobHealth] " + thisDamange +" thisDamange.");
//		System.out.print("[MobHealth] " + mobsHealth +" mobsHealth.");
//		System.out.print("[MobHealth] " + HealthBefore +" HealthBefore.");
//		System.out.print("[MobHealth] " + damageTaken +" damageTaken.");
//		System.out.print("[MobHealth] " + damageResisted +" damageResisted.");

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

		//#    1: display damage inflicted.  
		//#    2: display damage taken.
		//#    3: display damage inflicted (-amount resisted)
		//#    4: display damage taken (+amount resisted)
		switch (MobHealth.damageDisplayType) {
		case 4:
			damageOutput=Integer.toString(damageTaken);
			if (damageResisted>0) damageOutput+= "(+" +  damageResisted + ")";
			break;
		case 3:
			damageOutput=Integer.toString(thisDamange);
			if (damageResisted>0) damageOutput+= "(-" +  damageResisted + ")";
			break;
		case 2:		
			damageOutput=Integer.toString(damageTaken);
			break;
		default:
			damageOutput=Integer.toString(thisDamange);
		}
		
		Boolean spoutUsed=false;
		Boolean checkForZeroDamageHide=true;
		
		if (damageEvent.getDamager() instanceof Egg && (!(plugin.getLangConfig().getString("chatMessageEgg")==null))) {
			checkForZeroDamageHide=false;
		} else if (damageEvent.getDamager() instanceof Snowball && (!(plugin.getLangConfig().getString("chatMessageSnowball")==null))) {
			checkForZeroDamageHide=false;
		} else if ((MobHealth.hideNoDammage&&(damageTaken>0)) || !MobHealth.hideNoDammage) {
			checkForZeroDamageHide=false;
		}
		
//		if (isPlayer) { System.out.print("Is Player"); } else { System.out.print("Is not Player"); }
//		if (isAnimal) { System.out.print("Is Animal"); } else { System.out.print("Is not Animal"); }
//		if (isMonster) { System.out.print("Is Monster"); } else { System.out.print("Is not Monster"); }
		
		if (
				((MobHealth.disablePlayers&&!isPlayer) || !MobHealth.disablePlayers) 
				&& 
				((MobHealth.disableMonsters&&!isMonster) || !MobHealth.disableMonsters) 
				&& 
				((MobHealth.disableAnimals&&!isAnimal) || !MobHealth.disableAnimals) 
				&&
				(!checkForZeroDamageHide)
			)
		{




			if (!MobHealth.disableSpout) {
				if(player.getServer().getPluginManager().isPluginEnabled("Spout")) {
					if(SpoutManager.getPlayer(player).isSpoutCraftEnabled()) {
						String title, message = "";
						Material icon;
						if (damageEvent.getDamager() instanceof Projectile) {
							if (damageEvent.getDamager() instanceof Egg) {
								icon = Material.getMaterial(344);
							} else if (damageEvent.getDamager() instanceof Snowball) {
								icon = Material.getMaterial(332);
							} else {
								icon = Material.getMaterial(261);
							}
						} else {
							icon = Material.getMaterial(276);
						}				
						if (damageEvent.getDamager() instanceof Egg && (!(plugin.getLangConfig().getString("spoutEggTitle")==null))) {
							title =  plugin.getLangConfig().getString("spoutEggTitle");
						} else if (damageEvent.getDamager() instanceof Snowball && (!(plugin.getLangConfig().getString("spoutSnowballTitle")==null))) {
							title =  plugin.getLangConfig().getString("spoutSnowballTitle");
						} else {
							title =  plugin.getLangConfig().getString("spoutDamageTitle");
						}

						title=title.replaceAll("%D",damageOutput);
						title=title.replaceAll("%N",mobtype);
						title=title.replaceAll("%M",Integer.toString(mobsMaxHealth));

						for (int chatcntr = 0;chatcntr<16;chatcntr++){
							title=title.replaceAll("&"+Integer.toHexString(chatcntr),(ChatColor.getByCode(chatcntr))+"");
						}

						if (damageEvent.getDamager() instanceof Egg && (!(plugin.getLangConfig().getString("spoutEggMessage")==null))) {
							message =  plugin.getLangConfig().getString("spoutEggMessage");
						} else if (damageEvent.getDamager() instanceof Snowball && (!(plugin.getLangConfig().getString("spoutSnowballMessage")==null))) {
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
							message=message.replaceAll("&"+Integer.toHexString(chatcntr2),(ChatColor.getByCode(chatcntr2))+"");
						}
						message=message.replaceAll("%D",damageOutput);
						message=message.replaceAll("%N",mobtype);
						message=message.replaceAll("%M",Integer.toString(mobsMaxHealth));			        
						try {
							spoutUsed=true;
							SpoutManager.getPlayer(player).sendNotification(title, message, icon);
						}
						catch (UnsupportedOperationException e) {
							System.err.println(e.getMessage());
							spoutUsed=false;
						}
					}
				}
			}


			if (!spoutUsed) {
				String ChatMessage;
				if (damageEvent.getDamager() instanceof Egg && (!(plugin.getLangConfig().getString("chatMessageEgg")==null))) {
					ChatMessage =  plugin.getLangConfig().getString("chatMessageEgg");
				} else if (damageEvent.getDamager() instanceof Snowball && (!(plugin.getLangConfig().getString("chatMessageSnowball")==null))) {
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
					ChatMessage=ChatMessage.replaceAll("&"+Integer.toHexString(chatcntr3),(ChatColor.getByCode(chatcntr3))+"");
				}
				player.sendMessage(ChatMessage);
			}
		}
	}

	// Optional, if you need it
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public LivingEntity getTargetMob() {
		return targetMob;
	}
	public void setTargetMob(LivingEntity targetMob) {
		this.targetMob = targetMob;
	}
	public EntityDamageByEntityEvent getdamageEvent() {
		return damageEvent;
	}
	public void setdamageEvent(EntityDamageByEntityEvent damageEvent) {
		this.damageEvent = damageEvent;
	}
}