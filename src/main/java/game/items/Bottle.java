package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Drinkable;
import game.GameAttributes;
import game.actions.DrinkAction;

/**
 * A bottle that can be drunk by actors to restore hydration.
 * <p>
 * Each bottle has a limited number of uses. Drinking increases the actor's hydration.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class Bottle extends Item implements Drinkable {

    /** Number of uses left for drinking */
    private int uses = 5;

    /***
     * Constructor for a Bottle.
     *
     * @param name the name of this Item
     * @param displayChar the character to represent this item on the ground
     * @param portable true if and only if the Item can be picked up
     */
    public Bottle(String name, char displayChar, boolean portable) {
        super(name, displayChar, portable);
        this.enableAbility(Status.DRINK);
    }

    /**
     * Returns a list of allowable actions for the actor carrying this item.
     * Adds a DrinkAction if the bottle has remaining uses.
     *
     * @param owner the actor that owns the item
     * @param map the map where the actor is located
     * @return an unmodifiable list of Actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = super.allowableActions(owner, map);

        if (uses > 0) {
            actions.add(new DrinkAction(this));
        }
        return actions;
    }

    /**
     * Performs drinking for the given actor.
     * Decreases the number of uses and increases the actor's hydration.
     *
     * @param actor the actor that drinks from the bottle
     * @return a message indicating the result of drinking
     */
    @Override
    public String drunkBy(Actor actor) {
        if (uses > 0) {
            int hydrationAdd = 4;
            actor.modifyAttribute(GameAttributes.HYDRATION, ActorAttributeOperation.INCREASE, hydrationAdd);
            uses--;
            return actor + " drinks from " + this.getClass().getSimpleName() + " (" + uses + " uses left)";
        } else {
            return actor + " tries to drink, but the bottle is empty!";
        }
    }
}
