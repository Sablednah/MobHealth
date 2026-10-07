package com.sablednah.mobhealth.beta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.sablednah.mobhealth.core.BarContent;
import com.sablednah.mobhealth.core.BuildInfo;
import com.sablednah.mobhealth.core.HealthBarFormatter;
import com.sablednah.mobhealth.core.HealthBarFormatter.ValueStyle;
import com.sablednah.mobhealth.core.MobCategory;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 * MobHealth for Beta 1.7.3 (CraftBukkit #1060 and its forks: Poseidon, UberBukkit, Canyon).
 *
 * <p>The API of August 2011 is a different world: health is an {@code int} with no maximum to
 * ask for, mobs have no name tags, there is no action bar or boss bar, and events arrive through
 * {@code EntityListener} subclasses rather than annotations. So this is its own small plugin
 * rather than the shared body — chat only, with the mob's maximum health taken from a table of
 * what each creature had in Beta 1.7.3. It shares {@code core} with everything else, so the bar
 * looks the same as it does on 26.3.</p>
 */
public final class MobHealthBeta extends JavaPlugin implements CommandExecutor {

    // config
    private boolean chat;
    private boolean nearby;
    private int nearbyRadius;
    private boolean hostile, neutral, passive, players, hideUntilDamaged;
    private BarContent chatContent;
    private int barSegments;
    private char barFilledChar, barEmptyChar;
    private ValueStyle valueStyle;

    private Configuration playerState;

    @Override
    public void onEnable() {
        getServer().getLogger().info("[MobHealth] MobHealth " + BuildInfo.describe());
        getServer().getLogger().info("[MobHealth] Beta 1.7.3 server: chat display only");
        loadSettings();
        playerState = new Configuration(new File(getDataFolder(), "players.yml"));
        playerState.load();

        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, new Damage(), Event.Priority.Monitor, this);
        getCommand("mobhealth").setExecutor(this);
    }

    @Override
    public void onDisable() {
    }

    // ================================================================= config

    private void loadSettings() {
        Configuration c = getConfiguration();
        c.load();
        boolean fresh = c.getKeys().isEmpty();

        chat = c.getBoolean("display.chat", true);
        nearby = "NEARBY".equalsIgnoreCase(c.getString("audience.audience", "ATTACKER"));
        nearbyRadius = Math.max(4, Math.min(128, c.getInt("audience.nearbyRadius", 32)));
        hostile = c.getBoolean("targets.hostile", true);
        neutral = c.getBoolean("targets.neutral", true);
        passive = c.getBoolean("targets.passive", true);
        players = c.getBoolean("targets.players", false);
        hideUntilDamaged = c.getBoolean("targets.hideUntilDamaged", true);
        chatContent = enumOr(c.getString("chat.chatContent", "BOTH"), BarContent.BOTH);
        barSegments = Math.max(1, Math.min(100, c.getInt("bar.segments", 20)));
        barFilledChar = firstChar(c.getString("bar.filledChar", "|"), '|');
        barEmptyChar = firstChar(c.getString("bar.emptyChar", "|"), '|');
        valueStyle = enumOr(c.getString("bar.valueStyle", "CURRENT_MAX"), ValueStyle.CURRENT_MAX);

        if (fresh) {
            // Write the defaults out so there is a file to edit, with the same keys as every other
            // MobHealth (minus the modes a Beta server cannot show).
            c.setHeader("# MobHealth for Beta 1.7.3: chat display only; keys as on newer servers.");
            c.setProperty("display.chat", chat);
            c.setProperty("audience.audience", nearby ? "NEARBY" : "ATTACKER");
            c.setProperty("audience.nearbyRadius", nearbyRadius);
            c.setProperty("targets.hostile", hostile);
            c.setProperty("targets.neutral", neutral);
            c.setProperty("targets.passive", passive);
            c.setProperty("targets.players", players);
            c.setProperty("targets.hideUntilDamaged", hideUntilDamaged);
            c.setProperty("chat.chatContent", chatContent.name());
            c.setProperty("bar.segments", barSegments);
            c.setProperty("bar.filledChar", String.valueOf(barFilledChar));
            c.setProperty("bar.emptyChar", String.valueOf(barEmptyChar));
            c.setProperty("bar.valueStyle", valueStyle.name());
            c.save();
        }
    }

    // ================================================================= damage

    private final class Damage extends EntityListener {
        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !chat || !(event instanceof EntityDamageByEntityEvent)) {
                return;
            }
            final Player attacker = attackingPlayer(((EntityDamageByEntityEvent) event).getDamager());
            if (attacker == null || !(event.getEntity() instanceof LivingEntity)) {
                return;
            }
            final LivingEntity victim = (LivingEntity) event.getEntity();
            if (victim == attacker) {
                return;
            }
            final int before = victim.getHealth();
            // Measured, not predicted: the event fires before armour has had its say.
            getServer().getScheduler().scheduleSyncDelayedTask(MobHealthBeta.this, new Runnable() {
                public void run() {
                    int after = victim.isDead() ? 0 : Math.max(0, victim.getHealth());
                    int damage = before - after;
                    if (damage > 0) {
                        report(victim, attacker, damage, after);
                    }
                }
            }, 1L);
        }
    }

    private static Player attackingPlayer(Entity damager) {
        if (damager instanceof Player) {
            return (Player) damager;
        }
        if (damager instanceof Projectile) {
            LivingEntity shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }
        return null;
    }

    private void report(LivingEntity victim, Player attacker, int damage, int current) {
        int max = maxHealth(victim);
        if (hideUntilDamaged && current >= max) {
            return;
        }
        if (!groupEnabled(categorize(victim))) {
            return;
        }
        String line = ChatColor.WHITE + label(victim) + " " + content(current, max)
                + ChatColor.GRAY + " (-" + damage + ")";
        for (Player viewer : audienceFor(victim, attacker)) {
            viewer.sendMessage(line);
        }
    }

    private List<Player> audienceFor(LivingEntity victim, Player attacker) {
        List<Player> viewers = new ArrayList<Player>();
        if (!nearby) {
            if (receives(attacker)) {
                viewers.add(attacker);
            }
            return viewers;
        }
        double radiusSq = (double) nearbyRadius * nearbyRadius;
        for (Player p : victim.getWorld().getPlayers()) {
            if (p.getLocation().distance(victim.getLocation()) * p.getLocation().distance(victim.getLocation()) <= radiusSq
                    && receives(p)) {
                viewers.add(p);
            }
        }
        return viewers;
    }

    private boolean receives(Player p) {
        return !playerState.getBoolean(p.getName() + ".muted", false) && p.hasPermission("mobhealth.see");
    }

    // ================================================================= text

    private String content(int current, int max) {
        String bar = bar(current, max);
        String value = HealthBarFormatter.value(current, max, valueStyle);
        switch (chatContent) {
            case BAR:
                return bar;
            case NUMBERS:
                return ChatColor.WHITE + value;
            default:
                return value.isEmpty() ? bar : bar + " " + ChatColor.WHITE + value;
        }
    }

    private String bar(int current, int max) {
        int filled = HealthBarFormatter.filledSegments(current, max, barSegments);
        double fraction = HealthBarFormatter.fraction(current, max);
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GRAY).append('[');
        sb.append(fraction > 0.5D ? ChatColor.GREEN : fraction > 0.25D ? ChatColor.YELLOW : ChatColor.RED);
        for (int i = 0; i < filled; i++) {
            sb.append(barFilledChar);
        }
        if (filled < barSegments) {
            sb.append(ChatColor.DARK_GRAY);
            for (int i = filled; i < barSegments; i++) {
                sb.append(barEmptyChar);
            }
        }
        sb.append(ChatColor.GRAY).append(']');
        return sb.toString();
    }

    // ================================================================= Beta 1.7.3 creatures

    /** What each creature spawned with in Beta 1.7.3; the API has no way to ask. */
    private static int maxHealth(LivingEntity e) {
        if (e instanceof Player) return 20;
        if (e instanceof Giant) return 100;
        if (e instanceof Slime) {
            int size = ((Slime) e).getSize();
            return Math.max(1, size * size);
        }
        if (e instanceof Ghast) return 10;
        if (e instanceof Wolf) return ((Wolf) e).isTamed() ? 20 : 8;
        if (e instanceof Zombie || e instanceof Skeleton || e instanceof Creeper
                || e instanceof Spider || e instanceof PigZombie) return 20;
        if (e instanceof Pig || e instanceof Cow || e instanceof Squid) return 10;
        if (e instanceof Sheep) return 8;
        if (e instanceof Chicken) return 4;
        return 20;
    }

    private static MobCategory categorize(LivingEntity e) {
        if (e instanceof Player) return MobCategory.PLAYER;
        if (e instanceof Wolf || e instanceof PigZombie) return MobCategory.NEUTRAL;
        if (e instanceof Monster || e instanceof Slime || e instanceof Ghast) return MobCategory.HOSTILE;
        if (e instanceof Animals || e instanceof Squid) return MobCategory.PASSIVE;
        return MobCategory.PASSIVE;
    }

    private boolean groupEnabled(MobCategory c) {
        switch (c) {
            case HOSTILE: return hostile;
            case NEUTRAL: return neutral;
            case PASSIVE: return passive;
            case PLAYER:  return players;
            default:      return false;
        }
    }

    private static String label(LivingEntity e) {
        if (e instanceof Player) return ((Player) e).getName();
        if (e instanceof PigZombie) return "Zombie Pigman";
        if (e instanceof Giant) return "Giant";
        if (e instanceof Slime) return "Slime";
        if (e instanceof Ghast) return "Ghast";
        if (e instanceof Zombie) return "Zombie";
        if (e instanceof Skeleton) return "Skeleton";
        if (e instanceof Creeper) return "Creeper";
        if (e instanceof Spider) return "Spider";
        if (e instanceof Wolf) return "Wolf";
        if (e instanceof Pig) return "Pig";
        if (e instanceof Cow) return "Cow";
        if (e instanceof Sheep) return "Sheep";
        if (e instanceof Chicken) return "Chicken";
        if (e instanceof Squid) return "Squid";
        return "Mob";
    }

    // ================================================================= commands

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
            loadSettings();
            sender.sendMessage(ChatColor.GREEN + "MobHealth configuration reloaded.");
            return true;
        }
        if (sub.equals("toggle")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players have displays to toggle.");
                return true;
            }
            if (!sender.hasPermission("mobhealth.toggle")) {
                sender.sendMessage(ChatColor.RED + "You may not toggle MobHealth.");
                return true;
            }
            Player p = (Player) sender;
            String path = p.getName() + ".muted";
            boolean muted = args.length > 1
                    ? args[1].equalsIgnoreCase("off")
                    : !playerState.getBoolean(path, false);
            playerState.setProperty(path, muted);
            playerState.save();
            sender.sendMessage(muted
                    ? ChatColor.YELLOW + "MobHealth displays hidden for you."
                    : ChatColor.GREEN + "MobHealth displays shown for you.");
            return true;
        }
        return false;
    }

    private static <E extends Enum<E>> E enumOr(String raw, E fallback) {
        try {
            return Enum.valueOf(fallback.getDeclaringClass(), raw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return fallback;
        }
    }

    private static char firstChar(String s, char fallback) {
        return (s == null || s.isEmpty()) ? fallback : s.charAt(0);
    }
}
