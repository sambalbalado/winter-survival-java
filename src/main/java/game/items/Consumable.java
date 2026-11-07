package game.items;

import edu.monash.fit2099.engine.actors.Actor;

/**
 * Interface for items that can be consumed by actors.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public interface Consumable {
    /**
     * Called when an Explorer consumes this item.
     * @param actor the Explorer using the item
     */
    void consume(Actor actor);
}
