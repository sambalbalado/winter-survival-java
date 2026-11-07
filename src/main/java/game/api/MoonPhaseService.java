package game.api;

/**
 * Interface for fetching moon phase data.
 * Follows Dependency Inversion Principle - high-level code depends on this abstraction.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */

public interface MoonPhaseService {

    /**
     * Fetches the current moon phase from external source
     *
     * @return Current MoonPhase enum value
     * @throws Exception if API call fails
     */
    MoonPhase fetchCurrentPhase() throws Exception;

    /**
     * Checks if the service is available
     *
     * @return true if service can be reached, false otherwise
     */
    boolean isAvailable();
}
