package com.sablednah.mobhealth.bukkit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.sablednah.mobhealth.core.MobCategory;

import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

/**
 * Classifies a Bukkit entity into the classic MobHealth groups.
 *
 * <p>Mostly by interface ({@link Monster}, {@link LivingEntity}), which survives every API drop.
 * The exceptions are listed by {@code EntityType} <em>name</em> rather than by constant: a name a
 * server has never heard of simply never matches, where a reference to a missing enum constant
 * would refuse to load the class on that server. That is what lets one class list bees (1.15) and
 * wardens (1.19) and still run on 1.7.</p>
 */
public final class EntityCategorizer {

    private EntityCategorizer() {}

    /** Hostile until provoked. Mirrors the Forge side's {@code NeutralMob} marker. */
    private static final Set<String> NEUTRAL = names(
            "ENDERMAN", "WOLF", "PIG_ZOMBIE", "ZOMBIFIED_PIGLIN", "IRON_GOLEM", "BEE",
            "POLAR_BEAR", "LLAMA", "TRADER_LLAMA", "PANDA", "DOLPHIN");

    /** Hostiles that do not implement Bukkit's {@link Monster}. Mirrors the Forge {@code Enemy} marker. */
    private static final Set<String> HOSTILE = names(
            "SLIME", "MAGMA_CUBE", "GHAST", "PHANTOM", "SHULKER", "HOGLIN", "ZOGLIN",
            "ENDER_DRAGON", "WITHER", "BREEZE", "PIGLIN", "PIGLIN_BRUTE");

    private static final Set<String> BOSS = names("ENDER_DRAGON", "WITHER");

    public static MobCategory categorize(Entity entity) {
        if (entity instanceof Player) {
            return MobCategory.PLAYER;
        }
        if (!(entity instanceof LivingEntity)) {
            return MobCategory.OTHER;
        }
        String type = typeName(entity);
        if (NEUTRAL.contains(type)) {
            return MobCategory.NEUTRAL;
        }
        if (entity instanceof Monster || HOSTILE.contains(type)) {
            return MobCategory.HOSTILE;
        }
        // Any other living thing (cows, villagers, squid, armour stands...) is treated as passive;
        // the config's default overrides switch armour stands off, as on Forge.
        return MobCategory.PASSIVE;
    }

    public static boolean isBoss(Entity entity) {
        return BOSS.contains(typeName(entity));
    }

    /** A dragon's hitboxes are separate entities; report the dragon. */
    public static LivingEntity livingTarget(Entity entity) {
        if (entity instanceof ComplexEntityPart) {
            return ((ComplexEntityPart) entity).getParent();
        }
        return entity instanceof LivingEntity ? (LivingEntity) entity : null;
    }

    /** The mob's type as a label: {@code Zombie}, {@code Cave Spider}, {@code Zombified Piglin}. */
    public static String typeLabel(Entity entity) {
        String[] words = typeName(entity).toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (w.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
        }
        return sb.toString();
    }

    private static String typeName(Entity entity) {
        try {
            return entity.getType().name();
        } catch (Throwable t) {
            return "UNKNOWN"; // a modded entity with no EntityType; treat as passive, non-boss
        }
    }

    private static Set<String> names(String... names) {
        return new HashSet<String>(Arrays.asList(names));
    }
}
