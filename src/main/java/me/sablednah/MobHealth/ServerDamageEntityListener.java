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

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ServerDamageEntityListener implements Listener {
    
    public MobHealth plugin;
    
    public ServerDamageEntityListener(MobHealth instance) {
        this.plugin = instance;
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (MobHealth.showPlayerHeadHealth || MobHealth.showMobHeadHealth) {
            if ((event.getRightClicked() instanceof LivingEntity)) {
                LivingEntity le = (LivingEntity) event.getRightClicked();
                String customName = le.getCustomName();
                if (customName != null) {
                    if (customName.length() > 32) {
                        if (customName.contains(MobHealth.healthPrefix)) {  // trim at [ if found (removes health bar)
                            le.setCustomName(MobHealth.cleanName(customName));
                            // now set a timer to put it back.
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ScheduledshowBar(le), 5L);
                        } else {
                        	le.setCustomName(le.getCustomName().substring(0,32));
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageMonitor(EntityDamageEvent event) {
        if (MobHealth.showPlayerHeadHealth || MobHealth.showMobHeadHealth) {
            if (event.getEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getEntity();
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ScheduledshowBar(target), 2L);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityRegenMonitor(EntityRegainHealthEvent event) {
        if (MobHealth.showPlayerHeadHealth || MobHealth.showMobHeadHealth) {
            if (event.getEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getEntity();
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ScheduledshowBar(target), 2L);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        
        if (MobHealth.hasHeroes) { // skip weird custom heroes event
            if (event.getDamage()==0.0D && event.getCause()==DamageCause.CUSTOM) {
                if (MobHealth.debugMode) {
                    System.out.print("-cancelling EntityDamageEvent event as is ~probably~ a Skill event-");
                }
                return;
            }
        }
        
        if (!event.isCancelled()) {
            
            int targetHealth = 0;
            
            if (MobHealth.debugMode) {
                if (event.getEntity() instanceof Player) {
                    String tmpplay = ((Player) event.getEntity()).getDisplayName();
                    tmpplay = tmpplay.toLowerCase().toString();
                    if (tmpplay.contains("sablednah")) { // || tmpplay.contains("lordsable")
                        event.setDamage(0.0D);
                        return;
                    }
                }
            }
            MobHealthAPI API = new MobHealthAPI(plugin);
            if (MobHealth.showPlayerHeadHealth || MobHealth.showMobHeadHealth) {
                if (event.getEntity() instanceof LivingEntity) {
                    LivingEntity tm = (LivingEntity) event.getEntity();
                    if (tm instanceof Horse) {
                        String horsename = MobHealth.cleanName(tm.getCustomName());
                        if (horsename != null) {
                            if (horsename.length() > 32) {
                                horsename = horsename.substring(0, 32);
                            }
                            tm.setCustomName(horsename);
                        }
                    } else {
                        API.showBar(tm);
                    }
                }
            }
            
            Player playa = null;
            
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
                
                if (damageEvent.getDamager() instanceof Projectile) {
                    Projectile bullit = (Projectile) damageEvent.getDamager();
                    if (bullit.getShooter() instanceof Player) {
                        playa = (Player) bullit.getShooter();
                    }
                }
                
                if (damageEvent.getDamager() instanceof Tameable && !MobHealth.disablePets) {
                    AnimalTamer temp = ((Tameable) damageEvent.getDamager()).getOwner();
                    if (temp instanceof Player) {
                        playa = (Player) temp;
                    }
                }
                
                if (damageEvent.getDamager() instanceof Player) {
                    playa = (Player) damageEvent.getDamager();
                }
                
                if (playa != null) {
                    if (MobHealth.debugMode) {
                        // event.setDamage(200);
                        System.out.print("----");
                        System.out.print("-damage event-");
                        System.out.print("Entity Damaged " + event.getEntity());
                        System.out.print("Entity class " + event.getEntity().getClass().getName());
                        System.out.print("Event getEventName  " + event.getEventName());
                        System.out.print("Damage class  " + event.getClass());
                        System.out.print("Entity Damage  " + event.getDamage());
                        System.out.print("Damage Cause  " + event.getCause());
                        if (event.getEntity() instanceof ComplexLivingEntity)
                            System.out.print("Entity Damaged is ComplexLivingEntity ");
                        
                    }
                    
                    LivingEntity targetMob = null; // (LivingEntity) event.getEntity();
                    if (event.getEntity() instanceof ComplexEntityPart) {
                        targetMob = ((ComplexEntityPart) event.getEntity()).getParent();
                    } else if (event.getEntity() instanceof LivingEntity) {
                        targetMob = (LivingEntity) event.getEntity();
                    }
                    
                    if (targetMob != null) {
                        
                        if (MobHealth.getPluginState(playa)) {
                            if ((playa.hasPermission("mobhealth.show") && MobHealth.usePermissions) || (!MobHealth.usePermissions)) {
                                targetHealth = API.getMobHealth(targetMob);
                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new MessageScheduler(playa, damageEvent, targetMob, targetHealth, (int) event.getDamage(), plugin), 2L);
                                return;
                            } else {
                                if (MobHealth.debugMode) {
                                    System.out.print("Not allowed - mobhealth.show is " + playa.hasPermission("mobhealth.show") + " - usePermissions set to "
                                            + MobHealth.usePermissions);
                                }
                            }
                        }
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ScheduledshowBar(targetMob), 2L);
                    }
                    if (MobHealth.debugMode) {
                        System.out.print("/----");
                    }
                }
            } else {
            	LivingEntity targetMob = null; // (LivingEntity) event.getEntity();
                if (event.getEntity() instanceof ComplexEntityPart) {
                    targetMob = ((ComplexEntityPart) event.getEntity()).getParent();
                } else if (event.getEntity() instanceof LivingEntity) {
                    targetMob = (LivingEntity) event.getEntity();
                }
                
                if (targetMob != null) {
                	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ScheduledshowBar(targetMob), 2L);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (MobHealth.showPlayerHeadHealth) {
            MobHealth.setHealths.setPlayer(event.getPlayer());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (MobHealth.showPlayerHeadHealth) {
            MobHealth.setHealths.removePlayer(event.getPlayer());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (MobHealth.cleanDeathMessages) {
            String dm = e.getDeathMessage();
            if (e != null) {
                dm = MobHealth.cleanName(dm);
                e.setDeathMessage(dm);
            }
        }
    }
    
    public class ScheduledshowBar implements Runnable {
        
        public LivingEntity tm;
        
        public ScheduledshowBar(LivingEntity t) {
            this.tm = t;
        }
        
        public void run() {
            MobHealthAPI API = new MobHealthAPI(plugin);
            if (tm instanceof Horse) {
                if (tm.getCustomName() != null) {
                    String horsename = MobHealth.cleanName(tm.getCustomName());
                    if (horsename.length() > 32) {
                        horsename = horsename.substring(0, 32);
                    }
                    tm.setCustomName(horsename);
                }
            } else {
                API.showBar(this.tm);
            }
        }
    }
}
