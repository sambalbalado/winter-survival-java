package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.ArrayList;
import java.util.Random;

/**
 * A behaviour that makes an actor wander randomly around the map.
 * <p>
 * The actor will attempt to move to a randomly chosen adjacent location
 * that it can enter. If no locations are available, the actor will stay in place.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.8
 */
public class WanderBehaviour implements Behaviour {

    /** Random number generator for selecting random moves */
    private final Random random = new Random();

    /**
     * Generates a random move action for the given actor.
     *
     * @param actor the actor to generate an action for
     * @param map   the game map the actor is on
     * @return a random move {@link Action} if possible, or null if no moves are available
     */
    @Override
    public Action generateAction(Actor actor, GameMap map) {
        ArrayList<Action> actions = new ArrayList<>();

        // Check all exits from the actor's current location
        for (Exit exit : map.locationOf(actor).getExits()) {
            Location destination = exit.getDestination();
            // Add move action if the actor can enter the destination
            if (destination.canActorEnter(actor)) {
                actions.add(exit.getDestination().getMoveAction(actor, exit.getName(), exit.getHotKey()));
            }
        }

        // Return a random move action or null if no moves available
        if (!actions.isEmpty()) {
            return actions.get(random.nextInt(actions.size()));
        } else {
            return null;
        }
    }
}
