package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TeleportAction;
import game.teleport.TeleportUtils;
import game.teleport.Teleporter;
import game.teleport.TeleportDestination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Teleporter} item that enables the player or any {@link Actor} to teleport across maps.
 * <p>
 * The {@code TeleportCube}:
 * <ul>
 *     <li>Allows teleportation to predefined {@link TeleportDestination}s</li>
 *     <li>May randomly malfunction, sending the user to a random safe location</li>
 *     <li>Provides teleport actions only when carried by an actor</li>
 * </ul>
 * </p>
 * <p>
 * When a malfunction occurs, the teleportation target is replaced with a random
 * valid location within the current map, determined by {@link TeleportUtils#randomFreeLocation(GameMap, Actor)}.
 * </p>
 *
 * @author Taiyo
 * @version 1.0
 */
public class TeleportCube extends Item implements Teleporter {

    /**
     * The list of all teleport destinations that can be accessed using this cube.
     */
    private final List<TeleportDestination> dests = new ArrayList<>();

    /**
     * Random number generator used to determine teleport malfunctions.
     */
    private final java.util.Random rng = new java.util.Random();

    /**
     * Tracks whether the last teleport attempt resulted in a malfunction.
     */
    private boolean lastMalfunction = false;

    /**
     * Creates a new {@code TeleportCube}.
     * <p>
     * By default, the cube:
     * <ul>
     *     <li>Is portable</li>
     *     <li>Displays as the character '□'</li>
     *     <li>Has the name "Teleport cube"</li>
     * </ul>
     * </p>
     */
    public TeleportCube() {
        super("Teleport cube", '□', true);
    }

    /**
     * Adds a teleport destination to this cube.
     *
     * @param map   the {@link GameMap} representing the destination
     * @param x     the x-coordinate of the destination
     * @param y     the y-coordinate of the destination
     * @param label a human-readable label for the destination
     * @return this cube instance (for method chaining)
     */
    public TeleportCube addDestination(GameMap map, int x, int y, String label) {
        dests.add(new TeleportDestination(map, x, y, label));
        return this;
    }

    /**
     * Returns all allowable actions for this cube when carried by an {@link Actor}.
     * <p>
     * For each stored destination, a {@link TeleportAction} is generated and added
     * to the list of available actions.
     * </p>
     *
     * @param owner the actor carrying this cube
     * @param map   the map where the owner currently resides
     * @return a list of allowable {@link TeleportAction}s
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList list = new ActionList();
        if (owner == null || dests.isEmpty()) return list;
        for (var d : dests) {
            list.add(new TeleportAction(this, d, map.locationOf(owner), /*fromCube=*/true));
        }
        return list;
    }

    /**
     * Determines the final teleport destination based on whether a malfunction occurs.
     * <p>
     * If a malfunction occurs, the teleport target becomes a random safe
     * location on the current map. Otherwise, it returns the specified
     * {@link TeleportDestination}.
     * </p>
     *
     * @param actor      the actor using the cube
     * @param currentMap the current map where the teleport originates
     * @param sourceLoc  the cube’s current location
     * @param intended   the intended teleport destination
     * @return the resolved teleport {@link Location}
     */
    @Override
    public Location resolveDestination(Actor actor, GameMap currentMap,
                                       Location sourceLoc, TeleportDestination intended) {
        lastMalfunction = rng.nextBoolean();
        if (lastMalfunction) {
            return TeleportUtils.randomFreeLocation(currentMap, actor);
        }

        GameMap target = intended.map();
        Location intendedLoc = target.at(intended.x(), intended.y());
        return TeleportUtils.findSafeLanding(target, actor, intendedLoc);
    }

    /**
     * Generates a post-teleport message after the teleportation process completes.
     * <p>
     * If the teleport malfunctioned, this method returns a message indicating
     * that the user warped to a random location. Otherwise, it returns {@code null}.
     * </p>
     *
     * @param actor     the actor performing the teleport
     * @param resolved  the final resolved destination
     * @param intended  the originally intended destination
     * @return a string message if a malfunction occurred, otherwise {@code null}
     */
    @Override
    public String postTeleportMessage(Actor actor, Location resolved, TeleportDestination intended) {
        if (lastMalfunction) {
            return actor + " uses the Teleport cube (malfunction!) and warps to a random location.";
        }
        return null;
    }

    /**
     * Returns an empty {@link ActionList}, as this cube cannot be interacted with while on the ground.
     *
     * @param location the map location of the cube
     * @return an empty action list
     */
    @Override
    public ActionList allowableActions(Location location) {
        return new ActionList();
    }

    /**
     * Returns the display name of this teleporter item.
     *
     * @return the string name of the cube
     */
    @Override
    public String name() {
        return "Teleport cube";
    }

    /**
     * Returns all teleport destinations accessible by this cube.
     *
     * @param here the map where the cube is currently located
     * @return an unmodifiable list of {@link TeleportDestination}s
     */
    @Override
    public List<TeleportDestination> destinations(GameMap here) {
        return Collections.unmodifiableList(dests);
    }
}
