package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.Coatable;

/**
 * Action for coating a weapon with an item from the actor's inventory.
 * The coating item is consumed (removed from inventory) in the process.
 *
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public class CoatWeaponAction extends Action {

    /**
     * The item to be used as coating material
     */
    private Item coatingItem;

    /**
     * The weapon to be coated
     */
    private Coatable weapon;


    /**
     * Constructor for CoatWeaponAction.
     *
     * @param weapon the weapon to coat
     * @param coatingItem the item to use as coating
     */
    public CoatWeaponAction(Coatable weapon, Item coatingItem) {
        this.weapon = weapon;
        this.coatingItem = coatingItem;
    }

    /**
     * Executes the coating action.
     * Applies the coating to the weapon and removes the item from inventory.
     *
     * @param actor the actor performing the action
     * @param map the game map
     * @return description of the result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Apply the coating
        weapon.applyCoating(coatingItem.toString());

        actor.removeItemFromInventory(coatingItem);

        return actor + " coats " + weapon + " with " + coatingItem;
    }

    /**
     * Returns a description for the menu.
     *
     * @param actor the actor who would perform the action
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " coats " + weapon.getClass().getSimpleName() + " with " + coatingItem;
    }
}
