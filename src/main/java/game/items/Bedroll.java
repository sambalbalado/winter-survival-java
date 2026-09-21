package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Sleepable;
import game.actions.SleepAction;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A bedroll that actors can sleep on.
 * <p>
 * Sleeping on the bedroll lasts a random number of rounds.
 * During each round, the actor remains asleep until the rounds are exhausted.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class Bedroll extends Item implements Sleepable {
    private static final int MINIMUM_SLEEP_ROUNDS = 6;
    private static final int MAXIMUM_SLEEP_ROUNDS = 10;
    private static final long ROUND_DELAY_MILLIS = 500;

    /** Number of rounds left for sleeping */
    private int roundsLeft;

    /***
     * Constructor for a Bedroll.
     *
     * @param name the name of this Item
     * @param displayChar the character to represent this item on the ground
     * @param portable true if and only if the Item can be picked up
     */
    public Bedroll(String name, char displayChar, boolean portable) {
        super(name, displayChar, portable);
    }

    /**
     * Returns a list of allowable actions that can be performed on this item.
     * Adds the SleepAction if an actor is on this location.
     *
     * @param location the location of the item on the ground
     * @return an unmodifiable list of actions
     */
    @Override
    public ActionList allowableActions(Location location) {
        ActionList actions = super.allowableActions(location);
        if(location.getActor() != null) {
            actions.add(new SleepAction(this));
        }
        return actions;
    }

    /**
     * Performs sleeping for the given actor.
     * Initializes sleep rounds randomly if not already initialized.
     * Decrements the number of rounds left and simulates time passing.
     *
     * @param actor the actor that is sleeping
     * @return a message indicating that the actor is sleeping and rounds left
     */
    @Override
    public String sleptBy(Actor actor) {
        if (roundsLeft <= 0) {
            roundsLeft = ThreadLocalRandom.current().nextInt(
                    MINIMUM_SLEEP_ROUNDS, MAXIMUM_SLEEP_ROUNDS + 1);
        }

        roundsLeft--;

        try {
            Thread.sleep(ROUND_DELAY_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return actor + " is sleeping on " + this.getClass().getSimpleName() + ". (" + roundsLeft + " rounds left)";
    }

    /**
     * Checks if sleeping can continue based on remaining rounds.
     *
     * @return true if there are rounds left, false otherwise
     */
    @Override
    public boolean canContinueSleep() {
        return roundsLeft > 0;
    }
}
