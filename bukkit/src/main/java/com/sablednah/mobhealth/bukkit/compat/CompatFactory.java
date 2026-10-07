package com.sablednah.mobhealth.bukkit.compat;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Picks the richest adapter the running server can carry.
 *
 * <p>Adapters are named, not referenced: each lives in its own module compiled against a newer API,
 * and a range jar may or may not contain it. {@code Class.forName} on a name that is absent from
 * this jar, or whose API the server lacks, fails — and the next candidate down is tried. The floor,
 * {@link Compat17}, always exists and always loads.</p>
 */
public final class CompatFactory {

    private CompatFactory() {}

    /** Newest first. Each entry: the minimum server version, and the adapter class to try. */
    private static final String[][] CANDIDATES = {
            {"1.19.4", "com.sablednah.mobhealth.compat19.Compat1194"},
            {"1.18",   "com.sablednah.mobhealth.compat18.Compat118"},
            {"1.14",   "com.sablednah.mobhealth.compat14.Compat114"},
            {"1.11",   "com.sablednah.mobhealth.compat9.Compat111"},
            {"1.9",    "com.sablednah.mobhealth.compat9.Compat19"},
            {"1.8",    "com.sablednah.mobhealth.compat8.Compat18"},
    };

    public static Compat create(Plugin plugin) {
        Logger log = plugin.getLogger();
        int[] server = parse(Bukkit.getBukkitVersion());
        for (String[] candidate : CANDIDATES) {
            if (!atLeast(server, parse(candidate[0]))) {
                continue;
            }
            try {
                Class<?> type = Class.forName(candidate[1]);
                return (Compat) type.getConstructor(Plugin.class).newInstance(plugin);
            } catch (ClassNotFoundException absent) {
                // Not in this jar: a legacy jar on a newer server. Expected; fall through.
            } catch (Throwable failed) {
                // The class is here but would not load or construct against this server. Also
                // expected on exotic forks; say so once, at FINE, and fall through.
                log.log(Level.FINE, "Adapter " + candidate[1] + " unavailable: " + failed);
            }
        }
        return new Compat17(plugin);
    }

    /**
     * {@code "1.8.8-R0.1-SNAPSHOT"} -> {1, 8, 8}; {@code "26.3-R0.1-SNAPSHOT"} -> {26, 3, 0}.
     * Anything unparseable is {0, 0, 0}, which selects the floor rather than crashing.
     */
    static int[] parse(String bukkitVersion) {
        int[] out = new int[3];
        if (bukkitVersion == null) {
            return out;
        }
        String head = bukkitVersion.split("[-+ ]", 2)[0];
        String[] parts = head.split("\\.");
        for (int i = 0; i < parts.length && i < 3; i++) {
            try {
                out[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                break;
            }
        }
        return out;
    }

    static boolean atLeast(int[] actual, int[] wanted) {
        for (int i = 0; i < 3; i++) {
            if (actual[i] != wanted[i]) {
                return actual[i] > wanted[i];
            }
        }
        return true;
    }
}
