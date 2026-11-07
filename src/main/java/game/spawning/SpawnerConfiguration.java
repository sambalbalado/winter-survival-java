// SpawnerConfiguration.java
package game.spawning;

import java.util.*;

/**
 * Holds the spawn setup for a specific type of spawner.
 * <p>
 * Each configuration defines what animals can appear on different maps,
 * their relative spawn weights, and any special modifications to apply.
 * This lets the same ground type behave differently between maps.
 *
 * @author Sai Sann Lone
 * @version 1.0
 */
public class SpawnerConfiguration {
    private final String spawnerName;
    private final Map<String, List<SpawnOption>> mapRules = new HashMap<>();

    /**
     * Creates a new configuration with the given name.
     *
     * @param spawnerName identifier for this configuration (e.g. "CAVE" or "MEADOW")
     */

    public SpawnerConfiguration(String spawnerName) {
        this.spawnerName = spawnerName;
    }

    /**
     * Adds a spawn option for a specific map.
     *
     * @param mapName      the name of the map (e.g. "Forest", "Plains")
     * @param animalType   the type of animal to spawn
     * @param weight       the relative weight for random selection
     * @param modification any modifications to apply when spawning
     */
    public void addSpawnOption(String mapName, AnimalRegistry.AnimalType animalType,
                               double weight, SpawnModification modification) {
        mapRules.computeIfAbsent(mapName, k -> new ArrayList<>())
                .add(new SpawnOption(animalType, weight, modification));
    }

    /**
     * Returns the list of spawn options for a given map.
     * If no options exist for the map, an empty list is returned.
     *
     * @param mapName the name of the map
     * @return a list of spawn options
     */

    public List<SpawnOption> getOptionsFor(String mapName) {
        return mapRules.getOrDefault(mapName, Collections.emptyList());
    }

    /**
     * Returns the name of this spawner configuration.
     *
     * @return the spawner name
     */

    public String getSpawnerName() {
        return spawnerName;
    }

    /**
     * Represents a single spawn option, linking an animal type
     * with its spawn weight and any applied modification.
     */
    public static class SpawnOption {
        private final AnimalRegistry.AnimalType animalType;
        private final double weight;
        private final SpawnModification modification;

        /**
         * Creates a new spawn option.
         *
         * @param animalType   the animal type to spawn
         * @param weight       the relative spawn weight
         * @param modification the modification to apply
         */

        public SpawnOption(AnimalRegistry.AnimalType animalType, double weight, SpawnModification modification) {
            this.animalType = animalType;
            this.weight = weight;
            this.modification = modification;
        }

        public AnimalRegistry.AnimalType getAnimalType() { return animalType; }
        public double getWeight() { return weight; }
        public SpawnModification getModification() { return modification; }
    }
}
