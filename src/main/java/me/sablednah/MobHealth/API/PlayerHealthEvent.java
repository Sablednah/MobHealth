package main.java.me.sablednah.MobHealth.API;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerHealthEvent extends Event implements Cancellable {

	private static final HandlerList	handlers	= new HandlerList();
	private boolean						cancelled;
	private int							health;
	private int							maxHealth;
	private Scoreboard					scoreboard;
	private Objective					objective;
	private Score						score;

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public PlayerHealthEvent(int health, int maxHealth, Scoreboard scoreboard, Objective objective, Score score) {
		this.setHealth(health);
		this.setMaxHealth(maxHealth);
		this.setScoreboard(scoreboard);
		this.setObjective(objective);
		this.setScore(score);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean bln) {
		this.cancelled = bln;
	}

	/**
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @param health the health to set
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * @return the maxHealth
	 */
	public int getMaxHealth() {
		return maxHealth;
	}

	/**
	 * @param maxHealth the maxHealth to set
	 */
	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	/**
	 * @return the scoreboard
	 */
	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	/**
	 * @param scoreboard the scoreboard to set
	 */
	public void setScoreboard(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}

	/**
	 * @return the objective
	 */
	public Objective getObjective() {
		return objective;
	}

	/**
	 * @param objective the objective to set
	 */
	public void setObjective(Objective objective) {
		this.objective = objective;
	}

	/**
	 * @return the score.  Goal!
	 */
	public Score getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(Score score) {
		this.score = score;
	}

}
