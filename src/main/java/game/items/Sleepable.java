package game.items;

/**
 * Interface for actors that can sleep.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public interface Sleepable {
    /**
     * Sets the sleeping state of this sleepable actor.
     *
     * @param sleeping true to set the actor as sleeping, false to wake them up
     */
    void setSleeping(boolean sleeping);
}
