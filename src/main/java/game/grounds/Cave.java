package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.api.LunarPhaseManager;
import game.spawning.*;

/**
 * A cave ground that uses an {@link AnimalSpawner} to generate animals at fixed intervals.
 * <p>
 * Spawning behaviour:
 * - In the <b>Forest</b>, caves can spawn Bears, Wolves, and Deer.
 * - In the <b>Plains</b>, caves only spawn Bears and Wolves.
 * <p>
 * The spawner runs every 5 turns, using {@link SpawnTiming.FixedInterval}.
 * No additional spawn modifications are applied.
 *
 * Each turn, {@link #tick(Location)} increases a local counter and calls
 * {@link AnimalSpawner#(Location, int)} to handle spawning.
 *
 * @author Sai Sann Lone
 * @version 1.0
 */
public class Cave extends Ground {
    private final AnimalSpawner spawner;
    private int turnCount = 0;


    /**
     * Creates a new Cave ground tile with a predefined spawning setup.
     */
    public Cave() {
        super('C', "Cave");

        SpawnerConfiguration config = new SpawnerConfiguration("CAVE");

        // Forest caves spawn all animal types equally with no modifications
        config.addSpawnOption("Forest", AnimalRegistry.AnimalType.BEAR, 1.0, SpawnModification.none());
        config.addSpawnOption("Forest", AnimalRegistry.AnimalType.WOLF, 1.0, SpawnModification.none());
        config.addSpawnOption("Forest", AnimalRegistry.AnimalType.DEER, 1.0, SpawnModification.none());

        // Plains caves only spawn predators
        config.addSpawnOption("Plains", AnimalRegistry.AnimalType.BEAR, 1.0, SpawnModification.none());
        config.addSpawnOption("Plains", AnimalRegistry.AnimalType.WOLF, 1.0, SpawnModification.none());

        this.spawner = new AnimalSpawner(config, new SpawnTiming.FixedInterval(5));
    }

    /**
     * Handles the cave’s per-turn logic.
     * <p>
     * Increments the turn counter and triggers the spawner check.
     *
     * @param location the location of this cave
     */

    @Override
    public void tick(Location location) {
        super.tick(location);
        spawner.processSpawning(location, ++turnCount);
    }
}
