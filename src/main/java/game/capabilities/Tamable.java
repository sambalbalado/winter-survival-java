package game.capabilities;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Represents an animal that can be tamed by a player.
 * <p>
 * Implementing classes allow animals to transition from wild to tamed states,
 * track ownership, and provide access to their owner.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public interface Tamable {

    /**
     * Tames the animal for the given actor (usually the player).
     *
     * @param owner the actor that tames this animal
     */
    void tame(Actor owner);

    /**
     * Checks if this animal is currently tamed.
     *
     * @return true if tamed, false otherwise
     */
    boolean isTamed();

    /**
     * Gets the actor who tamed this animal.
     *
     * @return the owner of this animal, or null if untamed
     */
    Actor getOwner();
}
