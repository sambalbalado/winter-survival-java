package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.EatAction;
import game.capabilities.Eatable;

/**
 * Represents a Hazelnut that can be consumed by an actor.
 * <p>
 * Eating a Hazelnut increases the actor's maximum health by 1.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class Hazelnut extends Item implements Eatable {

    /**
     * Constructor for Hazelnut.
     * Sets the name, display character, portability, and marks it as food.
     */
    public Hazelnut() {
        super("Hazelnut", 'n', true);  // 'n' is the display character
        this.enableAbility(Status.FOOD);
    }

    @Override
    public ActionList allowableActions(Actor actor, GameMap map) {
        ActionList actions = super.allowableActions(actor, map);
        actions.add(new EatAction(this));
        return actions;
    }

    @Override
    public String eatenBy(Actor actor) {
        actor.removeItemFromInventory(this);
        actor.modifyStatsMaximum(BaseAttributes.HEALTH,
                ActorAttributeOperation.INCREASE, 1); // increase max health by 1
        return actor + " eats the hazelnut and feels stronger!";
    }
}
