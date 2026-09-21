package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * A weapon representing a claw attack.
 * <p>
 * This is an intrinsic weapon used by an actor without needing to carry an item.
 * It has a fixed damage and hit rate.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class Claw extends IntrinsicWeapon {

    /**
     * Constructor for a Claw weapon.
     * Initializes the damage, verb, hit rate, and weapon name.
     */
    public Claw() {
        super(75, "claws", 80, "claw");
    }
}
