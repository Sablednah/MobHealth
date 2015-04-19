package main.java.me.sablednah.MobHealth.API;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBar_1_8_1 implements ActionBar {
	public boolean sendActionBar(String text, Player player) {
		String output = "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', text) + "\"}";

		IChatBaseComponent baseChat = ChatSerializer.a(output);
		PacketPlayOutChat actionbarPacket = new PacketPlayOutChat(baseChat, (byte) 2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(actionbarPacket);
		return true;
	}
}
