package com.sablednah.mobhealth.compat9;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/** 1.11: Spigot grew a real action bar call, so the reflected packet is retired. */
public class Compat111 extends Compat19 {

    public Compat111(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String describe() {
        return "Spigot 1.11 (chat, action bar, nameplates, boss bars, armour-stand damage numbers)";
    }

    @Override
    public boolean supportsActionBar() {
        return true;
    }

    @Override
    public void sendActionBar(Player player, String legacyText) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(legacyText));
    }
}
