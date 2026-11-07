package game.environment;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * Represents snow terrain in the game.
 * Snow can be used as a coating material for weapons.
 * When actors walk on snow terrain, they can collect snow coating for their coatable weapons.
 *
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public class Snow extends Ground {

    /**
     * The type of coating this snow provides
     */
    private String coatingType;

    /**
     * Constructor for Snow.
     * Creates snow terrain with the '.' display character and "Snow" coating type.
     */
    public Snow() {
        super('.', "Snow");
        this.coatingType = "Snow";
    }

    /**
     * Gets the coating type provided by this snow terrain.
     *
     * @return the coating type string
     */
    public String getCoatingType() {
        return coatingType;
    }

    /**
     * Sets the coating type for this snow terrain.
     *
     * @param coatingType the new coating type
     */
    public void setCoatingType(String coatingType) {
        this.coatingType = coatingType;
    }
}
