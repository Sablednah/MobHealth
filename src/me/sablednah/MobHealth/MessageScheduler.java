package me.sablednah.MobHealth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageScheduler implements Runnable {
	private Player player;
	private int damageAmount;
	public MessageScheduler(Player player, int dammageAmount) {
		this.player = player;
		this.damageAmount=dammageAmount;
	}
	public void run() {
		// Do whatever you need with the player. For example, we send them a message
		player.sendMessage(ChatColor.RED + "Take "+damageAmount+" damage.");
	}
	// Optional, if you need it
	public Player getPlayer() {
		return player;
	}
	// Also Optional
	public void setPlayer(Player player) {
		this.player = player;
	}
}