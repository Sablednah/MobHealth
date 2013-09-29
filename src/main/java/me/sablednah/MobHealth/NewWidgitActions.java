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

import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

public class NewWidgitActions implements Runnable {
    
    public MobHealth plugin;
    private Player player;
    private Widget widget;
    private int action;
    
    public NewWidgitActions(Player p, MobHealth plugin, int action, Widget widget) {
        this.player = p;
        this.plugin = plugin;
        this.action = action;
        this.widget = widget;
    }
    
    public void run() {
        SpoutPlayer splayer = SpoutManager.getPlayer(player);
        MobHealth.killWidget(player, action);
        splayer.getMainScreen().removeWidget(widget);
        
    }
}
