package game.teleport;

import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.actors.Actor;
import game.environment.Fire;

import static game.status.BurnSource.FireTile;

/**
 * A utility class providing common teleportation-related helper methods.
 * <p>
 * The {@code TeleportUtils} class supports:
 * <ul>
 *     <li>Spawning flames around or near teleporters for visual or environmental effects</li>
 *     <li>Determining random or safe landing locations for teleportation</li>
 *     <li>Checking whether a map location is free and enterable for a given actor</li>
 * </ul>
 * </p>
 * <p>
 * All methods in this class are static and can be used by any {@link Teleporter}
 * implementation, including {@link game.items.TeleportCube},
 * {@link game.teleport.TeleportDoor}, and {@link game.teleport.TeleportCircle}.
 * </p>
 *
 * @author Taiyo
 * @version 1.0
 */
public final class TeleportUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private TeleportUtils() { }

    /**
     * Spawns flames in all adjacent tiles around the specified location.
     * <p>
     * Each surrounding tile is replaced with a {@link Fire}, simulating the aftermath
     * of a powerful teleportation event.
     * </p>
     *
     * @param center the central {@link Location} around which flames should spawn
     */
    public static void burnAround(Location center) {
        for (var e : center.getExits()) {
            e.getDestination().setGround(new Fire(FireTile,3));
        }
    }

    /**
     * Spawns a flame on one randomly chosen adjacent tile.
     * <p>
     * If the location has no exits, this method does nothing.
     * </p>
     *
     * @param center the central {@link Location} used as the reference point
     */
    public static void burnOneAdjacent(Location center) {
        var exits = center.getExits();
        if (exits.isEmpty()) return;

        var pick = exits.get(new java.util.Random().nextInt(exits.size())).getDestination();
        pick.setGround(new Fire(FireTile,3));
    }

    /**
     * Selects a random location within the given {@link GameMap}.
     * <p>
     * The returned location may not necessarily be empty or safe for an actor.
     * </p>
     *
     * @param map the map to search within
     * @return a random {@link Location} on the map
     */
    public static Location randomLocation(GameMap map) {
        java.util.Random rand = new java.util.Random();
        int x = rand.nextInt(map.getXRange().max() + 1);
        int y = rand.nextInt(map.getYRange().max() + 1);
        return map.at(x, y);
    }

    /**
     * Finds a random free location on the specified map where the actor can safely spawn.
     * <p>
     * This method attempts up to 100 random locations before scanning the entire map.
     * If no suitable location is found, it returns the map’s top-left corner as a fallback.
     * </p>
     *
     * @param map   the map to search within
     * @param actor the actor attempting to teleport
     * @return a free and enterable {@link Location}, or a fallback if none found
     */
    public static Location randomFreeLocation(GameMap map, Actor actor) {
        java.util.Random rand = new java.util.Random();

        // Try some random shots first
        for (int tries = 0; tries < 100; tries++) {
            Location loc = randomLocation(map);
            if (isFreeFor(loc, actor)) return loc;
        }

        // Fallback: scan the whole map (first fit)
        for (int x = map.getXRange().min(); x <= map.getXRange().max(); x++) {
            for (int y = map.getYRange().min(); y <= map.getYRange().max(); y++) {
                Location loc = map.at(x, y);
                if (isFreeFor(loc, actor)) return loc;
            }
        }

        // Absolute fallback: return some location (may be occupied)
        return map.at(map.getXRange().min(), map.getYRange().min());
    }

    /**
     * Determines a safe landing location for teleportation.
     * <p>
     * The preferred destination is used if possible; otherwise, this method checks
     * neighboring tiles, and finally falls back to a random free location.
     * </p>
     *
     * @param targetMap the map to land in
     * @param actor     the actor being teleported
     * @param preferred the preferred destination location
     * @return a safe and enterable {@link Location} for landing
     */
    public static Location findSafeLanding(GameMap targetMap, Actor actor, Location preferred) {
        if (preferred != null && isFreeFor(preferred, actor)) return preferred;

        if (preferred != null) {
            for (var ex : preferred.getExits()) {
                Location n = ex.getDestination();
                if (isFreeFor(n, actor)) return n;
            }
        }
        return randomFreeLocation(targetMap, actor);
    }

    /**
     * Checks whether a given location is both unoccupied and enterable for the actor.
     *
     * @param loc   the location to check
     * @param actor the actor attempting to enter
     * @return {@code true} if the location is free and enterable, otherwise {@code false}
     */
    public static boolean isFreeFor(Location loc, Actor actor) {
        return !loc.containsAnActor() && loc.canActorEnter(actor);
    }
}
