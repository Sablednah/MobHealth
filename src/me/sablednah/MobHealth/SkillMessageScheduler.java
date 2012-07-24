package me.sablednah.MobHealth;

import java.util.Arrays;

import me.sablednah.zombiemod.PutredineImmortui;
import me.sablednah.zombiemod.ZombieMod;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import org.getspout.spoutapi.SpoutManager;

import cam.Likeaboss;
import cam.boss.Boss;
import cam.boss.BossManager;


import com.garbagemule.MobArena.MobArenaHandler;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.MABoss;
import com.garbagemule.MobArena.waves.Wave;
import com.garbagemule.MobArena.waves.WaveManager;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;

public class SkillMessageScheduler implements Runnable {

    private Player player;
    private SkillDamageEvent skillDamageEvent;
    private LivingEntity targetMob;
    public MobHealth plugin;
    private int HealthBefore;
    private int DamageBefore;

    public SkillMessageScheduler(Player shooter, SkillDamageEvent skillDamageEvent, LivingEntity targetMob, int HealthBefore, int DamageBefore, MobHealth plugin) {
        this.plugin = plugin;
        this.skillDamageEvent = skillDamageEvent;
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

        //Check if player is in a MobArena.
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

                            WaveManager wm = arena.getWaveManager();
                            Wave thisWave = wm.getCurrent();

                            if (thisWave != null) {
                                mobsMaxHealth=(int) (targetMob.getMaxHealth()*thisWave.getHealthMultiplier());
                            } else {
                                mobsMaxHealth=targetMob.getMaxHealth();
                            }

                            thisDamange = skillDamageEvent.getDamage();
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

        String ZMName = null;

        //is entity tracked by ZombieMod.
        if (MobHealth.hasZM) {

            ZombieMod ZM=(ZombieMod) plugin.getServer().getPluginManager().getPlugin("ZombieMod");
            PutredineImmortui zomb = ZM.getZombie((Entity) targetMob);

            if (zomb != null) {
                isSpecial=true;
                thisDamange = DamageBefore;
                mobsMaxHealth = zomb.maxHealth;
                mobsHealth = zomb.health;
                damageTaken = HealthBefore - mobsHealth;
                //if (damageTaken>9950) { damageTaken=thisDamange; } //heroes hacky fix
                damageTaken=thisDamange;
                damageResisted = thisDamange - damageTaken;
                ZMName = zomb.commonName;
            }
            zomb = null;
            ZM = null;
        }

        //I need a Hero!
        if (!isSpecial) {
            Heroes heroes = (Heroes) plugin.getServer().getPluginManager().getPlugin("Heroes");
            thisDamange = skillDamageEvent.getDamage();
            mobsMaxHealth = heroes.getDamageManager().getMaxHealth(targetMob);
            if (targetMob.isDead()) {
                mobsHealth = HealthBefore-thisDamange;
            } else {
                mobsHealth = heroes.getDamageManager().getHealth(targetMob);
            }
            damageTaken = HealthBefore - mobsHealth;
            damageResisted = thisDamange - damageTaken;
            heroes = null;
        }

        String skillName=skillDamageEvent.getSkill().getName();

        if (MobHealth.debugMode) {
            System.out.print("--");
            System.out.print("[MobHealth] " + skillDamageEvent.getDamage() +" skillDamageEvent.getDamage();.");
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
            if (ZMName != null) { mobtype = ZMName; }
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

        if ((MobHealth.hideNoDammage&&(damageTaken>0)) || !MobHealth.hideNoDammage) {
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
                        icon = Material.getMaterial(377);
                        title =  plugin.getLangConfig().getString("heroesSkillSpoutDamageTitle");

                        title=title.replaceAll("%D",damageOutput);
                        title=title.replaceAll("%N",mobtype);
                        title=title.replaceAll("%S",skillName);
                        title=title.replaceAll("%M",Integer.toString(mobsMaxHealth));

                        for (int chatcntr = 0;chatcntr<16;chatcntr++){
                            title=title.replaceAll("&"+Integer.toHexString(chatcntr),(ChatColor.getByChar(Integer.toHexString(chatcntr)))+"");
                        }

                        if (targetMob.isDead()) {
                            message =  plugin.getLangConfig().getString("heroesSkillSpoutKilledMessage");
                        } else {
                            message =  plugin.getLangConfig().getString("heroesSkillSpoutDamageMessage");
                            if ((mobsHealth<2) || (mobsHealth<=(mobsMaxHealth/4)) ) {
                                message=message.replaceAll("%H",(ChatColor.DARK_RED) + Integer.toString(mobsHealth) + (ChatColor.WHITE));
                            } else {
                                message=message.replaceAll("%H",Integer.toString(mobsHealth));
                            }
                        }

                        for (int chatcntr2 = 0;chatcntr2<16;chatcntr2++){
                            message=message.replaceAll("&"+Integer.toHexString(chatcntr2),(ChatColor.getByChar(Integer.toHexString(chatcntr2)))+"");
                        }
                        message=message.replaceAll("%D",damageOutput);
                        message=message.replaceAll("%N",mobtype);
                        message=message.replaceAll("%S",skillName);
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

                if (targetMob.isDead()) {
                    ChatMessage = plugin.getLangConfig().getString("heroesSkillChatKilledMessage");
                } else {
                    ChatMessage = plugin.getLangConfig().getString("heroesSkillChatMessage");
                    if ((mobsHealth<2) || (mobsHealth<=(mobsMaxHealth/4)) ) {
                        ChatMessage=ChatMessage.replaceAll("%H",(ChatColor.DARK_RED) + Integer.toString(mobsHealth) + (ChatColor.WHITE));
                    } else {
                        ChatMessage=ChatMessage.replaceAll("%H",Integer.toString(mobsHealth));
                    }
                }

                ChatMessage=ChatMessage.replaceAll("%D",damageOutput);
                ChatMessage=ChatMessage.replaceAll("%N",mobtype);
                ChatMessage=ChatMessage.replaceAll("%S",skillName);
                ChatMessage=ChatMessage.replaceAll("%M",Integer.toString(mobsMaxHealth));
                for (int chatcntr3 = 0;chatcntr3<16;chatcntr3++){
                    ChatMessage=ChatMessage.replaceAll("&"+Integer.toHexString(chatcntr3),(ChatColor.getByChar(Integer.toHexString(chatcntr3)))+"");
                }
                if (!sendPluginMessage(player, ChatMessage)) {
                    player.sendMessage(ChatMessage);
                }
            }
        }
    }

    private boolean sendPluginMessage(Player player, String message) {
        if (player == null || message == null) {
            return false;
        }
        if (!player.getListeningPluginChannels().contains("SimpleNotice")) {
            return false;
        }

        try {
            player.sendPluginMessage(plugin, "SimpleNotice", message.getBytes("UTF-8"));
            return true;
        } catch (Exception e) {
            //plugin.getLogger().log(java.util.logging.Level.WARNING, "Sending PluginChannel{SimpleNotice} message to \"" + player.getName() + "\" failed", e.getCause());
            return false;
        }
    }
}