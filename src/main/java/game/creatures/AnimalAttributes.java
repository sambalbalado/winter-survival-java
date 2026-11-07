package game.creatures;

/**
 * Enumerates animal-specific attributes tracked on {@link edu.monash.fit2099.engine.actors.Actor}s.
 * <p>
 * This mirrors the Explorer-style attribute system so creatures can participate
 * in the same per-turn decay/KO rules without hard-coding fields in each class.
 * </p>
 *
 * @author  Sai Sann Lone
 * @version 1.0
 */

public enum AnimalAttributes {
    /**
     * Represents an animal's thermal resilience in cold environments.
     * Decreases over time; reaching zero (without cold resistance) causes KO/removal.
     */
    WARMTH
}
