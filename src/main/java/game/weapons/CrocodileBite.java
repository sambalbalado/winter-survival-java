// game/weapons/CrocodileBite.java
package game.weapons;
/**
 * Intrinsic bite for crocodiles: 80 damage, 75% hit rate.
 * @author Sai Sann
 *
 */

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;


public class CrocodileBite extends IntrinsicWeapon {
    /**
     * Creates the crocodile bite intrinsic weapon.
     * Damage: 80, Verb: "bites", Hit%: 75, Name: "Crocodile Bite ".
     */
    public CrocodileBite() {super(80, "bites", 75, "Crocodile Bite ");
    }
}
