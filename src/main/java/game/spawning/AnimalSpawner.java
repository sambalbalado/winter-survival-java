// AnimalSpawner.java
package game.spawning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.api.LunarPhaseManager;
import game.api.MoonPhase;
import game.spawning.AnimalRegistry.AnimalType;

/**
 * Handles the logic for spawning animals on a given ground type.
 * <p>
 * Each {@link AnimalSpawner} has its own {@link SpawnerConfiguration} and
 * {@link SpawnTiming}, allowing different maps or grounds to use custom rules.
 * The spawner checks conditions each turn and adds a new animal to the map
 * when the timing and configuration requirements are met.
 * <p>
 * All spawners share a single {@link SpawnEffects} instance to ensure consistent
 * post-spawn behavior (apple drops, yewberry scattering, tree growth, poisoning).
 *
 * @author Sai Sann Lone
 * @version 2.0
 */
public class AnimalSpawner {
    private final SpawnerConfiguration configuration;
    private final SpawnTiming timing;
    private final Random random;

    /**
     * Shared spawn effects instance used by all spawners.
     * Effects are registered once and reused across all spawn events.
     */
    private static final SpawnEffects SPAWN_EFFECTS = new SpawnEffects();

    /**
     * Creates a new spawner with the given configuration and timing.
     *
     * @param configuration the set of spawn options and rules
     * @param timing        the timing rule for when spawns occur
     */
    public AnimalSpawner(SpawnerConfiguration configuration, SpawnTiming timing) {
        this(configuration, timing, new Random());
    }

    /**
     * Creates a new spawner with dependency injection for testing.
     *
     * @param configuration the set of spawn options and rules
     * @param timing        the timing rule for when spawns occur
     * @param random        the random number generator for weighted selection
     */
    public AnimalSpawner(SpawnerConfiguration configuration,
                         SpawnTiming timing,
                         Random random) {
        this.configuration = configuration;
        this.timing = timing;
        this.random = random;
    }

    /**
     * Attempts to spawn on ONE adjacent tile if (and only if) there is an actor
     * in the 8-neighbourhood. The actual spawn chance is governed by this
     * spawner's SpawnTiming (e.g., RandomChance(0.5) for swamps).
     * <p>
     * This method is used by proximity-based spawners like Swamp.
     *
     * @param location  the spawner's location
     * @param turnCount the current turn number
     */
    public void processSpawningAdjacentIfActorNearby(Location location, int turnCount) {
        // Do not spawn on top of an existing actor at the spawner tile
        if (location.map().isAnActorAt(location)) {
            return;
        }

        // Collect neighbouring exits in random order
        List<Exit> exits = new ArrayList<>(location.getExits());
        if (exits.isEmpty()) return;
        Collections.shuffle(exits, random);

        // Proximity gate: only proceed if ANY neighbour currently has an actor
        boolean someoneNearby = false;
        for (Exit e : exits) {
            if (location.map().isAnActorAt(e.getDestination())) {
                someoneNearby = true;
                break;
            }
        }
        if (!someoneNearby) return;

        // Pick the first adjacent location that is enterable and vacant
        Location spawnAt = null;
        for (Exit e : exits) {
            Location adj = e.getDestination();
            if (adj.getGround().canActorEnter(null) && !location.map().isAnActorAt(adj)) {
                spawnAt = adj;
                break;
            }
        }
        if (spawnAt == null) return;

        // Delegate to the standard spawning flow
        processSpawning(spawnAt, turnCount);
    }

    /**
     * Attempts to spawn an animal at the given location for the current turn.
     * <p>
     * The method checks:
     * <ul>
     *   <li>If spawning should occur based on timing,</li>
     *   <li>If the tile is empty,</li>
     *   <li>If the map supports spawning for this configuration,</li>
     *   <li>Then performs weighted selection and adds the animal.</li>
     * </ul>
     * After successful spawning, triggers post-spawn effects (apple drops,
     * yewberry scattering, tree growth, poisoning) via the shared
     * {@link SpawnEffects} instance.
     *
     * @param location  the location to spawn at
     * @param turnCount the current turn number
     */
    public void processSpawning(Location location, int turnCount) {

        if (!timing.shouldAttemptSpawn(turnCount)) return;
        if (location.map().isAnActorAt(location)) return;
        String mapName = location.map().toString();
        List<SpawnerConfiguration.SpawnOption> options = configuration.getOptionsFor(mapName);

        if (options.isEmpty()) return;

        SpawnerConfiguration.SpawnOption selected = selectWeightedOption(options);
        if (selected == null) return;
        AnimalType animalType = selected.getAnimalType();
        Actor animal = AnimalRegistry.createAnimal(animalType);
        if (animal == null) return;
        if (!location.map().isAnActorAt(location)) {
            selected.getModification().applyTo(animal);
            try {
                location.map().addActor(animal, location);
                System.out.println("[" + configuration.getSpawnerName() + "] " +
                        animal + " spawned at " + location + " on " + mapName + " " +
                        selected.getModification().getDisplayName());
                SPAWN_EFFECTS.apply(animalType, location, animal);
            } catch (Exception e) {
                // Silent failure
            }
        }
    }


    /**
     * Selects a spawn option using weighted random probability.
     * <p>
     * Each option has a weight; higher weights have higher spawn probability.
     * Uses standard weighted random selection algorithm.
     *
     * @param options the list of possible spawn options
     * @return the chosen {@link SpawnerConfiguration.SpawnOption}, or null if none valid
     */
    private SpawnerConfiguration.SpawnOption selectWeightedOption(List<SpawnerConfiguration.SpawnOption> options) {
        // Calculate total weight
        double totalWeight = options.stream()
                .mapToDouble(SpawnerConfiguration.SpawnOption::getWeight)
                .sum();

        if (totalWeight <= 0) {
            return null;
        }

        // Roll random value in [0, totalWeight)
        double roll = random.nextDouble() * totalWeight;
        double accumulator = 0;

        // Find which option the roll lands in
        for (SpawnerConfiguration.SpawnOption option : options) {
            accumulator += option.getWeight();
            if (roll <= accumulator) {
                return option;
            }
        }

        // Fallback (should never reach here due to floating point precision)
        return options.get(options.size() - 1);
    }

    private SpawnerConfiguration.SpawnOption selectWeightedOption(List<SpawnerConfiguration.SpawnOption> options, double wolfMultiplier) {
        double totalWeight = 0;
        for (SpawnerConfiguration.SpawnOption option : options) {
            double adjustedWeight = option.getAnimalType() == AnimalRegistry.AnimalType.WOLF ?
                    option.getWeight() * wolfMultiplier : option.getWeight();
            totalWeight += adjustedWeight;
        }
        if (totalWeight <= 0) return null;

        double roll = random.nextDouble() * totalWeight;
        double accumulator = 0;
        for (SpawnerConfiguration.SpawnOption option : options) {
            double adjustedWeight = option.getAnimalType() == AnimalRegistry.AnimalType.WOLF ?
                    option.getWeight() * wolfMultiplier : option.getWeight();
            accumulator += adjustedWeight;
            if (roll <= accumulator) return option;
        }
        return options.get(options.size() - 1); // Fallback
    }

}
