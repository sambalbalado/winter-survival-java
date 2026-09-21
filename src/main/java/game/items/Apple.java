package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.GameAttributes;
import game.actions.EatAction;
import game.capabilities.Eatable;

/**
 * Represents an Apple that can be picked up and eaten.
 * <p>
 * When eaten, the apple restores the actor's health and hydration:
 * <ul>
 *   <li>Heals the actor by 3 HP.</li>
 *   <li>Increases {@link GameAttributes#HYDRATION} by 2.</li>
 * </ul>
 * The apple is removed from the actor's inventory once consumed.
 * </p>
 *
 * @author Esaam
 * @version 1.0
 */
public class Apple extends Item implements Eatable {

    /**
     * Constructs an Apple item.
     * The apple is portable and has the {@link Status#FOOD} ability.
     */
    public Apple() {
        super("Apple", 'a', true); // portable = true
        this.enableAbility(Status.FOOD);
    }

    /**
     * Returns the list of actions available for this apple.
     * <p>
     * In addition to the default item actions, this apple can be eaten
     * by the actor holding it.
     * </p>
     *
     * @param actor the actor interacting with the item
     * @param map   the current game map
     * @return a list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor actor, GameMap map) {
        ActionList actions = super.allowableActions(actor, map);
        actions.add(new EatAction(this));
        return actions;
    }

    /**
     * Defines what happens when the apple is eaten by an actor.
     * <p>
     * Effects:
     * <ul>
     *   <li>The apple is removed from the actor’s inventory.</li>
     *   <li>The actor regains 3 HP.</li>
     *   <li>The actor’s hydration is increased by 2.</li>
     * </ul>
     * </p>
     *
     * @param actor the actor eating the apple
     * @return a descriptive message of the effect
     */
    @Override
    public String eatenBy(Actor actor) {
        actor.removeItemFromInventory(this);
        actor.heal(3);
        actor.modifyAttribute(GameAttributes.HYDRATION,
                ActorAttributeOperation.INCREASE, 2);
        return actor + " eats the apple and feels refreshed!";
    }
}
