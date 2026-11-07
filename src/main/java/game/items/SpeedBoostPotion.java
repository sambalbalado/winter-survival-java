package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.potionactions.DrinkSpeedBoostAction;
import game.app.Abilities;

/** Grants a temporary speed boost: sprint 2 tiles per tick while active. */
public class SpeedBoostPotion extends Item {
    private final int durationTurns;

    public SpeedBoostPotion(int durationTurns) {
        super("Speed Boost Potion", '⚡', true); // portable
        this.durationTurns = Math.max(1, durationTurns);
    }

    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        // Only offer if the actor is allowed to consume items (mirrors your Bottle/CAN_DRINK style)
        if (owner.hasAbility(Abilities.CAN_CONSUME) || owner.hasAbility(Abilities.CAN_DRINK)) {
            actions.add(new DrinkSpeedBoostAction(this, durationTurns));
        }
        return actions;
    }
}
