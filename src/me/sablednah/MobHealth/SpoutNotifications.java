package me.sablednah.MobHealth;

import me.sablednah.MobHealth.NewWidgitActions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.*;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutNotifications {

	public static Boolean showAchievement(Player player, String title, String message, Material icon) {
		Boolean spoutUsed;

		try {
			spoutUsed=true;
			
			SpoutManager.getPlayer(player).sendNotification(title, message, icon);

			if (MobHealth.debugMode) { 
				System.out.print("---+---");
				System.out.print("Title: "+title); 
				System.out.print("Message: "+message); 
				System.out.print("---+---");
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

		return spoutUsed;
	}

	public static Boolean showRPG(Player player, String message, Material icon) {
		Boolean spoutUsed;

		spoutUsed=true;
		try {
			SpoutPlayer splayer = SpoutManager.getPlayer(player);

			Widget w = MobHealth.getWidget(player,0);

			if (w!=null){  // remove widget if already onscreen
				splayer.getMainScreen().removeWidget(w);
			}



			Widget damageWidget = new GenericLabel(message).setAlign(WidgetAnchor.TOP_CENTER)//
					.setTextColor(new Color(0.8F, 0.0F, 0, 1.0F))//
					.setAuto(true).setScale(2F)//
					.setHeight(20).setWidth(20)//
					.shiftXPos(-10).shiftYPos(-30)//
					.setAnchor(WidgetAnchor.CENTER_CENTER)//
					.animate(WidgetAnim.POS_Y, -4F, (short)60, (short)2, false, false).animateStart();

			MobHealth.putWidget(player,damageWidget,0);
			splayer.getMainScreen().attachWidget(MobHealth.plugin, damageWidget);

			if (MobHealth.debugMode) { 
				System.out.print("MobHealth.plugin: "+MobHealth.plugin);
				System.out.print("player: "+player);
				System.out.print("player: "+damageWidget);

			}

			MobHealth.plugin.getServer().getScheduler().scheduleSyncDelayedTask(MobHealth.plugin, new NewWidgitActions(player,  MobHealth.plugin, 0, damageWidget), 80L);

		}
		catch (UnsupportedOperationException e) {
			System.err.println(e.getMessage());
			if (MobHealth.debugMode) { 
				System.out.print("Spout error");
				System.out.print(e.getMessage());
			}
			spoutUsed=false;
		}

		return spoutUsed;
	}



	public static Boolean showSideWidget(Player player, String title, String message, Material icon) {
		Boolean spoutUsed;

		try {
			spoutUsed=true;
			SpoutPlayer splayer2 = SpoutManager.getPlayer(player);

			Widget g = MobHealth.getWidget(player,1);
			if (g!=null){  // remove widget if already onscreen
				splayer2.getMainScreen().removeWidget(g);
			}
			Widget m = MobHealth.getWidget(player,2);
			if (m!=null){  // remove widget if already onscreen
				splayer2.getMainScreen().removeWidget(m);
			}								
			Widget i = MobHealth.getWidget(player,3);
			if (i!=null){  // remove widget if already onscreen
				splayer2.getMainScreen().removeWidget(i);
			}	
			Widget widget = new GenericLabel(title+" \n"+message).setAlign(WidgetAnchor.BOTTOM_RIGHT).setTextColor(new Color(1.0F, 1.0F, 1.0F, 0.5F));
			widget.setHeight(30).setWidth(150).setPriority(RenderPriority.Normal);
			widget.shiftXPos(-150).shiftYPos(35);  //-15
			widget.setAnchor(WidgetAnchor.CENTER_RIGHT);
			widget.animate(WidgetAnim.POS_X, -1F, (short)20, (short)1, false, false).animateStart();

			Widget gradient = new GenericGradient().setTopColor(new Color(0.0F, 0.0F, 0.0F, 1.0F)).setBottomColor(new Color(0.0F, 0.0F, 0.1F, 1.0F));
			gradient.setHeight(30).setWidth(150).setPriority(RenderPriority.High).setMargin(0);
			gradient.shiftXPos(-145).shiftYPos(10);  //40
			gradient.setAnchor(WidgetAnchor.CENTER_RIGHT);

			Widget item = new GenericItemWidget(new ItemStack(icon,1)).setDepth(16);
			item.setHeight(16).setWidth(16).setPriority(RenderPriority.Normal).setMargin(0);
			item.shiftXPos(-140).shiftYPos(16);  //40
			item.setAnchor(WidgetAnchor.CENTER_RIGHT);


			MobHealth.putWidget(player,item,3);
			MobHealth.putWidget(player,widget,2);
			MobHealth.putWidget(player,gradient,1);

			splayer2.getMainScreen().attachWidget(MobHealth.plugin,gradient);
			splayer2.getMainScreen().attachWidget(MobHealth.plugin,widget);
			splayer2.getMainScreen().attachWidget(MobHealth.plugin,item);

			MobHealth.plugin.getServer().getScheduler().scheduleSyncDelayedTask(MobHealth.plugin, new NewWidgitActions(player,  MobHealth.plugin, 1, gradient), 80L);
			MobHealth.plugin.getServer().getScheduler().scheduleSyncDelayedTask(MobHealth.plugin, new NewWidgitActions(player,  MobHealth.plugin, 2, widget), 80L);
			MobHealth.plugin.getServer().getScheduler().scheduleSyncDelayedTask(MobHealth.plugin, new NewWidgitActions(player,  MobHealth.plugin, 3, item), 80L);


		}
		catch (UnsupportedOperationException e) {
			System.err.println(e.getMessage());
			if (MobHealth.debugMode) { 
				System.out.print("Spout error");
				System.out.print(e.getMessage());
			}
			spoutUsed=false;
		}
		return spoutUsed;
	}

}
