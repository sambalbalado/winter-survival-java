package game.teleport;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.TeleportAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Ground} type that functions as a {@link Teleporter} in the form of a door.
 * <p>
 * The {@code TeleportDoor}:
 * <ul>
 *     <li>Allows teleportation to predefined {@link TeleportDestination}s</li>
 *     <li>Triggers a burning effect around the destination upon arrival</li>
 *     <li>Provides teleport actions to actors standing adjacent to or on it</li>
 * </ul>
 * </p>
 * <p>
 * Unlike the {@link game.items.TeleportCube}, the TeleportDoor is stationary and always
 * burns nearby tiles upon teleportation arrival using {@link TeleportUtils#burnAround(Location)}.
 * </p>
 *
 * @author Taiyo
 * @version 1.0
 */
public class TeleportDoor extends Ground implements Teleporter {

    /**
     * The list of teleport destinations accessible through this door.
     */
    private final List<TeleportDestination> dests = new ArrayList<>();

    /**
     * Creates a new {@code TeleportDoor}.
     * <p>
     * By default, a teleport door:
     * <ul>
     *     <li>Is displayed using the character '#'</li>
     *     <li>Has the name "Door"</li>
     *     <li>Acts as a fixed teleporter on the ground</li>
     * </ul>
     * </p>
     */
    public TeleportDoor() {
        super('#', "Door");
    }

    /**
     * Adds a teleport destination that this door can connect to.
     *
     * @param map   the target {@link GameMap}
     * @param x     the x-coordinate of the destination
     * @param y     the y-coordinate of the destination
     * @param label a human-readable label describing the destination
     * @return this door instance (for method chaining)
     */
    public TeleportDoor addDestination(GameMap map, int x, int y, String label) {
        dests.add(new TeleportDestination(map, x, y, label));
        return this;
    }

    /**
     * Executes effects when an actor arrives at the teleportation destination.
     * <p>
     * When a teleport completes, this door burns all surrounding tiles using
     * {@link TeleportUtils#burnAround(Location)} to simulate magical or destructive energy.
     * </p>
     *
     * @param destLoc the destination location where the teleport concludes
     */
    @Override
    public void onArrivedAtDestination(Location destLoc) {
        TeleportUtils.burnAround(destLoc);
    }

    /**
     * Returns the display name of this teleporter.
     *
     * @return the string name of this door
     */
    @Override
    public String name() {
        return "Tele-door";
    }

    /**
     * Returns an immutable list of all teleport destinations accessible through this door.
     *
     * @param here the map where this door is currently located
     * @return an unmodifiable list of {@link TeleportDestination}s
     */
    @Override
    public List<TeleportDestination> destinations(GameMap here) {
        return Collections.unmodifiableList(dests);
    }

    /**
     * Returns all allowable actions for an {@link Actor} interacting with this teleport door.
     * <p>
     * One {@link TeleportAction} is created for each available {@link TeleportDestination}.
     * </p>
     *
     * @param actor      the actor interacting with the door
     * @param here       the location of the door
     * @param direction  the direction of the actor relative to this door
     * @return a list of {@link TeleportAction}s representing possible teleportations
     */
    @Override
    public ActionList allowableActions(Actor actor, Location here, String direction) {
        ActionList list = new ActionList();
        for (var d : dests) {
            list.add(new TeleportAction(this, d, here, false));
        }
        return list;
    }
}
