package game.items;

/**
 * Interface for weapons that can be coated.
 * Torches should NOT implement this interface.
 *
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public interface Coatable {

    /**
     * Applies a coating to this weapon.
     * Replaces any existing coating.
     *
     * @param coatingItem the name of the coating item (e.g., "yewberry")
     */
    void applyCoating(String coatingItem);

    /**
     * Removes the current coating from this weapon.
     */
    void removeCoating();

    /**
     * Gets the current coating on this weapon.
     *
     * @return the coating name, or null if no coating
     */
    String getCoating();

    /**
     * Checks if this weapon currently has a coating.
     *
     * @return true if coated, false otherwise
     */
    default boolean hasCoating() {
        return getCoating() != null && !getCoating().isEmpty();
    }
}
