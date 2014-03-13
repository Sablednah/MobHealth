package main.java.me.sablednah.MobHealth;

import org.bukkit.event.Event;

import com.herocraftonline.heroes.api.events.SkillDamageEvent;


public class HeroesUtils {

    public static boolean isHeroesEvent(Event event) {
        if (event instanceof SkillDamageEvent) {
            return true;
        }
        return false;
    }
}
