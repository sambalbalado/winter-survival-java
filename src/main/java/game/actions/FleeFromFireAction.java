package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.status.Ignite;

/**
 * Represents an action where an {@link Actor} flees to a specific location
 * while affected by burning status.
 * <p>
 * When executed, the actor moves to the destination, takes damage if burning,
 * and outputs a descriptive message about the fleeing action.
 * </p>
 *
 * @author Esaam matudin
 * @version 1.0
 */
public class FleeFromFireAction extends Action {

    /**
     * The location the actor will flee to.
     */
    private final Location destination;

    /**
     * The direction the actor is fleeing towards (e.g., "north", "east").
     */
    private final String direction;

    /**
     * Constructs a {@code FleeFromFireAction} with the given destination and fleeing direction.
     *
     * @param destination the location to which the actor will move
     * @param direction   the direction in which the actor flees
     */
    public FleeFromFireAction(Location destination, String direction) {
        this.destination = destination;
        this.direction = direction;
    }

    /**
     * Executes the flee action by moving the actor to the destination.
     * <p>
     * If the actor has a {@link Ignite} status, the actor takes damage
     * and the action message includes the burn duration. Otherwise,
     * a default fleeing message is returned.
     * </p>
     *
     * @param actor the actor performing the action
     * @param map   the map in which the action occurs
     * @return a descriptive string of the action’s result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        map.moveActor(actor, destination);

        java.util.List<Ignite> burns = new java.util.ArrayList<>(actor.statusesOf(Ignite.class));
        if (!burns.isEmpty()) {
            int nextExpiry = burns.stream()
                    .mapToInt(Ignite::remainingTurns)
                    .min()
                    .orElse(0);

            int stacks = burns.stream().mapToInt(Ignite::stacks).sum();
            int dpt    = burns.stream().mapToInt(Ignite::damagePerTurn).sum();

            return actor + " is burning (" + stacks + " stack"
                    + (stacks == 1 ? "" : "s") + ", " + dpt + " dmg/turn)"
                    + " and runs " + direction + "! (next drop in " + nextExpiry + " turn"
                    + (nextExpiry == 1 ? "" : "s") + ")";
        }

        return actor + " flees " + direction + ".";
    }

    /**
     * Provides the menu description for this action.
     *
     * @param actor the actor performing the action
     * @return a short string suitable for displaying in menus
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " flees " + direction + " while burning!";
    }
}
