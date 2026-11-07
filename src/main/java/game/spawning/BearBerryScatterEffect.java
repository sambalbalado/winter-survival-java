package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.items.YewBerry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Post-spawn effect for bears: attempts to drop {@link YewBerry}s
 * on each adjacent tile with a fixed probability per exit.
 *
 * <p>For every neighbouring {@link Exit}, a random check is performed and,
 * on success, one berry is placed on that destination tile.</p>
 *
 * @author Sai Sann Lone
 * @version 1.0
 */
public final class BearBerryScatterEffect implements PostSpawnEffect {
    private static final double DROP_PROBABILITY = 0.5;

    /**
     * Applies the scatter effect around the spawner location.
     *
     * @param spawnerLocation the tile where the animal was spawned
     * @param spawnedActor    the newly spawned actor (unused here)
     * @param rng             source of randomness
     */

    @Override
    public void apply(Location spawnerLocation, Actor spawnedActor, Random rng) {
        List<Exit> exits = new ArrayList<>(spawnerLocation.getExits());
        if (exits.isEmpty()) return;

        for (Exit e : exits) {
            if (rng.nextDouble() <= DROP_PROBABILITY) {
                Location dest = e.getDestination();
                try {
                    dest.addItem(new YewBerry());
                    System.out.println("[SpawnEffect] Bear spawned -> YewBerry dropped at " + dest);
                } catch (Exception ignored) { /* skip */ }
            }
        }
    }
}
