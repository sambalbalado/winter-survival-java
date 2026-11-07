package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.ConsumeAction;
import game.app.Abilities;
import game.items.Consumable;

/**
 * A behaviour that allows an {@link Actor} to automatically consume edible items found
 * on the ground at its current location. The actor must have the
 * {@link Abilities#CONSUMES_GROUND_ITEMS} ability.
 * <p>
 * When an edible item is detected, it creates a one-time action to pick up and consume it
 * using the existing {@link ConsumeAction} logic.
 *
 * @author Sai Sann Lone
 * @version 1.0
 */
public class ConsumeGroundItemsBehaviour implements Behaviour {
    /**
     * Generates an action for consuming a ground item if possible.
     *
     * @param actor the actor performing the behaviour
     * @param map   the map containing the actor
     * @return an action to consume an item, or {@code null} if none available
     */

    @Override
    public Action generateAction(Actor actor, GameMap map) {
        if (!actor.hasAbility(Abilities.CONSUMES_GROUND_ITEMS)) {
            return null;
        }

        Location here = map.locationOf(actor);

        for (Item item : here.getItems()) {
            if (item.hasAbility(Abilities.EAT_FRUIT)) {
                return new ConsumeGroundItemAction(item);
            }
        }

        return null;
    }

    /**
     * An action that removes an edible item from the ground, adds it to the actor’s
     * inventory, and delegates to {@link ConsumeAction}.
     */

    private static class ConsumeGroundItemAction extends Action {
        private final Item item;

        /**
         * Creates a new consume-ground-item action.
         *
         * @param item the edible item
         */

        public ConsumeGroundItemAction(Item item) {
            this.item = item;
        }

        /**
         * Moves the item to the actor’s inventory and executes a {@link ConsumeAction}.
         *
         * @param actor the actor performing the action
         * @param map   the current map
         * @return the result message from {@link ConsumeAction}
         */

        @Override
        public String execute(Actor actor, GameMap map) {
            Location location = map.locationOf(actor);

            // Move item ground -> inventory so ConsumeAction (inventory-only) can run unchanged
            if (location.getItems().contains(item)) {
                location.removeItem(item);
            }
            actor.addItemToInventory(item);

            // Delegate to existing ConsumeAction (keeps exact message layout and effects)
            return new ConsumeAction(item, (Consumable) item).execute(actor, map);
        }

        /**
         * Returns a simple menu description.
         *
         * @param actor the actor performing the action
         * @return a short description for the menu
         */

        @Override
        public String menuDescription(Actor actor) {
            return actor + " consumes " + item;
        }
    }
}
