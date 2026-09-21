package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Interface for actors or objects that can be slept on or interacted with for sleeping.
 * <p>
 * Implementing classes define how an actor sleeps and whether sleep can continue.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public interface Sleepable {

    /**
     * Performs the sleep action by the given actor.
     *
     * @param actor the actor that is sleeping
     * @return a string describing the result of sleeping
     */
    String sleptBy(Actor actor);

    /**
     * Checks if the sleeping can continue.
     *
     * @return true if sleep can continue, false otherwise
     */
    boolean canContinueSleep();
}
