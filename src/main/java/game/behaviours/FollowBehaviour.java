package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.capabilities.Tamable;

/**
 * A behaviour that makes a tamable actor follow its owner.
 * <p>
 * The actor will move one step closer to its owner each turn if possible.
 * </p>
 *
 * @author Riordan D. Alfredo (original supplied follow behaviour)
 * @author Esaam Matudin (tamable-owner adaptation)
 * @version 2.5
 */
public class FollowBehaviour implements Behaviour {

    /** The tamable actor that will follow its owner. */
    private final Tamable animal;

    /**
     * Constructor for FollowBehaviour.
     *
     * @param subject the tamable actor that will follow its owner
     */
    public FollowBehaviour(Tamable subject) {
        this.animal = subject;
    }

    /**
     * Generates a move action for the actor to get closer to its owner.
     *
     * @param actor the actor performing the behaviour
     * @param map   the game map containing the actor
     * @return a {@link MoveActorAction} toward the owner, or null if no move is possible
     */
    @Override
    public Action generateAction(Actor actor, GameMap map) {
        Actor owner = animal.getOwner();
        if (owner == null || !map.contains(owner) || !map.contains(actor)) {
            return null;
        }

        Location here = map.locationOf(actor);
        Location there = map.locationOf(owner);

        int currentDistance = distance(here, there);
        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();
            if (destination.canActorEnter(actor)) {
                int newDistance = distance(destination, there);
                if (newDistance < currentDistance) {
                    return new MoveActorAction(destination, exit.getName());
                }
            }
        }

        return null;
    }

    /**
     * Calculates the Manhattan distance between two locations.
     *
     * @param a first location
     * @param b second location
     * @return the Manhattan distance between the two locations
     */
    private int distance(Location a, Location b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
