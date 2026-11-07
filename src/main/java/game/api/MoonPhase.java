package game.api;

/**
 * Represents the three moon phases that affect gameplay.
 * Each phase has a wolf spawn multiplier and display message.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public enum MoonPhase {
    /** New Moon: Wolves spawn at 50% rate, become docile */
    NEW_MOON(0.5, "🌑 NEW MOON - Wolves rest. Darkness prevails."),

    /** Normal Phase: Wolves spawn at 100% rate, standard behavior */
    NORMAL(1.0, "🌓 NORMAL PHASE - The moon waxes and wanes."),

    /** Full Moon: Wolves spawn at 200% rate, gain +5 damage, Lunar Berries appear */
    FULL_MOON(2.0, "🌕 FULL MOON - Wolves howl! Lunar Berries shimmer!");

    private final double wolfSpawnMultiplier;
    private final String displayMessage;

    /**
     * Creates a moon phase with spawn multiplier and display message.
     *
     * @param wolfSpawnMultiplier multiplier for wolf spawn rates
     * @param displayMessage message shown to player
     */
    MoonPhase(double wolfSpawnMultiplier, String displayMessage) {
        this.wolfSpawnMultiplier = wolfSpawnMultiplier;
        this.displayMessage = displayMessage;
    }

    /**
     * Returns the wolf spawn rate multiplier for this phase.
     *
     * @return spawn multiplier (0.5 for New Moon, 1.0 for Normal, 2.0 for Full Moon)
     */
    public double getWolfSpawnMultiplier() {
        return wolfSpawnMultiplier;
    }

    /**
     * Returns the display message for this phase.
     *
     * @return phase description shown to player
     */
    public String getDisplayMessage() {
        return displayMessage;
    }

    /**
     * Converts API moon phase value (0.0-1.0) to MoonPhase enum.
     * <p>
     * Mapping:
     * <ul>
     *   <li>0.00-0.15 and 0.85-1.00 → NEW_MOON</li>
     *   <li>0.40-0.60 → FULL_MOON</li>
     *   <li>All other values → NORMAL</li>
     * </ul>
     *
     * @param value moon phase value from API (0.0 = new moon, 0.5 = full moon, 1.0 = new moon)
     * @return corresponding MoonPhase enum value
     */
    public static MoonPhase fromApiValue(double value) {
        if ((value >= 0.0 && value <= 0.15) || (value >= 0.85 && value <= 1.0)) {
            return NEW_MOON;
        } else if (value >= 0.40 && value <= 0.60) {
            return FULL_MOON;
        } else {
            return NORMAL;
        }
    }
}
