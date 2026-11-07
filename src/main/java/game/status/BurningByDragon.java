package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actors.Actor;


/**
 * Represents a burning status effect applied to an {@link Actor} or any {@link GameEntity}.
 * <p>
 * While active, this status indicates that the entity is burning and will take damage
 * (implementation to apply damage may be handled externally). The effect lasts for a
 * configurable number of turns (duration) and decreases over time.
 * </p>
 *
 * <p>Usage:</p>
 * <pre>
 *     Actor enemy = ...;
 *     Burning burning = new Burning(3); // lasts 3 turns
 *     enemy.addCapability(burning);
 * </pre>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class BurningByDragon implements Status {

    /**
     * The number of turns remaining before the burning effect wears off.
     */
    private int duration;
    private int tickCount;

    /**
     * Creates a new {@code Burning} status with the specified duration.
     *
     * @param duration the number of turns the status should remain active
     */
    public BurningByDragon(int duration) {
        this.duration = duration;
    }

    /**
     * Returns the number of turns left before the burning effect ends.
     *
     * @return remaining duration in turns
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Updates the burning status each turn.
     * <p>
     * This method decreases the duration by 1 each time it is called, simulating
     * the effect wearing off over time.
     * </p>
     *
     * @param entity   the game entity that has this status
     * @param location the current location of the entity
     */
    @Override
    public void tickStatus(GameEntity entity, Location location) {
        tickCount++;
        if (tickCount % 2 == 0 && duration > 0) {
            duration--; // decrement remaining turns
        }
    }

    /**
     * Checks if the burning status is still active.
     *
     * @return {@code true} if duration is greater than 0, otherwise {@code false}
     */
    @Override
    public boolean isStatusActive() {
        return duration > 0;
    }

    /**
     * Provides a human-readable string describing the burning status.
     *
     * @return a string representation, including turns remaining
     */
    @Override
    public String toString() {
        return "Burning (" + duration + " turns left)";
    }
}
