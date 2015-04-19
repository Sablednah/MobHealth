package main.java.me.sablednah.MobHealth.API;

import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBar_1_8_3 implements ActionBar {
	public boolean sendActionBar(String text, Player player) {
		String output = "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', text) + "\"}";

		IChatBaseComponent baseChat = IChatBaseComponent.ChatSerializer.a(output);
		PacketPlayOutChat actionbarPacket = new PacketPlayOutChat(baseChat, (byte) 2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(actionbarPacket);
		return true;
	}
}
