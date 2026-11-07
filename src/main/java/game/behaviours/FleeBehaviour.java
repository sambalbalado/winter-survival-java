package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.FleeFromFireAction;
import game.app.Abilities;
import game.status.Ignite;
import game.status.BurnSource;

/**
 * A {@link Behaviour} that makes an {@link Actor} flee when it is burning.
 * <p>
 * This behaviour prioritises moving away from nearby predators (i.e., actors with
 * the {@link Abilities#PREDATOR} ability). The flee direction is determined
 * using a dot-product scoring method to choose the exit that maximises distance
 * from the nearest predator. If no predator is detected, the actor continues in
 * the previously chosen flee direction. If no direction is available, the actor
 * will attempt to move through any valid exit as a fallback.
 * </p>
 *
 * <p>
 * This behaviour is primarily triggered by the {@link Ignite} status.
 * </p>
 *
 * @author Esaam
 * @version 1.0
 */
public class FleeBehaviour implements Behaviour {

    /** Stores the horizontal flee direction vector. */
    private int fleeDx = 0;

    /** Stores the vertical flee direction vector. */
    private int fleeDy = 0;

    /**
     * Generates an action for the actor to flee when burning.
     *
     * <p>
     * The algorithm works as follows:
     * <ol>
     *   <li>If the actor is not burning, reset flee direction and return {@code null}.</li>
     *   <li>Otherwise, search for the nearest predator in adjacent locations.</li>
     *   <li>If a predator is found, calculate the flee vector and select the best exit
     *       using a dot-product scoring system.</li>
     *   <li>If no predator is nearby, continue moving in the previously chosen flee direction.</li>
     *   <li>If no valid move matches the direction, fallback to the first available exit.</li>
     * </ol>
     * </p>
     *
     * @param actor the actor that may flee
     * @param map   the game map containing the actor
     * @return a {@link FleeFromFireAction} if a valid escape is found, otherwise {@code null}
     */
    @Override
    public Action generateAction(Actor actor, GameMap map) {
        Location here = map.locationOf(actor);

        boolean dragonBurn = actor.statusesOf(Ignite.class)
                .stream().anyMatch(b -> b.source() == BurnSource.DRAGON);

        if (!dragonBurn) {
            fleeDx = 0;
            fleeDy = 0;
            return null;
        }



        Location nearestPred = null;
        int closestDistance = Integer.MAX_VALUE;

        // Look for adjacent predators
        for (Exit exit : here.getExits()) {
            Location loc = exit.getDestination();
            Actor nearby = loc.getActor();
            if (nearby != null && nearby.hasAbility(Abilities.PREDATOR)) {
                int dist = distance(here, loc);
                if (dist < closestDistance) {
                    closestDistance = dist;
                    nearestPred = loc;
                }
            }
        }

        Exit bestExit = null;

        if (nearestPred != null) {
            // Calculate flee direction vector
            fleeDx = here.x() - nearestPred.x();
            fleeDy = here.y() - nearestPred.y();

            // Pick the exit aligned with flee vector
            int bestScore = Integer.MIN_VALUE;
            for (Exit exit : here.getExits()) {
                Location dest = exit.getDestination();
                if (!dest.canActorEnter(actor)) continue;

                int moveDx = dest.x() - here.x();
                int moveDy = dest.y() - here.y();

                int score = fleeDx * moveDx + fleeDy * moveDy;
                if (score > bestScore) {
                    bestScore = score;
                    bestExit = exit;
                }
            }
        } else if (fleeDx != 0 || fleeDy != 0) {
            // Continue moving in the previous flee direction
            int bestScore = Integer.MIN_VALUE;
            for (Exit exit : here.getExits()) {
                Location dest = exit.getDestination();
                if (!dest.canActorEnter(actor)) continue;

                int moveDx = dest.x() - here.x();
                int moveDy = dest.y() - here.y();

                int score = fleeDx * moveDx + fleeDy * moveDy;
                if (score > bestScore) {
                    bestScore = score;
                    bestExit = exit;
                }
            }
        }

        // Fallback: pick any valid exit
        if (bestExit == null) {
            for (Exit exit : here.getExits()) {
                if (exit.getDestination().canActorEnter(actor)) {
                    bestExit = exit;
                    break;
                }
            }
        }

        return (bestExit != null)
                ? new FleeFromFireAction(bestExit.getDestination(), bestExit.getName())
                : null;
    }

    /**
     * Calculates the Manhattan distance between two locations.
     *
     * @param a the first location
     * @param b the second location
     * @return the Manhattan distance
     */
    private int distance(Location a, Location b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
