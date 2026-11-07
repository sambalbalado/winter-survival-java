package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.app.Abilities;
import game.items.Consumable;

/**
 * An action that allows an actor to consume an item from their inventory.
 *
 * This action handles the consumption of items by actors who have the ability
 * to consume. The action checks if the actor has the required CAN_CONSUME ability,
 * removes the item from their inventory, and applies the consumable's effects.
 * If the actor lacks the consumption ability, the action will fail gracefully.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class ConsumeAction extends Action {
    private final Item item;
    private final Consumable consumer;

    /**
     * Constructor for creating a ConsumeAction.
     *
     * @param item the Item to be consumed from the actor's inventory
     * @param consumer the Consumable interface that defines how the item affects the actor
     */
    public ConsumeAction(Item item, Consumable consumer) {
        this.item = item;
        this.consumer = consumer;
    }

    /**
     * Executes the consume action.
     *
     * First checks if the actor has the CAN_CONSUME ability. If they do not,
     * the action fails and returns an appropriate message. If they can consume,
     * the item is removed from their inventory and the consumable's effects
     * are applied to the actor.
     *
     * @param actor the Actor attempting to consume the item
     * @param map the GameMap where the action takes place
     * @return a String describing the result of the consumption attempt
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasAbility(Abilities.CAN_CONSUME)) {
            return actor + " cannot consume items.";
        }

        actor.removeItemFromInventory(item);
        consumer.consume(actor);

        if (!actor.isConscious()) {
            String msg = actor.unconscious(map);
            return actor + " consumes " + item + " and " + msg;
        }

        return actor + " consumes " + item ;
    }


    /**
     * Returns a description of this action suitable for display in a menu.
     *
     * @param actor the Actor who would perform this action
     * @return a String describing the consumption action in a user-friendly format
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " consumes " + item;
    }
}
