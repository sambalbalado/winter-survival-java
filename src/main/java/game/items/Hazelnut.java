package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.app.Abilities;

/**
 * A consumable hazelnut that permanently increases maximum health.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class Hazelnut extends Item implements Consumable {

    /**
     * Constructs a Hazelnut.
     */
    public Hazelnut() {
        super("Hazelnut", 'n', true);
        this.enableAbility(Abilities.EAT_FRUIT);


    }

    /**
     * Consumes the hazelnut, permanently increasing the actor's maximum health by 1.
     *
     * @param actor the actor consuming the hazelnut
     */
    @Override
    public void consume(Actor actor) {
        actor.modifyStatsMaximum(BaseAttributes.HEALTH, ActorAttributeOperation.INCREASE, 1);
    }

    /**
     * Returns available actions for this hazelnut.
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
