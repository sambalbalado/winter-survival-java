package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Poison status that deals damage over time.
 * <p>
 * Defaults to 5 turns at 4 damage/turn, and also supports custom duration and damage.
 * Internally decrements remaining turns every second engine tick.
 * </p>
 * @author Taabish Farooq Bhat and Sai Sann Lone
 * @version 2.1
 */
public class Poisoned implements Status {

    /**
     * Number of turns remaining for the poisoned effect
     */
    private int turnsRemaining;

    /**
     * Amount of damage dealt per turn
     */
    private final int damagePerTurn;

    /**
     * Counter to track ticks (increments twice per game turn)
     */
    private int tickCount = 0;

    /**
     * Constructor for Poisoned status.
     * Creates a poisoned effect that lasts 5 turns and deals 4 damage per turn.
     */
    public Poisoned() {
        this.turnsRemaining = 5;
        this.damagePerTurn = 4;
    }

    /** NEW: configurable poison (for swamps: 10 turns, 5 dmg/turn) */
    public Poisoned(int turns, int damagePerTurn) {
        this.turnsRemaining = turns;
        this.damagePerTurn = damagePerTurn;
    }

    /**
     * Advances poison timing. Decrements remaining turns every second tick.
     *
     * @param currEntity entity bearing this status
     * @param location   entity location
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        tickCount++;
        // Only decrement every second tick
        if (tickCount % 2 == 0 && turnsRemaining > 0) {
            turnsRemaining--;
        }
    }

    /**
     * Checks if the poisoned status is still active.
     *
     * @return true if there are turns remaining, false otherwise
     */
    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }

    /**
     * Gets the number of turns remaining for the poisoned effect.
     *
     * @return the number of turns remaining
     */
    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    /**
     * Gets the amount of damage dealt per turn by the poisoned effect.
     *
     * @return the damage per turn value
     */
    public int getDamagePerTurn() {
        return damagePerTurn;
    }

    @Override
    public String toString() {
        return "[POISONED " + turnsRemaining + "@" + damagePerTurn + "]";
    }
}
