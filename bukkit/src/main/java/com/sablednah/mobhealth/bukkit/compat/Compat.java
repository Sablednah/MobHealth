package com.sablednah.mobhealth.bukkit.compat;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Everything the plugin wants from the server that the oldest supported API (Bukkit 1.7.2) cannot
 * give it. One implementation per API step, each extending the one before, chosen at startup by
 * {@link CompatFactory} from the running server's version.
 *
 * <p>The plugin body never asks "which version is this?" — it asks the adapter whether a capability
 * exists and uses it if so. That keeps the version knowledge in one place, and it is what lets a
 * single set of plugin classes serve 1.7.2 and 26.3 alike.</p>
 *
 * <p>Every method here must be safe to call on any server the adapter was chosen for: an adapter
 * that reports {@code supportsX() == false} returns {@code null} / does nothing from {@code x()}.</p>
 */
public interface Compat {

    /** One line for the startup log: which adapter was chosen and what it can do. */
    String describe();

    // ------------------------------------------------------------------ action bar

    boolean supportsActionBar();

    /** Show legacy-formatted ({@code §}-coded) text on the line above the hotbar. */
    void sendActionBar(Player player, String legacyText);

    // ------------------------------------------------------------------ boss bar

    boolean supportsBossBar();

    /**
     * Create a hidden boss bar; the caller sets its viewers.
     *
     * @param colourName one of PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE (unknown = RED)
     */
    BossBarHandle createBossBar(String title, String colourName);

    // ------------------------------------------------------------------ floating text

    boolean supportsFloatingText();

    /**
     * Spawn a piece of free-floating text (a damage number) at a point in the world. Shown only to
     * {@code viewers} where {@link #perViewerEntities()} is true; to everyone nearby otherwise.
     */
    FloatingText spawnFloatingText(Location at, String legacyText, Collection<? extends Player> viewers);

    /** Whether spawned entities can be shown to some players and hidden from the rest. */
    boolean perViewerEntities();

    // ------------------------------------------------------------------ attached bar

    /** Whether a bar can ride on a mob as a free-floating piece of text (1.19.4+ display entities). */
    boolean supportsAttachedBar();

    /**
     * Put a piece of text above a mob, riding it, so it follows the mob around. The Bukkit stand-in
     * for the Forge mod's client-drawn graphical bar.
     *
     * @param offset extra height above the mob's head, in blocks
     */
    AttachedBar attachBar(LivingEntity mob, String legacyText, double offset, Collection<? extends Player> viewers);

    // ------------------------------------------------------------------ entities

    /**
     * The entity's id in the form the config's {@code overrides} use: {@code minecraft:zombie}.
     * Before 1.14 there is no registry key to read, so it is the {@code EntityType} constant
     * lower-cased ({@code minecraft:pig_zombie} rather than {@code minecraft:zombified_piglin}).
     */
    String entityKey(Entity entity);

    /** A boolean flag on an entity. Persistent across restarts where the server can (1.14+). */
    boolean flag(Entity entity, String key);

    void setFlag(Entity entity, String key, boolean value);

    // ------------------------------------------------------------------ players

    /** Whether {@link #playerFlag} is backed by the player's own save data (1.14+). */
    boolean supportsPersistentPlayerData();

    /** A boolean flag on a player, or {@code null} if never set. */
    Boolean playerFlag(Player player, String key);

    void setPlayerFlag(Player player, String key, boolean value);
}
