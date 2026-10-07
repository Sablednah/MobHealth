package com.sablednah.mobhealth.bukkit;

import com.sablednah.mobhealth.bukkit.compat.Compat;
import com.sablednah.mobhealth.bukkit.compat.CompatFactory;
import com.sablednah.mobhealth.core.BuildInfo;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * MobHealth for Bukkit: shows the damage you deal and the health a mob has left.
 *
 * <p>One set of classes serves every server from 1.7.2 to 26.3; what differs between them is
 * behind {@link Compat}, chosen at startup. The log says which adapter was chosen, which is how
 * a bug report tells you what the server could and could not do.</p>
 */
public final class MobHealthPlugin extends JavaPlugin {

    private final BukkitConfig cfg = new BukkitConfig();
    private Compat compat;
    private PlayerState players;
    private DisplayManager displays;
    private BukkitTask ticker;

    @Override
    public void onEnable() {
        // The build stamp is the half of a bug report that matters: it says what actually RAN.
        getLogger().info("MobHealth " + BuildInfo.describe());

        compat = CompatFactory.create(this);
        getLogger().info("Server " + getServer().getBukkitVersion() + ": using " + compat.describe());

        saveDefaultConfig();
        cfg.load(getConfig(), getLogger());
        warnUnavailable();

        players = new PlayerState(this, compat);
        displays = new DisplayManager(cfg, compat, players);

        getServer().getPluginManager().registerEvents(new DamageListener(this, displays), this);
        Commands commands = new Commands(this);
        getCommand("mobhealth").setExecutor(commands);
        getCommand("mobhealth").setTabCompleter(commands);

        ticker = getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                displays.tick();
            }
        }, 1L, 1L);
    }

    @Override
    public void onDisable() {
        if (ticker != null) {
            ticker.cancel();
            ticker = null;
        }
        if (displays != null) {
            displays.shutdown(); // give every mob its name back, drop every bar
        }
    }

    /** {@code /mobhealth reload}: re-read config.yml. */
    public void reloadSettings() {
        reloadConfig();
        cfg.load(getConfig(), getLogger());
        warnUnavailable();
    }

    /** Say once, at startup, which enabled modes this server cannot show, rather than silently not showing them. */
    private void warnUnavailable() {
        if (cfg.actionBar && !compat.supportsActionBar()) {
            getLogger().info("display.actionBar is on, but this server has no action bar (needs 1.8+); skipping it.");
        }
        if (cfg.bossBar && !compat.supportsBossBar()) {
            getLogger().info("display.bossBar is on, but this server has no boss bar API (needs 1.9+); skipping it.");
        }
        if (cfg.graphical && !compat.supportsAttachedBar()) {
            getLogger().info("display.graphical is on, but this server has no display entities (needs 1.19.4+); the nameplate is the bar here.");
        }
        if (cfg.damageIndicators && !compat.supportsFloatingText()) {
            getLogger().info("display.damageIndicators is on, but this server cannot float text (needs 1.8+); skipping it.");
        }
    }

    public PlayerState players() {
        return players;
    }
}
