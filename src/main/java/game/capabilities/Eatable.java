package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Represents an item or source that can be eaten by an actor.
 * <p>
 * Implementing classes define what happens when an actor consumes
 * the item.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public interface Eatable {

    /**
     * Defines what happens when the actor eats this item.
     *
     * @param actor the actor eating
     * @return a string describing the result of eating
     */
    String eatenBy(Actor actor);
}
