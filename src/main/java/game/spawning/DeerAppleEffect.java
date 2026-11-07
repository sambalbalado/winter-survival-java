// file: game/spawning/effects/DeerAppleEffect.java
package game.spawning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Apple;

/**
 * Post-spawn effect for deer: drops exactly one {@link Apple}
 * on a random adjacent tile (if available).
 *
 * <p>Shuffles neighbouring exits and places the apple on the first
 * reachable destination, then stops.</p>
 *
 * @author Sai Sann Lone
 * @version 1.0
 */
public final class DeerAppleEffect implements PostSpawnEffect {
    /**
     * Drops one apple on a random neighbouring tile.
     *
     * @param spawnerLocation the tile where the deer spawned
     * @param spawnedActor    the spawned deer (unused)
     * @param rng             randomness source
     */
    @Override
    public void apply(Location spawnerLocation, Actor spawnedActor, Random rng) {
        List<Exit> exits = new ArrayList<>(spawnerLocation.getExits());
        if (exits.isEmpty()) return;

        Collections.shuffle(exits, rng);

        for (Exit e : exits) {
            Location dest = e.getDestination();
            try {
                dest.addItem(new Apple());
                System.out.println("[SpawnEffect] Deer spawned -> Apple dropped at " + dest);
                break; // only ONE apple
            } catch (Exception ignored) { /* try next exit */ }
        }
    }
}
