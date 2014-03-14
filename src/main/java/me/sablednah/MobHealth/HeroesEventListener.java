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

import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import com.herocraftonline.heroes.api.events.*;

public class HeroesEventListener implements Listener {
    
    public MobHealth plugin;
    
    public HeroesEventListener(MobHealth instance) {
        this.plugin = instance;
    }
       
    @EventHandler(priority = EventPriority.LOWEST)
    public void SkillDamageEvent(SkillDamageEvent event) {
        
        if (!event.isCancelled()) {
            
            Player playa = null;
            
            if (event instanceof SkillDamageEvent) {
                SkillDamageEvent damageEvent = event;
                
                String skillName = damageEvent.getSkill().getName();
                
                if (MobHealth.debugMode) {
                    System.out.print("[MobHealth] detected skillName: " + skillName);
                }
                
                if (damageEvent.getDamager().getEntity() instanceof Player) {
                    playa = (Player) damageEvent.getDamager().getEntity();
                }
                
                if (playa != null) {
                    if (MobHealth.getPluginState(playa)) {
                        if ((playa.hasPermission("mobhealth.show") && MobHealth.usePermissions) || (!MobHealth.usePermissions)) {
                            LivingEntity targetMob = null;
                            if (event.getEntity() instanceof ComplexEntityPart) {
                                targetMob = ((ComplexEntityPart) event.getEntity()).getParent();
                            } else if (event.getEntity() instanceof LivingEntity) {
                                targetMob = (LivingEntity) event.getEntity();
                            }
                            if (targetMob != null) {
                                targetMob.setMetadata("HeroesSkillDamaged", new FixedMetadataValue(plugin, skillName));
                            }
                        } else {
                            if (MobHealth.debugMode) {
                                System.out.print("Not allowed - mobhealth.show is " + playa.hasPermission("mobhealth.show") + " - usePermissions set to " + MobHealth.usePermissions);
                            }
                        }
                    }
                }
            }
        }
    }
}