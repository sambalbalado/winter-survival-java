package game.items;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.EatAction;
import game.capabilities.Eatable;

/**
 * A poisonous Yew Berry that kills the actor who eats it.
 * <p>
 * When consumed, the Yew Berry immediately reduces the actor's health
 * to zero, resulting in instant death.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class YewBerry extends Item implements Eatable {

    /**
     * Constructor for Yew Berry.
     * Sets the display character and enables FOOD status.
     */
    public YewBerry() {
        super("Yew Berry", 'x', true); // 'x' represents Yew Berry
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
        actor.hurt(actor.getAttribute(BaseAttributes.HEALTH));
        return actor + " eats the Yew Berry and dies instantly!";
    }
}
