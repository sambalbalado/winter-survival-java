package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.app.Abilities;
import game.characters.Player;

/**
 * A consumable apple that provides hydration and healing.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class Apple extends Item implements Consumable {

    /**
     * Constructs an Apple with eating and consuming abilities.
     */
    public Apple() {
        super("Apple", 'a', true);
        this.enableAbility(Abilities.EAT_FRUIT);
    }

    /**
     * Consumes the apple, increasing hydration and healing the actor.
     *
     * @param actor the actor consuming the apple
     */
    @Override
    public void consume(Actor actor) {
        if (actor.hasStatistic(Player.ExplorerAttributes.HYDRATION)) {
            actor.modifyAttribute(Player.ExplorerAttributes.HYDRATION, ActorAttributeOperation.INCREASE, 2);
        }

        actor.heal(3);
    }

    /**
     * Returns available actions for this apple.
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
