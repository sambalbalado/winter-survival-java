package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Represents a source that can be drunk from by an actor.
 * <p>
 * Implementing classes define what happens when an actor drinks
 * from the source.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public interface Drinkable {

    /**
     * Defines what happens when the actor drinks from this source.
     *
     * @param actor the actor drinking
     * @return a string describing the result of drinking
     */
    String drunkBy(Actor actor);
}
