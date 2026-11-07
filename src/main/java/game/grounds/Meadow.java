package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.api.LunarPhaseManager;
import game.spawning.*;

/**
 * A meadow ground that periodically spawns animals able to consume items on the ground.
 * <p>
 * Spawning behaviour:
 * - In the <b>Forest</b>, meadows spawn Deer.
 * - In the <b>Plains</b>, meadows spawn Deer and Bears.
 * <p>
 * Spawning occurs every 7 turns with a 50% chance.
 * All spawned animals are given ground-consumption abilities through
 * {@link SpawnModification#withGroundConsumption()}.
 *
 * Each turn, {@link #tick(Location)} increases a local counter and triggers
 * {@link AnimalSpawner#(Location, int)}.
 *
 * @author Sai Sann Lone
 * @version 1.0
 */
public class Meadow extends Ground {
    private final AnimalSpawner spawner;
    private int turnCount = 0;


    /**
     * Creates a new Meadow tile with its spawn configuration.
     */

    public Meadow() {
        super('w', "Meadow");

        SpawnerConfiguration config = new SpawnerConfiguration("MEADOW");
        SpawnModification groundConsumer = SpawnModification.withGroundConsumption();

        // Forest: deer only
        config.addSpawnOption("Forest", AnimalRegistry.AnimalType.DEER, 1.0, groundConsumer);

        config.addSpawnOption("Forest", AnimalRegistry.AnimalType.CROCODILE, 1.0, SpawnModification.none());
        // Plains: deer and bears
        config.addSpawnOption("Plains", AnimalRegistry.AnimalType.DEER, 1.0, groundConsumer);
        config.addSpawnOption("Plains", AnimalRegistry.AnimalType.BEAR, 1.0, groundConsumer);

        this.spawner = new AnimalSpawner(config, new SpawnTiming.IntervalWithProbability(7, 0.5));
    }

    /**
     * Updates the meadow each turn and triggers spawning checks.
     *
     * @param location the current map location of this meadow
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        spawner.processSpawning(location, ++turnCount);
    }
}
