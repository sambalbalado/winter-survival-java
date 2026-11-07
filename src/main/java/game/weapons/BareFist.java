package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * Represents a bare fist intrinsic weapon for unarmed combat.
 *
 * The bare fist deals 25 damage with a 50% hit rate.
 * This is the default weapon used when an actor has no other weapons equipped.
 *
 * @author Adrian Kristanto
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public class BareFist extends IntrinsicWeapon  {

    /**
     * Constructor for BareFist.
     * Creates a bare fist weapon with 25 damage and 50% hit rate.
     */
    public BareFist() {
        super(25, "punches", 50, "bare fist");
    }

}
