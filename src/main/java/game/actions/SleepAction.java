package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Sleepable;

/**
 * An action that allows an actor to sleep on a {@link Sleepable} source.
 * The action executes the sleeping behavior of the source and may be repeated if
 * the actor can continue sleeping.
 *
 * @author Esaam Matudin
 * @version 5.1
 */
public class SleepAction extends Action {

    /** The sleepable source. */
    private final Sleepable source;

    /**
     * Constructor.
     *
     * @param source the sleepable source
     */
    public SleepAction(Sleepable source) {
        this.source = source;
    }

    /**
     * Executes the sleeping action by the given actor.
     *
     * @param actor the actor performing the action
     * @param map   the current game map
     * @return a string describing the result of sleeping
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return source.sleptBy(actor);
    }

    /**
     * Returns a description of this action for the menu.
     *
     * @param actor the actor performing the action
     * @return a string describing the sleeping action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " sleeps on " + source.getClass().getSimpleName();
    }

    /**
     * Returns the next action if the actor can continue sleeping.
     *
     * @return this action if sleeping can continue, otherwise null
     */
    @Override
    public Action getNextAction() {
        return source.canContinueSleep() ? this : null;
    }
}
