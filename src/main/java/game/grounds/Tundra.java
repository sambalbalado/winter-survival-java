package game.grounds;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.api.LunarPhaseManager;
import game.spawning.*;

/**
 * A tundra ground that occasionally spawns cold-resistant animals.
 * <p>
 * Spawning behaviour:
 * - In the <b>Forest</b>, tundra tiles spawn Bears with cold resistance (+10 HP).
 * - In the <b>Plains</b>, tundra tiles spawn Wolves with cold resistance (+10 HP).
 * <p>
 * Spawning occurs with a 5% chance each turn, using {@link SpawnTiming.RandomChance}.
 * Spawned animals gain the {@code COLD_RESISTANT} ability and bonus health
 * from {@link SpawnModification#withColdResistance(int)}.
 *
 * Each turn, {@link #tick(Location)} increases a counter and calls
 * {@link AnimalSpawner#(Location, int)} to manage spawning.
 *
 * @author Sai Sann Lone
 * @version 1.0
 */
public class Tundra extends Ground {
    private final AnimalSpawner spawner;
    private int turnCount = 0;

    /**
     * Creates a tundra tile with cold-resistant animal spawning.
     */

    public Tundra() {
        super('_', "Tundra");

        SpawnerConfiguration config = new SpawnerConfiguration("TUNDRA");
        SpawnModification coldResistant = SpawnModification.withColdResistance(10);

        // Different animals spawn on different maps
        config.addSpawnOption("Forest", AnimalRegistry.AnimalType.BEAR, 1.0, coldResistant);
        config.addSpawnOption("Plains", AnimalRegistry.AnimalType.WOLF, 1.0, coldResistant);

        config.addSpawnOption("Plains", AnimalRegistry.AnimalType.CROCODILE, 1.0, SpawnModification.none());

        this.spawner = new AnimalSpawner(config, new SpawnTiming.RandomChance(0.05));
    }
    /**
     * Updates the tundra each turn and triggers the spawning system.
     *
     * @param location the current map location of this tundra tile
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        spawner.processSpawning(location, ++turnCount);
    }
}
