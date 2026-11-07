package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents a burning status effect that causes damage over time.
 * Burning can be inflicted in two ways:
 * - Torch attacks: 7 turns duration, 3 damage per turn
 * - Fire terrain: 5 turns duration, 5 damage per turn
 * Multiple burning effects can stack on the same actor.
 *
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public class BurningByTorch implements Status {

    /**
     * Number of turns remaining for the burning effect
     */
    private int turnsRemaining;

    /**
     * Amount of damage dealt per turn
     */
    private final int damagePerTurn;

    /**
     * Default constructor for torch-inflicted burning.
     * Creates a burning effect that lasts 7 turns and deals 3 damage per turn.
     */
    public BurningByTorch() {
        this.turnsRemaining = 7;
        this.damagePerTurn = 3;
    }

    /**
     * Constructor for custom burning effects (e.g., fire terrain).
     *
     * @param turns the number of turns the burning effect lasts
     * @param damage the amount of damage dealt per turn
     */
    public BurningByTorch(int turns, int damage) {
        this.turnsRemaining = turns;
        this.damagePerTurn = damage;
    }

    /**
     * Called each game tick to update the burning status.
     * Decrements the remaining turns counter.
     *
     * @param currEntity the entity with this status
     * @param location the current location of the entity
     */
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        if (turnsRemaining > 0) {
            turnsRemaining--;
        }
    }

    /**
     * Checks if the burning status is still active.
     *
     * @return true if there are turns remaining, false otherwise
     */
    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }

    /**
     * Gets the number of turns remaining for the burning effect.
     *
     * @return the number of turns remaining
     */
    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    /**
     * Gets the amount of damage dealt per turn by the burning effect.
     *
     * @return the damage per turn value
     */
    public int getDamagePerTurn() {
        return damagePerTurn;
    }
}
