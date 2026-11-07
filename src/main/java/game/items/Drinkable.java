package game.items;

/**
 * Interface for actors that can drink and increase their hydration.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public interface Drinkable {
    /**
     * Increases the hydration level of this drinkable actor.
     *
     * @param amount the amount to increase hydration by
     */
    void increaseHydration(int amount);
}
