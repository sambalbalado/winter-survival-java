package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * A weapon representing a bite attack.
 * <p>
 * This is an intrinsic weapon used by an actor without needing to carry an item.
 * It has a fixed damage and hit rate.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class Bite extends IntrinsicWeapon {

    /**
     * Constructor for a Bite weapon.
     * Initializes the damage, verb, hit rate, and weapon name.
     */
    public Bite() {
        super(50, "bites", 50, "Bite");
    }
}
