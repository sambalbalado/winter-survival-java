// game/grounds/Swamp.java
package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.spawning.AnimalRegistry;
import game.spawning.AnimalSpawner;
import game.spawning.SpawnModification;
import game.spawning.SpawnTiming;
import game.spawning.SpawnerConfiguration;

/**
 * Swamp ground that can spawn animals when a nearby actor is present.
 * <p>Rules:</p>
 * <ul>
 *   <li>Forest: spawns Crocodile and Deer</li>
 *   <li>Plains: spawns Crocodile only</li>
 *   <li>50% spawn attempt chance when an adjacent actor exists</li>
 *   <li>All spawns are poisoned for 10 turns (5 dmg/turn)</li>
 * </ul>
 * @author Sai Sann Lone
 * @version 1.0
 */
public class Swamp extends Ground {
    private final AnimalSpawner spawner;
    private int turnCount = 0;

    /**
     * Creates a new Swamp tile with its spawn configuration.
     */
    public Swamp() {
        super('~', "Swamp");

        SpawnerConfiguration config = new SpawnerConfiguration("SWAMP");
        SpawnModification swampPoison = SpawnModification.poisoned(10, 5);

        // Forest: crocodile + deer
        config.addSpawnOption("Forest", AnimalRegistry.AnimalType.CROCODILE, 1.0, swampPoison);
        config.addSpawnOption("Forest", AnimalRegistry.AnimalType.DEER,      1.0, swampPoison);

        // Plains: crocodile only
        config.addSpawnOption("Plains", AnimalRegistry.AnimalType.CROCODILE, 1.0, swampPoison);

        // 50% chance per attempt; proximity handled by the adjacent helper
        this.spawner = new AnimalSpawner(config, new SpawnTiming.RandomChance(0.5));
    }

    /**
     * Ticks the tile and triggers a proximity-gated spawn attempt.
     *
     * @param location this swamp's location
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        spawner.processSpawningAdjacentIfActorNearby(location, ++turnCount);
    }
}
