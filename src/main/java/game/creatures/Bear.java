package game.creatures;

import game.app.Abilities;
import game.behaviours.*;
import game.weapons.Claw;

/**
 * A bear actor that can wander, attack, and be tamed.
 * <p>
 * Bears use a claw as their intrinsic weapon. Once tamed, they can follow and protect
 * their owner. They also track a warmth attribute that decays each turn; at zero,
 * they are removed unless cold-resistant.
 *
 * @author Sai Sann Lone
 * @version 8.0
 */
public class Bear extends Animal {

    /**
     * Creates a bear with default stats, abilities, and behaviours.
     * Sets warmth to 50 (displays as 49 after initial decrement).
     */
    public Bear() {
        super("Bear", 'B', 200, 50);

        // Bear-specific abilities
        this.enableAbility(Abilities.PREDATOR);
        this.enableAbility(Abilities.CAN_CONSUME);
        this.enableAbility(Abilities.CONSUMES_GROUND_ITEMS);
        this.setIntrinsicWeapon(new Claw());


        // Consumption behaviour
        addNewBehaviour(2, new ConsumeGroundItemsBehaviour());

        // Attack behaviour
        addNewBehaviour(3, new AttackBehaviour(this));

        // Tamed behaviours (only active when tamed)
        addNewBehaviour(4, new ProtectOwnerBehaviour(this));
    }

    /**
     * Tames the bear and assigns the owner.
     * Disables the {@code PREDATOR} ability when tamed.
     *
     * @param owner the actor who tames the bear
     */
    @Override
    public void tame(edu.monash.fit2099.engine.actors.Actor owner) {
        super.tame(owner);
        this.disableAbility(Abilities.PREDATOR);
    }
}
