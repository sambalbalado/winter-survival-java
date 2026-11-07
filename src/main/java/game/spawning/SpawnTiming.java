// SpawnTiming.java
package game.spawning;

import edu.monash.fit2099.engine.positions.Location;

import java.util.Random;

/**
 * Defines different timing rules for animal spawning.
 * <p>
 * Each implementation determines whether a spawn should be attempted
 * on a given turn, based on interval or random chance. This allows
 * different ground types to control how often spawning occurs.
 *
 * @author Sai Sann Lone
 * @version 1.0
 */
public interface SpawnTiming {
    /**
     * Checks whether spawning should be attempted this turn.
     *
     * @param turnCount the current turn number
     * @return {@code true} if spawning should occur, otherwise {@code false}
     */
    boolean shouldAttemptSpawn(int turnCount);

    // NEW (non-breaking): location-aware check; defaults to old behaviour
    default boolean shouldAttemptSpawn(int turnCount, Location location) {
        return shouldAttemptSpawn(turnCount);
    }

    /**
     * Spawns at a fixed interval (e.g. every 5 turns).
     * <p>
     * Used for caves — animals appear at consistent time steps.
     */
    class FixedInterval implements SpawnTiming {
        private final int interval;

        /**
         * Creates a fixed interval spawner.
         *
         * @param interval the number of turns between spawns
         */
        public FixedInterval(int interval) {
            this.interval = interval;
        }

        @Override
        public boolean shouldAttemptSpawn(int turnCount) {
            return turnCount % interval == 0;
        }
    }

    /**
     * Spawns at a set interval but only with a given probability.
     * <p>
     * Used for meadows — e.g. 50% chance every 7 turns.
     */
    class IntervalWithProbability implements SpawnTiming {
        private final int interval;
        private final double probability;
        private final Random random = new Random();

        /**
         * Creates a timed spawner with both interval and probability.
         *
         * @param interval    how often to check for spawning
         * @param probability the chance of spawning on an interval turn
         */

        public IntervalWithProbability(int interval, double probability) {
            this.interval = interval;
            this.probability = probability;
        }

        @Override
        public boolean shouldAttemptSpawn(int turnCount) {
            // Only check on interval turns (7, 14, 21, etc.)
            if (turnCount % interval != 0) {
                return false;
            }
            // On interval turns, roll probability
            return random.nextDouble() < probability;
        }
    }

    /**
     * Spawns with a random chance every turn.
     * <p>
     * Used for tundra — e.g. 5% chance each turn.
     */
    class RandomChance implements SpawnTiming {
        private final double probability;
        private final Random random = new Random();

        /**
         * Creates a spawner with a fixed probability each turn.
         *
         * @param probability the chance to spawn per turn (0.0–1.0)
         */
        public RandomChance(double probability) {
            this.probability = probability;
        }

        @Override
        public boolean shouldAttemptSpawn(int turnCount) {
            return random.nextDouble() < probability;
        }
    }

    /**
     * Swamp timing: attempt only if any adjacent tile has an actor; then apply probability.
     * Use probability = 0.5 for the “50%” swamp requirement.
     */
    class ProximityWithChance implements SpawnTiming {
        private final double probability;
        private final Random random = new Random();

        public ProximityWithChance(double probability) {
            this.probability = probability;
        }

        @Override
        public boolean shouldAttemptSpawn(int turnCount) {
            // not used for proximity; keep false so legacy calls don’t trigger
            return false;
        }

        @Override
        public boolean shouldAttemptSpawn(int turnCount, Location location) {
            if (location == null) return false;

            // proximity gate: any of the 8 neighbours has an actor?
            boolean someoneNearby = location.getExits().stream()
                    .anyMatch(exit -> location.map().isAnActorAt(exit.getDestination()));

            if (!someoneNearby) return false;

            // 50% chance (or whatever you pass to the ctor)
            return random.nextDouble() < probability;
        }
    }
}
