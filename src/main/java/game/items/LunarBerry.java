package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.ConsumeAction;
import game.app.Abilities;

/**
 * A special healing item that only appears during Full Moon phase.
 * Heals 25 HP when consumed, compared to normal berries which heal 10 HP.
 * Spawns on Yew Berry Trees and is removed when the moon phase changes.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class LunarBerry extends Item implements Consumable {

    private final int healAmount = 25;

    /**
     * Creates a Lunar Berry with display character 'L' and portable property.
     */
    public LunarBerry() {
        super("Lunar Berry", 'L', true);
    }

    /**
     * Heals the actor for 25 HP when consumed.
     *
     * @param actor the actor consuming the berry
     */
    @Override
    public void consume(Actor actor) {
        actor.heal(healAmount);
        System.out.println(actor + " eats a Lunar Berry and feels moonlight surge through them.");
    }

    /**
     * Returns allowable actions including consume if actor has CAN_CONSUME ability.
     *
     * @param actor the actor holding the berry
     * @param gameMap the current game map
     * @return list of actions including consume action if permitted
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
