package game.teleport;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;

/**
 * Defines the contract for any object capable of teleporting actors across locations or maps.
 * <p>
 * A {@code Teleporter} provides:
 * <ul>
 *     <li>Named identification via {@link #name()}</li>
 *     <li>Registered {@link TeleportDestination}s that it connects to</li>
 *     <li>Customisable teleport logic and side effects before and after teleportation</li>
 * </ul>
 * </p>
 * <p>
 * Implementations include:
 * <ul>
 *     <li>{@link game.items.TeleportCube} — portable and prone to malfunction</li>
 *     <li>{@link game.teleport.TeleportDoor} — stationary and burns the destination area</li>
 *     <li>{@link game.teleport.TeleportCircle} — stable magic circle that burns an adjacent tile</li>
 * </ul>
 * </p>
 *
 * @author Taiyo
 * @version 1.0
 */
public interface Teleporter {

    /**
     * Returns the display name of this teleporter.
     *
     * @return the name string of the teleporter
     */
    String name();

    /**
     * Returns a list of {@link TeleportDestination}s accessible from this teleporter.
     *
     * @param here the map where the teleporter currently resides
     * @return a list of available teleport destinations
     */
    List<TeleportDestination> destinations(GameMap here);

    /**
     * Resolves the final destination for teleportation.
     * <p>
     * Implementations may override this method to alter teleport behaviour, such as
     * randomising the destination or handling malfunction effects.
     * </p>
     *
     * @param actor       the actor performing the teleport
     * @param currentMap  the map where the teleport originates
     * @param sourceLoc   the source location of the teleport
     * @param intended    the intended teleport destination
     * @return the resolved {@link Location} where the actor will arrive
     */
    default Location resolveDestination(Actor actor, GameMap currentMap,
                                        Location sourceLoc, TeleportDestination intended) {
        return intended.map().at(intended.x(), intended.y());
    }

    /**
     * Executes any side effects that occur at the teleport source.
     * <p>
     * For example, {@link game.teleport.TeleportCircle} burns a random neighbouring tile
     * when used.
     * </p>
     *
     * @param sourceLoc the location of the teleporter when activated
     */
    default void onUsedAtSource(Location sourceLoc) {}

    /**
     * Executes any side effects that occur at the teleport destination.
     * <p>
     * For example, {@link game.teleport.TeleportDoor} burns the area around the destination.
     * </p>
     *
     * @param destLoc the destination location where the actor arrives
     */
    default void onArrivedAtDestination(Location destLoc) {}

    /**
     * Provides an optional custom message after teleportation.
     * <p>
     * Return {@code null} to use the default message format
     * (“{@code <actor> teleports to <map> (x,y)}”).
     * </p>
     *
     * @param actor     the actor who teleported
     * @param resolved  the actual destination reached
     * @param intended  the intended destination before resolution
     * @return a custom message string, or {@code null} for the default
     */
    default String postTeleportMessage(Actor actor, Location resolved, TeleportDestination intended) {
        return null;
    }
}
