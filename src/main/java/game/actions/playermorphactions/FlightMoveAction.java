package game.actions.playermorphactions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.app.Abilities;

/**
 * Performs a long-distance, straight-line movement available only in {@link Abilities#FORM_DRAGON}.
 * <p>
 * The action scans all adjacent directions (derived from {@link Location#getExits()}) and attempts to
 * travel up to a fixed number of steps in a straight line, succeeding only if every intermediate tile:
 * </p>
 * <ul>
 *   <li>remains within the map bounds,</li>
 *   <li>allows entry for the performer ({@link Location#canActorEnter(Actor)}), and</li>
 *   <li>is unoccupied (no actor present at any step).</li>
 * </ul>
 * <p>
 * On success, the performer is moved to the furthest reachable tile along the chosen direction (up to the
 * configured step count). If no direction yields a valid corridor, the action fails with a message.
 * </p>
 *
 * @version 1.0
 * @see Abilities#FORM_DRAGON
 * @see Exit
 * @see GameMap
 * @see Location
 */
public class FlightMoveAction extends Action {
    /**
     * Maximum number of tiles to advance in a single straight-line flight.
     * <p>
     * Guaranteed to be at least {@code 1}; values passed to the constructor are clamped via {@link Math#max(int, int)}.
     * </p>
     */
    private final int stepCount;

    /**
     * Creates a flight move with a given maximum stride length.
     *
     * @param stepCount desired maximum number of tiles to move; values ≤ 0 are treated as {@code 1}
     */
    public FlightMoveAction(int stepCount) {
        this.stepCount = Math.max(1, stepCount);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Presents this action as a long-flight option in the menu.
     * </p>
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " → Flies (long move)";
    }

    /**
     * Attempts to move the actor in a straight line up to {@link #stepCount} tiles in one of the
     * eight directions provided by the current location's {@link Exit} set.
     * <p>
     * Requirements:
     * </p>
     * <ul>
     *   <li>The performer must have {@link Abilities#FORM_DRAGON}.</li>
     *   <li>All intermediate tiles must be enterable and unoccupied.</li>
     *   <li>Movement must remain within {@link GameMap} bounds.</li>
     * </ul>
     * <p>
     * If a valid path is found, the actor is moved and a descriptive message is returned; otherwise,
     * a failure message is produced.
     * </p>
     *
     * @param actor the performer attempting to fly
     * @param map   the map that provides bounds, occupancy, and movement rules
     * @return a description of the outcome (success with direction/stride or a failure reason)
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasAbility(Abilities.FORM_DRAGON)) {
            return actor + " cannot fly right now.";
        }
        Location here = map.locationOf(actor);

        for (Exit e : here.getExits()) {
            int dx = e.getDestination().x() - here.x();
            int dy = e.getDestination().y() - here.y();
            int nx = here.x(), ny = here.y();
            Location cur = here;
            boolean ok = true;
            for (int i = 0; i < stepCount; i++) {
                nx += dx;
                ny += dy;
                if (!map.getXRange().contains(nx) || !map.getYRange().contains(ny)) { ok = false; break; }
                Location next = map.at(nx, ny);
                if (!next.canActorEnter(actor) || map.isAnActorAt(next)) { ok = false; break; }
                cur = next;
            }
            if (ok && cur != here) {
                map.moveActor(actor, cur);
                return actor + " soars " + stepCount + " tiles " + e.getName() + ".";
            }
        }
        return "No safe flight path.";
    }
}
