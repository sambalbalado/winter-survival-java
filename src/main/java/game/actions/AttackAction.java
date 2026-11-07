package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;

/**
 * Fixed AttackAction that safely uses intrinsic weapons
 * without trying to move the attacker into the target.

 * This action represents a combat attack between two actors, where one actor
 * (the attacker) attempts to deal damage to another actor (the target) using
 * either a specified weapon or their intrinsic weapon. The action handles
 * both weapon-based and intrinsic weapon attacks, and automatically processes
 * the consequences if the target becomes unconscious.
 *
 * Based on the AttackAction class from the FIT2099 demo project.
 * @author Adrian Kristanto (original implementation)
 * @author Taabish Farooq Bhat (Adapted for current implementation)
 * @version 1.0
 */
public class AttackAction extends Action {

    private Actor target;
    private String direction;
    private Weapon weapons;


    /**
     * Constructor for creating an AttackAction with a specific weapon.
     *
     * @param target the Actor to be attacked
     * @param direction the direction string indicating the target's location relative to the attacker
     * @param weapons the Weapon to be used for the attack
     */
    public AttackAction(Actor target, String direction, Weapon weapons) {
        this.target = target;
        this.direction = direction;
        this.weapons = weapons;
    }

    /**
     * Constructor for creating an AttackAction using the actor's intrinsic weapon.
     * The weapon will be determined during execution by calling the actor's getIntrinsicWeapon() method.
     *
     * @param target the Actor to be attacked
     * @param direction the direction string indicating the target's location relative to the attacker
     */
    public AttackAction(Actor target, String direction) {
        this.target = target;
        this.direction = direction;
    }

    /**
     * Executes the attack action.
     *
     * If no weapon was specified in the constructor, this method will use the actor's
     * intrinsic weapon. The attack is performed using the weapon's attack method,
     * and if the target becomes unconscious as a result, the target's unconscious
     * method is called to handle the consequences.
     *
     * @param actor the Actor performing the attack
     * @param map the GameMap where the action takes place
     * @return a String describing the result of the attack, including any consequences
     *         if the target becomes unconscious
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (weapons == null) {
            weapons = actor.getIntrinsicWeapon();
        }

        String result = weapons.attack(actor, target, map);

        if (!target.isConscious()) {
            result += "\n" + target.unconscious(actor, map);
        }

        return result;
    }

    /**
     * Returns a description of this action suitable for display in a menu.
     *
     * The description includes the attacker, target, direction, and weapon being used.
     * If no specific weapon was provided, it will display "Intrinsic Weapon".
     *
     * @param actor the Actor who would perform this action
     * @return a String describing the action in a user-friendly format
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " attacks " + target + " at " + direction +
                (weapons != null ? " with " + weapons.getClass().getSimpleName() : "");
    }
}
