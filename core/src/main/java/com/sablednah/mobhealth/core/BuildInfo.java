package com.sablednah.mobhealth.core;

import java.io.InputStream;
import java.util.Properties;

/**
 * Which build this is.
 *
 * <p>A version number answers "which release". During development that is a different
 * question from "which bytes": a jar rebuilt under an unchanged version number is
 * indistinguishable from the one it replaced, in a mods folder, on a releases page, or in
 * somebody's bug report.</p>
 *
 * <p><b>The startup log line is the half that matters.</b> A stamp inside the jar says what
 * is on disk now; the log line says what actually <em>ran</em>, which is the question a bug
 * report needs answered. The same values are on the jar manifest ({@code Build-Commit},
 * {@code Build-Branch}, {@code Build-Time}) for inspecting a jar from a shell without
 * loading it — that is the one that tells you an instance jar is stale.</p>
 *
 * <p>Read from a generated properties file rather than from the manifest, because this has
 * to work in a dev run too, where the mod is loaded from a classes directory and there is no
 * jar to carry a manifest.</p>
 *
 * <p>Loader-agnostic on purpose: it reads a resource and nothing else, so a future Fabric
 * port inherits it unchanged along with the rest of {@code core}.</p>
 */
public final class BuildInfo {

    /** Namespaced: a bare {@code /build.properties} would collide with every other mod
     *  doing the same thing on a shared classpath, and you would read somebody else's. */
    private static final String RESOURCE = "/mobhealth/build.properties";

    private static final String COMMIT;
    private static final String BRANCH;
    private static final String TIME;
    private static final String VERSION;

    static {
        String commit = "unknown", branch = "unknown", time = "unknown", version = "unknown";
        try (InputStream in = BuildInfo.class.getResourceAsStream(RESOURCE)) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                commit = p.getProperty("commit", commit);
                branch = p.getProperty("branch", branch);
                time = p.getProperty("time", time);
                version = p.getProperty("version", version);
            }
        } catch (Exception ignored) {
            // A missing or unreadable stamp must never stop the mod loading: it is
            // diagnostic information, not a dependency.
        }
        COMMIT = commit;
        BRANCH = branch;
        TIME = time;
        VERSION = version;
    }

    public static String commit() {
        return COMMIT;
    }

    public static String branch() {
        return BRANCH;
    }

    /** When the build's commit was made (UTC), not when Gradle ran: a wall-clock stamp would
     *  change on every invocation and cost the build its up-to-date checks. On a {@code -dirty}
     *  build this is still the commit's time, which loses nothing — {@code -dirty} has already
     *  said the bytes are not the commit's. */
    public static String time() {
        return TIME;
    }

    /** The one-line form for the startup log: {@code 2.5.1 (build a1b2c3d4 on main,
     *  2026-09-10T07:24:24Z)}. A {@code -dirty} suffix on the commit means it was built from
     *  uncommitted changes, which is worth seeing in somebody's log before you spend an hour
     *  reproducing against a tag. */
    public static String describe() {
        return VERSION + " (build " + COMMIT + " on " + BRANCH + ", " + TIME + ")";
    }

    private BuildInfo() {}
}
