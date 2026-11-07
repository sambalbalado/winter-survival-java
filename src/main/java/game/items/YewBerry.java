package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.app.Abilities;

/**
 * A dangerous consumable berry that causes fatal damage when consumed.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class YewBerry extends Item implements Consumable {

    /**
     * Constructs a YewBerry.
     */
    public YewBerry() {
        super("Yew Berry", 'x', true);
        this.enableAbility(Abilities.EAT_FRUIT);
        this.enableAbility(Abilities.CAN_COAT_WEAPONS);
    }

    /**
     * Consumes the yew berry, dealing damage equal to the actor's current health.
     *
     * @param actor the actor consuming the yew berry
     */
    @Override
    public void consume(Actor actor) {
        actor.hurt(actor.getAttribute(BaseAttributes.HEALTH));
    }

    /**
     * Returns available actions for this yew berry.
     *
     * @param actor the actor performing the action
     * @param gameMap the game map
     * @return list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor actor, GameMap gameMap) {
        ActionList actions = super.allowableActions(actor, gameMap);
        if (actor.hasAbility(Abilities.CAN_CONSUME)) {
            actions.add(new ConsumeAction(this, this));
        }
        return actions;
    }
}
