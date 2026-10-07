package com.sablednah.mobhealth.core;

/**
 * The available ways MobHealth can present a mob's health. Any combination may be enabled
 * by the server/modpack via config.
 *
 * <p>Loader-agnostic on purpose (no Minecraft imports) so it can be reused by a future
 * Fabric/other port unchanged.
 */
public enum DisplayMode {
    /** Send a chat message to the attacker (classic behaviour). */
    CHAT,
    /** Append a text health bar to the entity's name tag above its head. Works on vanilla clients. */
    NAMEPLATE,
    /** Show the vanilla boss-bar widget at the top of the attacker's screen. Works on vanilla clients. */
    BOSS_BAR,
    /** Draw a graphical floating bar above the mob. Requires this mod installed on the client. */
    GRAPHICAL;
}
