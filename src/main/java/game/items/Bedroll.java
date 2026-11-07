package game.items;

import game.actions.SleepAction;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;

/**
 * A bedroll item that allows sleeping.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class Bedroll extends Item {
    private final Sleepable sleepable;

    /**
     * Constructs a Bedroll for a specific sleepable actor.
     *
     * @param sleepable the actor that can use this bedroll
     */
    public Bedroll(Sleepable sleepable) {
        super("Bedroll", '=', true);
        this.sleepable = sleepable;
    }

    /**
     * Returns available actions for this bedroll.
     *
     * @param otherActor the actor performing the action
     * @param location the location of the bedroll
     * @return list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor otherActor, Location location) {
        ActionList actions = new ActionList();
        if (otherActor == sleepable && location.getActor() == otherActor) {
            actions.add(new SleepAction(sleepable));
        }
        return actions;
    }
}
