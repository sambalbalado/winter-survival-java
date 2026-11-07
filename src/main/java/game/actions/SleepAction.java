package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.Sleepable;

import java.util.Random;

/**
 * An action that puts a sleepable entity to sleep for a randomized duration.
 *
 * This action handles the sleeping mechanic where a sleepable entity goes to sleep
 * for a random number of turns (between 6 and 10 turns inclusive). The action
 * manages the sleeping state and countdown, automatically waking the entity when
 * the sleep duration is complete. This action continues automatically until the
 * sleep period ends.
 *
 * Based on the SleepAction class from the FIT2099 demo project.
 *
 * @author Adrian Kristanto (original implementation)
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class SleepAction extends Action {

    private final Sleepable sleepable;
    private int turnsLeft;
    private boolean started = false;

    /**
     * Constructor for creating a SleepAction with randomized duration.
     *
     * The sleep duration is randomly determined to be between 6 and 10 turns
     * (inclusive) when the action is created.
     *
     * @param sleepable the Sleepable entity that will go to sleep
     */
    public SleepAction(Sleepable sleepable) {
        this.sleepable = sleepable;
        this.turnsLeft = new Random().nextInt(5) + 6;
    }

    /**
     * Executes the sleep action for one turn.
     *
     * On the first execution, sets the sleepable entity to sleeping state.
     * Each subsequent execution decrements the remaining sleep turns.
     * When no turns remain, the entity is awakened and a wake-up message
     * is returned.
     *
     * @param actor the Actor associated with this action
     * @param map the GameMap where the action takes place
     * @return a String describing the current sleep status or wake-up message
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!started) {
            sleepable.setSleeping(true);
            started = true;
        }

        turnsLeft--;

        if (turnsLeft < 0) {
            sleepable.setSleeping(false);
            return sleepable + " wakes up feeling rested.";
        }

        return sleepable + " is sleeping... (" + turnsLeft + " turns left)";
    }

    /**
     * Returns a description of this action suitable for display in a menu.
     *
     * @param actor the Actor who would perform this action
     * @return a String describing the sleep action with remaining turn count
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Explorer goes to sleep for " + turnsLeft + " turns ";
    }

    /**
     * Returns the next action to be executed automatically.
     *
     * This method allows the sleep action to continue automatically each turn
     * until the sleep duration is complete. Returns this action if there are
     * still turns remaining, or null when the sleep is finished.
     *
     * @return this SleepAction if sleep continues, null if sleep is complete
     */
    @Override
    public Action getNextAction() {
        return (turnsLeft > 0) ? this : null;
    }
}
