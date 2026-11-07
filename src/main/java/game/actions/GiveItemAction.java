package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * An action that allows an actor to give an item to another actor.
 *
 * This action handles the transfer of items between actors. The giver actor
 * removes the specified item from their inventory and adds it to the receiver's
 * inventory. The action validates that the giver actually possesses the item
 * before attempting the transfer, preventing invalid item transactions.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class GiveItemAction extends Action {
    private final Item item;
    private final Actor receiver;

    /**
     * Constructor for creating a GiveItemAction.
     *
     * @param item the Item to be transferred from giver to receiver
     * @param receiver the Actor who will receive the item
     */
    public GiveItemAction(Item item, Actor receiver) {
        this.item = item;
        this.receiver = receiver;
    }

    /**
     * Executes the give item action.
     *
     * First checks if the giver actually has the specified item in their inventory.
     * If they do, the item is removed from the giver's inventory and added to the
     * receiver's inventory. If the giver doesn't have the item, the action fails
     * with an appropriate error message.
     *
     * @param giver the Actor who is giving away the item
     * @param map the GameMap where the action takes place
     * @return a String describing the result of the item transfer attempt
     */
    @Override
    public String execute(Actor giver, GameMap map) {
        if (giver.getItemInventory().contains(item)) {
            giver.removeItemFromInventory(item);
            receiver.addItemToInventory(item);
            return giver + " gives " + item + " to " + receiver + ".";
        }
        return giver + " tried to give " + item + " but doesn't have it!";
    }

    /**
     * Returns a description of this action suitable for display in a menu.
     *
     * @param actor the Actor who would perform this action
     * @return a String describing the give item action in a user-friendly format
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " gives " + item + " to " + receiver;
    }
}
