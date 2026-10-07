package com.sablednah.mobhealth.compat9;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.sablednah.mobhealth.bukkit.compat.BossBarHandle;
import com.sablednah.mobhealth.compat8.Compat18;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/** 1.9: the boss bar API. Touches nothing newer, so it loads on 1.9 and 1.10 as well as later. */
public class Compat19 extends Compat18 {

    public Compat19(Plugin plugin) {
        super(plugin);
    }

    @Override
    public String describe() {
        return "Bukkit 1.9 (chat, nameplates, armour-stand damage numbers, boss bars"
                + (supportsActionBar() ? ", NMS action bar)" : "; no action bar)");
    }

    @Override
    public boolean supportsBossBar() {
        return true;
    }

    @Override
    public BossBarHandle createBossBar(String title, String colourName) {
        final BossBar bar = Bukkit.createBossBar(title, colour(colourName), BarStyle.SOLID, new BarFlag[0]);
        bar.setVisible(true);
        return new BossBarHandle() {
            @Override
            public void setTitle(String legacyTitle) {
                bar.setTitle(legacyTitle);
            }

            @Override
            public void setProgress(double fraction) {
                bar.setProgress(fraction);
            }

            @Override
            public void setViewers(Collection<? extends Player> viewers) {
                List<Player> current = new ArrayList<Player>(bar.getPlayers());
                for (Player p : current) {
                    if (!viewers.contains(p)) {
                        bar.removePlayer(p);
                    }
                }
                for (Player p : viewers) {
                    if (!current.contains(p)) {
                        bar.addPlayer(p);
                    }
                }
            }

            @Override
            public void remove() {
                bar.removeAll();
                bar.setVisible(false);
            }
        };
    }

    private static BarColor colour(String name) {
        try {
            return BarColor.valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return BarColor.RED;
        }
    }
}
