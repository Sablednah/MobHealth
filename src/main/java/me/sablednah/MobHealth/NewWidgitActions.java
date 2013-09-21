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
