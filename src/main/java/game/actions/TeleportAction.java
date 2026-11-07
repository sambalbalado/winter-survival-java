package game.actions;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.teleport.*;

/**
 * An {@link Action} that allows an {@link Actor} to teleport between locations or maps
 * through a {@link Teleporter} source.
 * <p>
 * The {@code TeleportAction}:
 * <ul>
 *     <li>Triggers source-side effects before teleportation</li>
 *     <li>Resolves the final destination (which may differ if a malfunction occurs)</li>
 *     <li>Applies destination-side effects after arrival</li>
 *     <li>Handles cross-map movement safely between maps</li>
 * </ul>
 * </p>
 * <p>
 * This action works for all teleporter types, such as {@link game.items.TeleportCube},
 * {@link game.teleport.TeleportDoor}, and {@link game.teleport.TeleportCircle}, ensuring
 * consistent cross-map travel behaviour.
 * </p>
 *
 * @author Taiyo
 * @version 1.0
 */
public class TeleportAction extends Action {

    /**
     * The source {@link Teleporter} initiating the teleport.
     */
    private final Teleporter source;

    /**
     * The intended teleport destination.
     */
    private final TeleportDestination dest;

    /**
     * The location from which the teleport originates.
     */
    private final Location sourceLoc;

    /**
     * Indicates whether this teleport was triggered by a {@link game.items.TeleportCube}.
     */
    private final boolean fromCube;

    /**
     * Creates a new {@code TeleportAction}.
     * <p>
     * The action stores all context required to perform a teleport, including
     * the source teleporter, destination, and originating location.
     * </p>
     *
     * @param source     the teleporter object initiating the teleport
     * @param dest       the intended teleport destination
     * @param sourceLoc  the location of the teleporter when used
     * @param fromCube   {@code true} if triggered by a TeleportCube, otherwise {@code false}
     */
    public TeleportAction(Teleporter source, TeleportDestination dest, Location sourceLoc, boolean fromCube) {
        this.source = source;
        this.dest = dest;
        this.sourceLoc = sourceLoc;
        this.fromCube = fromCube;
    }

    /**
     * Executes the teleport action for the specified actor.
     * <p>
     * The teleportation process includes:
     * <ul>
     *     <li>Activating source-side effects via {@link Teleporter#onUsedAtSource(Location)}</li>
     *     <li>Resolving the teleport destination using {@link Teleporter#resolveDestination(Actor, GameMap, Location, TeleportDestination)}</li>
     *     <li>Applying arrival effects via {@link Teleporter#onArrivedAtDestination(Location)}</li>
     *     <li>Safely transferring the actor between maps</li>
     * </ul>
     * </p>
     *
     * @param actor       the actor performing the teleport
     * @param currentMap  the current map where the teleport originates
     * @return a message describing the teleport outcome
     */
    @Override
    public String execute(Actor actor, GameMap currentMap) {
        // 1) Let the teleporter do its source effect and choose destination
        source.onUsedAtSource(sourceLoc);
        Location resolved = source.resolveDestination(actor, currentMap, sourceLoc, dest);

        // 2) Destination-side effect (e.g., burning rings for TeleportDoor)
        source.onArrivedAtDestination(resolved);

        // 3) Move
        try {
            move(actor, currentMap, resolved.map(), resolved.x(), resolved.y());
        } catch (GameEngineException e) {
            throw new RuntimeException(e);
        }

        // 4) Message
        String custom = source.postTeleportMessage(actor, resolved, dest);
        if (custom != null) return custom;
        return actor + " teleports to " + resolved.map() + " (" + resolved.x() + "," + resolved.y() + ")";
    }

    /**
     * Safely transfers an {@link Actor} from one map to another.
     * <p>
     * The actor is removed from the current map and added to the destination map
     * at the specified coordinates. This method ensures compatibility with cross-map travel.
     * </p>
     *
     * @param a    the actor being moved
     * @param from the source map
     * @param to   the destination map
     * @param x    the x-coordinate on the destination map
     * @param y    the y-coordinate on the destination map
     * @throws GameEngineException if an error occurs during movement
     */
    private void move(Actor a, GameMap from, GameMap to, int x, int y) throws GameEngineException {
        try {
            from.removeActor(a);
        } catch (Exception ignored) {} // Safe cleanup if actor not found
        to.addActor(a, to.at(x, y));
    }

    /**
     * Returns a brief description of this teleport action for menu display.
     *
     * @param actor the actor performing the action
     * @return a string description of the teleport
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " uses " + source.name() + " → " + dest.label();
    }
}
