// game/creatures/Crocodile.java
package game.creatures;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.app.Abilities;
import game.behaviours.AttackBehaviour;
// If you chose the dedicated class:
import game.weapons.CrocodileBite;

public class Crocodile extends Animal {

    /**
     * Crocodile: predator animal with 300 HP, warmth 55, and an intrinsic bite (80 dmg, 75%).
     * Wanders by default via Animal; attacks when a target is in range.
     *
     * @author Sai Sann Lone
     * @version 1.0
     */

    public Crocodile() {

        /**
         * Creates a Crocodile with name "Crocodile", display '<', 300 HP, and initial warmth 55.
         * Grants {@link Abilities#PREDATOR} and sets an intrinsic bite weapon.
         */
        super("Crocodile", '<', 300, 55);

        //Predator flag, consistent with Bear/Wolf patterns
        this.enableAbility(Abilities.PREDATOR);

        // Intrinsic weapon: dedicated CrocodileBite
        this.setIntrinsicWeapon(new CrocodileBite());
        // or: this.setIntrinsicWeapon(new Bite(80, 75, "Crocodile Bite "));

        // Attack if something is in range; wandering is already provided by Animal (priority 999).
        addNewBehaviour(3, new AttackBehaviour(this));
    }

    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        return new CrocodileBite();
        // or: return new Bite(80, 75, "Crocodile Bite ");
    }
}
