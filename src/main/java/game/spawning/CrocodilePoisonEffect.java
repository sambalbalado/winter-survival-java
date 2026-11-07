package game.spawning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.spawning.SpawnModification;
/**
 * Post-spawn effect for crocodiles that poisons any adjacent actors.
 * <p>Skips the spawned crocodile itself.</p>
 *
 * @author Sai Sann Lone
 * @version 1.0
 */
public final class CrocodilePoisonEffect implements PostSpawnEffect {
    private static final int POISON_TURNS = 3;
    private static final int POISON_DAMAGE = 10;
    /**
     * Poisons every actor in neighbouring tiles (8-neighbourhood),
     * excluding the newly spawned crocodile.
     *
     * @param spawnerLocation the location where the crocodile spawned
     * @param spawnedActor    the spawned crocodile (not poisoned)
     * @param rng             randomness source (unused)
     */
    @Override
    public void apply(Location spawnerLocation, Actor spawnedActor, Random rng) {
        List<Exit> exits = new ArrayList<>(spawnerLocation.getExits());
        if (exits.isEmpty()) return;

        for (Exit e : exits) {
            Location neighbour = e.getDestination();

            if (neighbour.map().isAnActorAt(neighbour)) {
                Actor victim = neighbour.getActor();
                if (victim == spawnedActor) continue; // don’t poison the croc itself

                SpawnModification.applyPoisonToActor(victim, POISON_TURNS, POISON_DAMAGE);
                // Messages.log("[SpawnEffect] Croc -> Poisoned " + victim + " at " + neighbour);
            }
        }
    }
}
