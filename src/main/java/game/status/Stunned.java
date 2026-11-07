package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents a stunned status effect applied to a {@link GameEntity},
 * typically an {@link Actor}.
 * <p>
 * While active, this status indicates that the entity is stunned and may not
 * be able to perform actions (the prevention of actions should be enforced
 * externally in the game engine). The effect lasts for a configurable number
 * of turns and decreases over time.
 * </p>
 *
 * <p>Usage:</p>
 * <pre>
 *     Actor enemy = ...;
 *     Stunned stunned = new Stunned(2); // lasts 2 turns
 *     enemy.addCapability(stunned);
 * </pre>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class Stunned implements Status {

    /**
     * The number of turns remaining before the stunned effect wears off.
     */
    private int duration;
    private int tickCount;

    /**
     * Creates a new {@code Stunned} status with the specified duration.
     *
     * @param duration the number of turns the status should remain active
     */
    public Stunned(int duration) {
        this.duration = duration;
    }

    /**
     * Returns the number of turns left before the stunned effect ends.
     *
     * @return remaining duration in turns
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Updates the stunned status each turn.
     * <p>
     * This method decreases the duration by 1 each time it is called,
     * simulating the effect wearing off over time.
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
     * Checks if the stunned status is still active.
     *
     * @return {@code true} if duration is greater than 0, otherwise {@code false}
     */
    @Override
    public boolean isStatusActive() {
        return duration > 0;
    }

    /**
     * Provides a human-readable string describing the stunned status.
     *
     * @return a string representation, including turns remaining
     */
    @Override
    public String toString() {
        return "Stunned (" + duration + " turns left)";
    }
}
