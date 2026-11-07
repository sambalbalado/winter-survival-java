package game.spawning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.flora.ReactiveYewBerryTree;
/**
 * Post-spawn effect for wolves: grows exactly one {@link ReactiveYewBerryTree}
 * on a random adjacent tile (if available).
 *
 * <p>Shuffles neighbouring exits and sets the ground on the first reachable
 * destination to a reactive yew berry tree, then stops.</p>
 *
 * @author Sai Sann Lone
 * @version 1.0
 */
public final class WolfTreeGrowthEffect implements PostSpawnEffect {
    /**
     * Plants one reactive yew berry tree on a random neighbouring tile.
     *
     * @param spawnerLocation the tile where the wolf spawned
     * @param spawnedActor    the spawned wolf (unused)
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
                dest.setGround(new ReactiveYewBerryTree());
                // Messages.log("[SpawnEffect] Wolf -> Reactive Tree at " + dest);
                break; // only one tree
            } catch (Exception ignored) { /* try next exit */ }
        }
    }
}
