package game.flora;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.YewBerry;

/**
 * Yew berry tree variant that drops a berry whenever
 * any adjacent tile has an actor (checks every tick).
 * <p>Ignores the normal 5–turn drop interval of {@link YewBerryTree}.</p>
 * @author Sai Sann Lone
 * @version 2.0
 */
public class ReactiveYewBerryTree extends YewBerryTree {

    /** Creates a reactive yew berry tree. */
    public ReactiveYewBerryTree() {
        super();
    }
    /**
     * Ticks the tree and drops exactly one {@link YewBerry} on this tile
     * if any neighbouring location contains an actor.
     *
     * @param location the tree's location
     */
    @Override
    public void tick(Location location) {
        // let the base tree tick first if it does anything structural (aging, graphics, etc)
        super.tick(location);

        // Check surroundings for any actor
        boolean actorNearby = location.getExits().stream()
                .anyMatch(exit -> location.map().isAnActorAt(exit.getDestination()));

        if (actorNearby) {
            // Drop exactly one YewBerry on this tree's tile
            try {
                location.addItem(new YewBerry());
                System.out.println("[ReactiveYewBerryTree] Dropped YewBerry at " + location);
            } catch (Exception ignored) {
                // fail silently to match engine style
            }
        }
    }
}
