package game.environment;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * Represents dirt terrain in the game.
 * Dirt is a basic ground type that serves as neutral terrain.
 * It can be created when fire is extinguished or as default terrain.
 *
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public class Dirt extends Ground {

    /**
     * Constructor for Dirt.
     * Creates dirt terrain with the '#' display character.
     */
    public Dirt() {
        super('+', "dirt");
    }
}
