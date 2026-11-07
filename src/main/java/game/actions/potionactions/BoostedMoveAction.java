// src/game/actions/potionactions/BoostedMoveAction.java
package game.actions.potionactions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Moves the performer up to a fixed number of tiles in a straight line within a single turn.
 * <p>
 * This action represents a temporary speed boost (e.g., from a potion effect). Starting from the actor's
 * current location, it repeatedly attempts to step in the given {@code (dx, dy)} direction, up to
 * {@link #steps} times. Movement stops early if the next tile is out of bounds, blocked
 * (i.e., {@link Location#canActorEnter(Actor)} returns false), or already occupied by another actor.
 * </p>
 *
 * <ul>
 *   <li><b>Direction:</b> Human-readable compass label (e.g., "north-east") for menu text.</li>
 *   <li><b>Delta:</b> Grid offsets {@code dx, dy} applied each step from the current position.</li>
 *   <li><b>Step cap:</b> Attempts up to {@code steps} moves; clamped to at least 1.</li>
 *   <li><b>Early stop:</b> Aborts when encountering boundaries, impassable terrain, or another actor.</li>
 * </ul>
 *
 * <p>
 * The action produces a concise log describing the movement direction. It does not guarantee that all
 * {@link #steps} are realized—environment constraints may reduce actual displacement.
 * </p>
 *
 * @author Taiyo Arima
 * @version 1.0
 * @see edu.monash.fit2099.engine.positions.GameMap
 * @see edu.monash.fit2099.engine.positions.Location
 */
public class BoostedMoveAction extends Action {

    /**
     * Human-readable direction label for menu text (e.g., "north").
     */
    private final String direction;

    /**
     * X-axis increment applied per step (positive = east, negative = west).
     */
    private final int dx;

    /**
     * Y-axis increment applied per step (positive = south, negative = north).
     */
    private final int dy;

    /**
     * Maximum number of tiles to traverse in the given direction this turn.
     * <p>
     * Guaranteed to be at least {@code 1}; the constructor clamps non-positive inputs.
     * </p>
     */
    private final int steps; // usually 2

    /**
     * Constructs a boosted move action that attempts to move up to {@code steps} tiles in a straight line.
     *
     * @param direction human-readable direction name for UI (e.g., "east")
     * @param dx        per-step delta on the X axis
     * @param dy        per-step delta on the Y axis
     * @param steps     maximum number of steps to attempt; clamped to {@code >= 1}
     */
    public BoostedMoveAction(String direction, int dx, int dy, int steps) {
        this.direction = direction;
        this.dx = dx;
        this.dy = dy;
        this.steps = Math.max(1, steps);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Renders this action as a movement with an explicit speed multiplier indicator.
     * </p>
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " moves " + direction + " (Speed ×" + steps + ")";
    }

    /**
     * Executes the boosted movement.
     * <p>
     * Starting from the actor's current tile, attempts up to {@link #steps} moves in the configured
     * {@code (dx, dy)} direction. Each attempt validates map bounds, terrain passability, and absence
     * of another actor on the destination tile; failure on any check aborts further movement.
     * </p>
     *
     * @param actor the performer to move
     * @param map   the active map providing bounds and passability rules
     * @return a short message describing the attempted movement direction
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Location here = map.locationOf(actor);
        for (int i = 0; i < steps; i++) {
            int nx = here.x() + dx;
            int ny = here.y() + dy;

            // Stop if out of bounds
            if (!map.getXRange().contains(nx) || !map.getYRange().contains(ny)) break;

            Location next = map.at(nx, ny);

            // Stop if blocked or occupied
            if (!next.canActorEnter(actor) || map.isAnActorAt(next)) break;

            map.moveActor(actor, next);
            here = next; // continue from the new position
        }
        return actor + " moves " + direction;
    }
}
