package me.sablednah.MobHealth.API;

import java.util.List;
import java.util.Map;

import me.ThaH3lper.com.Api.Api;
import me.ThaH3lper.com.EpicBoss;
import me.sablednah.MobHealth.MobHealth;
import me.sablednah.MobHealth.SpoutNotifications;
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
    
    private MobHealth plugin;
    
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
        
        targetHealth = (int) targetMob.getHealth();
        
        if (MobHealth.hasMA) {
            MonsterApocalypse ma = (MonsterApocalypse) plugin.getServer().getPluginManager().getPlugin("Monster Apocalypse");
            healthmanager MAHealthManager = ma.getHealthManager();
            if (MAHealthManager != null) {
                targetHealth = MAHealthManager.getmobhp(targetMob);
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
        
        if (MobHealth.hasMobs) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mobs_data = targetMob.hasMetadata("mobs_data") ? (Map<String, Object>) targetMob.getMetadata("mobs_data").get(0).value() : null;
            
            // HP" and "MAX_HP", both of which are ints
            if (mobs_data != null) {
                if (mobs_data.containsKey("HP")) {
                    targetHealth = ((Integer) mobs_data.get("HP")).intValue();
                }
            }
            mobs_data = null;
        }
        
        if (MobHealth.hasEpicBoss) {
            EpicBoss EB = (EpicBoss) plugin.getServer().getPluginManager().getPlugin("EpicBossRecoded");
            Api EBAPI = new Api(EB);
            if (EBAPI.isBoss(targetMob)) {
                targetHealth = EBAPI.getHealth(targetMob);
            }
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
                    targetMaxHealth = (int) thisBoss.getMaxHealth();
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
        if (MobHealth.hasMobs) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mobs_data = targetMob.hasMetadata("mobs_data") ? (Map<String, Object>) targetMob.getMetadata("mobs_data").get(0).value() : null;
            
            // HP" and "MAX_HP", both of which are ints
            if (mobs_data != null) {
                if (mobs_data.containsKey("HP")) {
                    targetMaxHealth = ((Integer) mobs_data.get("MAX_HP")).intValue();
                }
            }
            mobs_data = null;
        }
        
        if (MobHealth.hasEpicBoss) {
            EpicBoss EB = (EpicBoss) plugin.getServer().getPluginManager().getPlugin("EpicBossRecoded");
            Api EBAPI = new Api(EB);
            if (EBAPI.isBoss(targetMob)) {
                targetMaxHealth = EBAPI.getMaxHealth(targetMob);
            }
        }
        return targetMaxHealth;
    }
    
    public String getMobName(LivingEntity targetMob) {
        String mobtype;
        
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
            Variant typ = ((Horse)targetMob).getVariant();
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
        
        if (MobHealth.hasEpicBoss) {
            EpicBoss EB = (EpicBoss) plugin.getServer().getPluginManager().getPlugin("EpicBossRecoded");
            Api EBAPI = new Api(EB);
            if (EBAPI.isBoss(targetMob)) {
                mobtype = EBAPI.getBossName(targetMob);
            }
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
        
        return mobtype;
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
                            showBar=false;
                        }
                    } else if (targetMob instanceof Animals) {
                        if (MobHealth.hideBarForAnimal) {
                            showBar=false;
                        }
                    }
                    if (targetMob.hasMetadata("NPC")){
                        if (MobHealth.hideBarForNPC){
                            showBar = false;
                        }
                    }
                    String thisType = targetMob.getType().getName();
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
