package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Tamable;

/**
 * An action that allows a player to tame a {@link Tamable} animal by offering a food item.
 * The food item is removed from the actor's inventory, and the actor becomes the animal's owner.
 *
 * @author Esaam Matudin
 * @version 6.4
 */
public class TameAction extends Action {

    /** The animal to tame. */
    private final Tamable animal;

    /** The food item used to tame the animal. */
    private final Item food;

    /**
     * Constructor.
     *
     * @param animal the animal to tame
     * @param food   the food item used to tame the animal
     */
    public TameAction(Tamable animal, Item food) {
        this.animal = animal;
        this.food = food;
    }

    /**
     * Executes the taming action.
     * Removes the food from the actor's inventory and tames the animal.
     *
     * @param actor the actor performing the action
     * @param map   the current game map
     * @return a string describing the result of the taming
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Remove the food from the player's inventory
        actor.removeItemFromInventory(food);
        // Tame the animal and assign the player as owner
        animal.tame(actor);

        return actor + " tamed " + animal + " using " + food + "!";
    }

    /**
     * Returns a description of this action for the menu.
     *
     * @param actor the actor performing the action
     * @return a string describing the taming action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " will tame " + animal + " using " + food;
    }
}
