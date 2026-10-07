package com.sablednah.mobhealth.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sablednah.mobhealth.bukkit.compat.AttachedBar;
import com.sablednah.mobhealth.bukkit.compat.BossBarHandle;
import com.sablednah.mobhealth.bukkit.compat.Compat;
import com.sablednah.mobhealth.bukkit.compat.FloatingText;
import com.sablednah.mobhealth.core.Audience;
import com.sablednah.mobhealth.core.MobCategory;
import com.sablednah.mobhealth.core.NameplateMode;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * The heart of the plugin: given a player-dealt hit, dispatch to every enabled display mode, then
 * revert the temporary ones on a timer. A port of the NeoForge {@code DisplayManager}, with the
 * server-specific bits behind {@link Compat}.
 *
 * <p>All state lives on the main thread; the maps are keyed by entity UUID. {@link #tick()} is run
 * every server tick by the plugin.</p>
 */
public final class DisplayManager {

    /** Entity flag marking a mob whose name tag we are currently controlling. */
    private static final String CTRL = "controlled";

    private final BukkitConfig cfg;
    private final Compat compat;
    private final PlayerState players;

    private long serverTick;
    private final Map<UUID, NameEntry> nameplates = new HashMap<UUID, NameEntry>();
    private final Map<UUID, BossEntry> bossBars = new HashMap<UUID, BossEntry>();
    private final Map<UUID, BarEntry> bars = new HashMap<UUID, BarEntry>();
    private final List<NumberEntry> numbers = new ArrayList<NumberEntry>();

    public DisplayManager(BukkitConfig cfg, Compat compat, PlayerState players) {
        this.cfg = cfg;
        this.compat = compat;
        this.players = players;
    }

    // ================================================================= damage

    /**
     * A player hit a mob.
     *
     * @param damage  health the mob lost (already known to be &gt; 0)
     * @param current the mob's health AFTER the hit; 0 for the killing blow
     */
    public void onHit(LivingEntity victim, Player attacker, double damage, double current, double max) {
        if (cfg.hideUntilDamaged && current >= max) {
            return;
        }
        MobCategory category = EntityCategorizer.categorize(victim);
        boolean boss = EntityCategorizer.isBoss(victim);
        if (!cfg.shouldDisplay(compat.entityKey(victim), category, boss)) {
            return;
        }

        List<Player> viewers = audienceFor(victim, attacker);

        if (cfg.chat) {
            sendChat(viewers, victim, damage, current, max);
        }
        if (cfg.actionBar && compat.supportsActionBar()) {
            sendActionBar(viewers, victim, current, max);
        }
        if (cfg.damageIndicators && compat.supportsFloatingText()) {
            sendDamageNumber(viewers, victim, damage, current);
        }
        if (cfg.nameplate) {
            updateNameplate(victim, current, max, category);
        }
        if (cfg.bossBar && compat.supportsBossBar()) {
            updateBossBar(victim, viewers, current, max, category);
        }
        if (cfg.graphical && compat.supportsAttachedBar()) {
            updateBar(victim, viewers, current, max, category);
        }
    }

    private List<Player> audienceFor(LivingEntity victim, Player attacker) {
        List<Player> viewers = new ArrayList<Player>();
        if (cfg.audience == Audience.ATTACKER) {
            if (players.receivesDisplays(attacker)) {
                viewers.add(attacker);
            }
            return viewers;
        }
        double radiusSq = (double) cfg.nearbyRadius * cfg.nearbyRadius;
        Location at = victim.getLocation();
        // World.getPlayers() rather than Bukkit.getOnlinePlayers(): the latter changed its return
        // type in 1.8 (array -> Collection), so one compiled call cannot run on both sides of it.
        for (Player player : victim.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(at) <= radiusSq && players.receivesDisplays(player)) {
                viewers.add(player);
            }
        }
        return viewers;
    }

    /**
     * The mob's real name for labels — never the bar we injected. A genuine custom name, otherwise
     * the type ("Zombie", "Cave Spider").
     */
    private String cleanName(LivingEntity victim) {
        NameEntry entry = nameplates.get(victim.getUniqueId());
        if (entry != null) {
            return entry.originalName != null ? entry.originalName : EntityCategorizer.typeLabel(victim);
        }
        String custom = victim.getCustomName();
        if (custom != null && !isControlled(victim, custom)) {
            return custom;
        }
        return EntityCategorizer.typeLabel(victim);
    }

    /**
     * Ours? The flag says so; failing that (a restart on a server whose flags do not persist, or a
     * crash mid-display) the name itself does, since every bar carries the same prefix.
     */
    private boolean isControlled(LivingEntity entity, String customName) {
        return compat.flag(entity, CTRL) || BarText.looksLikeBar(customName);
    }

    // ================================================================= chat / action bar

    private void sendChat(List<Player> viewers, LivingEntity victim, double damage, double current, double max) {
        String line = ChatColor.WHITE + cleanName(victim) + " "
                + BarText.content(cfg, current, max, cfg.chatContent)
                + ChatColor.GRAY + " (-" + BarText.trim(damage) + ")";
        for (Player viewer : viewers) {
            viewer.sendMessage(line);
        }
    }

    private void sendActionBar(List<Player> viewers, LivingEntity victim, double current, double max) {
        String line = ChatColor.WHITE + cleanName(victim) + " "
                + BarText.content(cfg, current, max, cfg.actionBarContent);
        for (Player viewer : viewers) {
            compat.sendActionBar(viewer, line);
        }
    }

    // ================================================================= floating damage numbers

    /**
     * A number that rises from the hit and fades. The anchor is upper-body height rather than above
     * the head, so it climbs through empty air instead of straight through a nameplate.
     */
    private void sendDamageNumber(List<Player> viewers, LivingEntity victim, double damage, double current) {
        if (damage < cfg.indicatorMinDamage || viewers.isEmpty()) {
            return;
        }
        boolean fatal = cfg.indicatorMarkKill && current <= 0.0D;
        String text = fatal
                ? ChatColor.GOLD.toString() + ChatColor.BOLD + "-" + BarText.trim(damage)
                : ChatColor.RED + "-" + BarText.trim(damage);
        Location at = victim.getLocation();
        // A little scatter, so a flurry of hits reads as several numbers rather than one flickering.
        at.add((Math.random() - 0.5D) * 0.6D, victim.getEyeHeight() * 0.6D, (Math.random() - 0.5D) * 0.6D);
        FloatingText entity = compat.spawnFloatingText(at, text, viewers);
        if (entity != null) {
            numbers.add(new NumberEntry(entity, at, serverTick + cfg.indicatorDurationTicks));
        }
    }

    // ================================================================= nameplate

    private void updateNameplate(LivingEntity victim, double current, double max, MobCategory category) {
        NameplateMode mode = cfg.nameplateMode;
        UUID id = victim.getUniqueId();
        NameEntry entry = nameplates.get(id);
        if (entry == null) {
            // If the current custom name is already OUR bar (a lapsed window), the real original is
            // absent — never nest a bar inside a bar.
            String custom = victim.getCustomName();
            boolean controlled = custom != null && isControlled(victim, custom);
            String realName = controlled ? null : custom;
            boolean realVisible = !controlled && victim.isCustomNameVisible();
            if (mode == NameplateMode.NAMED_ONLY && realName == null) {
                return; // this mode only decorates already-named mobs
            }
            entry = new NameEntry(victim, realName, realVisible, mode != NameplateMode.ALWAYS);
            nameplates.put(id, entry);
        }

        String display = BarText.content(cfg, current, max, cfg.nameplateContent);
        if (entry.originalName != null) {
            display = entry.originalName + ChatColor.RESET + " " + display;
        }
        victim.setCustomName(display);
        victim.setCustomNameVisible(true);
        compat.setFlag(victim, CTRL, true);
        entry.expiry = serverTick + cfg.displayTicks(category);
    }

    // ================================================================= boss bar

    private void updateBossBar(LivingEntity victim, List<Player> viewers, double current, double max, MobCategory category) {
        UUID id = victim.getUniqueId();
        BossEntry entry = bossBars.get(id);
        if (entry == null) {
            BossBarHandle bar = compat.createBossBar(cleanName(victim), cfg.bossBarColor);
            if (bar == null) {
                return;
            }
            entry = new BossEntry(bar);
            bossBars.put(id, entry);
        }
        String value = BarText.value(cfg, current, max);
        entry.bar.setTitle(ChatColor.WHITE + cleanName(victim) + (value.isEmpty() ? "" : "  " + value));
        entry.bar.setProgress(max <= 0 ? 0 : Math.max(0, Math.min(1, current / max)));
        entry.bar.setViewers(viewers);
        entry.expiry = serverTick + cfg.displayTicks(category);
    }

    // ================================================================= floating bar

    /** The Forge mod's graphical bar, as the server can manage it: text riding the mob. */
    private void updateBar(LivingEntity victim, List<Player> viewers, double current, double max, MobCategory category) {
        if (viewers.isEmpty()) {
            return;
        }
        UUID id = victim.getUniqueId();
        BarEntry entry = bars.get(id);
        String text = BarText.content(cfg, current, max, cfg.graphicalContent);
        if (entry == null || !entry.bar.isValid()) {
            if (entry != null) {
                entry.bar.remove();
            }
            AttachedBar bar = compat.attachBar(victim, text, cfg.graphicalOffset, viewers);
            if (bar == null) {
                return;
            }
            entry = new BarEntry(bar);
            bars.put(id, entry);
        } else {
            entry.bar.setText(text);
            entry.bar.setViewers(viewers);
        }
        entry.expiry = serverTick + cfg.displayTicks(category);
    }

    // ================================================================= death

    /** A tracked mob died: cut its remaining display time to the short death timeout. */
    public void onDeath(LivingEntity entity) {
        UUID id = entity.getUniqueId();
        long deathExpiry = serverTick + cfg.deathTicks;
        BossEntry boss = bossBars.get(id);
        if (boss != null) {
            boss.bar.setProgress(0);
            boss.expiry = Math.min(boss.expiry, deathExpiry);
        }
        NameEntry name = nameplates.get(id);
        if (name != null) {
            name.expiry = Math.min(name.expiry, deathExpiry);
        }
        BarEntry bar = bars.get(id);
        if (bar != null) {
            bar.expiry = Math.min(bar.expiry, deathExpiry);
        }
    }

    // ================================================================= tick / expiry

    public void tick() {
        serverTick++;

        for (Iterator<Map.Entry<UUID, NameEntry>> it = nameplates.entrySet().iterator(); it.hasNext();) {
            NameEntry entry = it.next().getValue();
            LivingEntity entity = entry.entity;
            if (!entity.isValid() && !entity.isDead()) {
                it.remove(); // unloaded with its chunk; the name will be whatever was saved
                continue;
            }
            if (entry.revert && serverTick >= entry.expiry) {
                revert(entry);
                it.remove();
            }
        }

        for (Iterator<Map.Entry<UUID, BossEntry>> it = bossBars.entrySet().iterator(); it.hasNext();) {
            BossEntry entry = it.next().getValue();
            if (serverTick >= entry.expiry) {
                entry.bar.remove();
                it.remove();
            }
        }

        for (Iterator<Map.Entry<UUID, BarEntry>> it = bars.entrySet().iterator(); it.hasNext();) {
            BarEntry entry = it.next().getValue();
            if (serverTick >= entry.expiry || !entry.bar.isValid()) {
                entry.bar.remove();
                it.remove();
            }
        }

        for (Iterator<NumberEntry> it = numbers.iterator(); it.hasNext();) {
            NumberEntry entry = it.next();
            if (serverTick >= entry.expiry || !entry.text.isValid()) {
                entry.text.remove();
                it.remove();
                continue;
            }
            entry.at.add(0, cfg.indicatorRise / cfg.indicatorDurationTicks, 0);
            entry.text.moveTo(entry.at);
        }
    }

    /** Put every display back the way it was. Called on disable so nothing is left behind. */
    public void shutdown() {
        for (NameEntry entry : nameplates.values()) {
            if (entry.revert) {
                revert(entry);
            }
        }
        nameplates.clear();
        for (BossEntry entry : bossBars.values()) {
            entry.bar.remove();
        }
        bossBars.clear();
        for (NumberEntry entry : numbers) {
            entry.text.remove();
        }
        numbers.clear();
        for (BarEntry entry : bars.values()) {
            entry.bar.remove();
        }
        bars.clear();
    }

    private void revert(NameEntry entry) {
        entry.entity.setCustomName(entry.originalName);
        entry.entity.setCustomNameVisible(entry.originalVisible);
        compat.setFlag(entry.entity, CTRL, false);
    }

    // ================================================================= state

    private static final class NameEntry {
        private final LivingEntity entity;
        private final String originalName;
        private final boolean originalVisible;
        private final boolean revert;
        private long expiry;

        private NameEntry(LivingEntity entity, String originalName, boolean originalVisible, boolean revert) {
            this.entity = entity;
            this.originalName = originalName;
            this.originalVisible = originalVisible;
            this.revert = revert;
        }
    }

    private static final class BossEntry {
        private final BossBarHandle bar;
        private long expiry;

        private BossEntry(BossBarHandle bar) {
            this.bar = bar;
        }
    }

    private static final class BarEntry {
        private final AttachedBar bar;
        private long expiry;

        private BarEntry(AttachedBar bar) {
            this.bar = bar;
        }
    }

    private static final class NumberEntry {
        private final FloatingText text;
        private final Location at;
        private final long expiry;

        private NumberEntry(FloatingText text, Location at, long expiry) {
            this.text = text;
            this.at = at;
            this.expiry = expiry;
        }
    }
}
