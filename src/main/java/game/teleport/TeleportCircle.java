package game.teleport;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.TeleportAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Ground} type that serves as a fixed {@link Teleporter} for map traversal.
 * <p>
 * The {@code TeleportCircle}:
 * <ul>
 *     <li>Allows actors to teleport to predefined {@link TeleportDestination}s</li>
 *     <li>Triggers a burning effect on one adjacent tile when activated</li>
 *     <li>Provides a teleport action to any actor standing on it</li>
 * </ul>
 * </p>
 * <p>
 * Teleport circles are static teleportation nodes that cannot malfunction.
 * They are typically used for stable portals or magic circles connecting major locations.
 * </p>
 *
 * @author Taiyo
 * @version 1.0
 */
public class TeleportCircle extends Ground implements Teleporter {

    /**
     * A list of teleport destinations accessible from this circle.
     */
    private final List<TeleportDestination> dests = new ArrayList<>();

    /**
     * Creates a new {@code TeleportCircle}.
     * <p>
     * By default, a teleport circle:
     * <ul>
     *     <li>Is displayed using the character 'O'</li>
     *     <li>Has the name "Circle"</li>
     *     <li>Acts as a fixed teleporter on the ground</li>
     * </ul>
     * </p>
     */
    public TeleportCircle() {
        super('O', "Circle");
    }

    /**
     * Adds a new teleport destination that this circle can connect to.
     *
     * @param map   the target {@link GameMap}
     * @param x     the x-coordinate of the destination
     * @param y     the y-coordinate of the destination
     * @param label a human-readable label for the destination
     * @return this circle instance (for method chaining)
     */
    public TeleportCircle addDestination(GameMap map, int x, int y, String label) {
        dests.add(new TeleportDestination(map, x, y, label));
        return this;
    }

    /**
     * Returns the display name of this teleporter.
     *
     * @return the string name of this circle
     */
    @Override
    public String name() {
        return "Teleportation circle";
    }

    /**
     * Returns an immutable list of all teleport destinations accessible from this circle.
     *
     * @param here the current map containing this circle
     * @return a list of {@link TeleportDestination}s
     */
    @Override
    public List<TeleportDestination> destinations(GameMap here) {
        return Collections.unmodifiableList(dests);
    }

    /**
     * Executes any source-side effects when the circle is activated.
     * <p>
     * When used, the circle burns one adjacent ground tile via
     * {@link TeleportUtils#burnOneAdjacent(Location)} to create a visual or environmental effect.
     * </p>
     *
     * @param sourceLoc the location where the teleportation originates
     */
    @Override
    public void onUsedAtSource(Location sourceLoc) {
        TeleportUtils.burnOneAdjacent(sourceLoc);
    }

    /**
     * Returns all allowable actions available to an actor standing on this teleport circle.
     * <p>
     * One {@link TeleportAction} is provided for each available {@link TeleportDestination}.
     * </p>
     *
     * @param actor      the actor interacting with this circle
     * @param here       the location of the circle
     * @param direction  the direction of the actor relative to this circle
     * @return a list of {@link TeleportAction}s available to the actor
     */
    @Override
    public ActionList allowableActions(Actor actor, Location here, String direction) {
        ActionList list = new ActionList();
        for (var d : dests) {
            list.add(new TeleportAction(this, d, here, /*fromCube=*/false));
        }
        return list;
    }
}
