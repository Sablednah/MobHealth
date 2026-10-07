package com.sablednah.mobhealth.bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 * {@code /mobhealth reload} and {@code /mobhealth toggle [on|off] [player]}. Permission nodes are
 * declared in plugin.yml; the command itself is open so the usage line reaches everyone.
 */
public final class Commands implements TabExecutor {

    private final MobHealthPlugin plugin;

    public Commands(MobHealthPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("reload")) {
            if (!sender.hasPermission("mobhealth.reload")) {
                sender.sendMessage(ChatColor.RED + "You may not reload MobHealth.");
                return true;
            }
            plugin.reloadSettings();
            sender.sendMessage(ChatColor.GREEN + "MobHealth configuration reloaded.");
            return true;
        }
        if (sub.equals("toggle")) {
            return toggle(sender, args);
        }
        return false;
    }

    private boolean toggle(CommandSender sender, String[] args) {
        Boolean force = null;
        Player target = null;
        for (int i = 1; i < args.length; i++) {
            String a = args[i].toLowerCase(Locale.ROOT);
            if (a.equals("on")) {
                force = Boolean.FALSE; // "on" = displays on = not muted
            } else if (a.equals("off")) {
                force = Boolean.TRUE;
            } else {
                target = plugin.getServer().getPlayer(args[i]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "No player called " + args[i] + " is online.");
                    return true;
                }
            }
        }

        if (target != null && target != sender) {
            if (!sender.hasPermission("mobhealth.toggle.others")) {
                sender.sendMessage(ChatColor.RED + "You may only toggle your own displays.");
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Name a player: /mobhealth toggle <on|off> <player>");
                return true;
            }
            if (!sender.hasPermission("mobhealth.toggle")) {
                sender.sendMessage(ChatColor.RED + "You may not toggle MobHealth.");
                return true;
            }
            target = (Player) sender;
        }

        PlayerState state = plugin.players();
        boolean muted = force != null ? force : !state.isMuted(target);
        state.setMuted(target, muted);

        // No list of modes here: it goes stale as modes are added. The nameplate is the one
        // thing worth naming, because it is the one thing this command cannot hide — a name tag
        // is a single shared property of the mob, so there is no per-viewer copy to switch off.
        String message = muted
                ? ChatColor.YELLOW + "MobHealth displays hidden for " + (target == sender ? "you" : target.getName())
                        + ". " + ChatColor.GRAY + "Nameplates are shared and stay visible."
                : ChatColor.GREEN + "MobHealth displays shown for " + (target == sender ? "you" : target.getName()) + ".";
        sender.sendMessage(message);
        if (target != sender) {
            target.sendMessage(muted
                    ? ChatColor.YELLOW + "MobHealth displays hidden for you."
                    : ChatColor.GREEN + "MobHealth displays shown for you.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return prefixed(args[0], Arrays.asList("reload", "toggle"));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("toggle")) {
            return prefixed(args[1], Arrays.asList("on", "off"));
        }
        return null; // player names
    }

    private static List<String> prefixed(String prefix, List<String> options) {
        List<String> out = new ArrayList<String>();
        for (String o : options) {
            if (o.startsWith(prefix.toLowerCase(Locale.ROOT))) {
                out.add(o);
            }
        }
        return out;
    }
}
