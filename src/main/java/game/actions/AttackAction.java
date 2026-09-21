package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;

/**
 * An action that allows an actor to attack another actor.
 * The attack can be performed using an intrinsic weapon or a specified weapon.
 *
 * @author Adrian Kristanto (original supplied demo implementation)
 * @author Esaam Matudin (game adaptation and documentation)
 * @version 1.3
 */
public class AttackAction extends Action {

    /** The actor that is to be attacked. */
    private final Actor target;

    /** The direction of the target (for display purposes). */
    private final String direction;

    /** The weapon used for the attack. */
    private final Weapon weapon;

    /**
     * Constructor with a specified weapon.
     *
     * @param target    the actor to attack
     * @param direction the direction of the target
     * @param weapon    the weapon to use
     */
    public AttackAction(Actor target, String direction, Weapon weapon) {
        this.target = target;
        this.direction = direction;
        this.weapon = weapon;
    }

    /**
     * Constructor that defaults to the attacker's intrinsic weapon.
     *
     * @param target    the actor to attack
     * @param direction the direction of the target
     */
    public AttackAction(Actor target, String direction) {
        this(target, direction, null);
    }

    /**
     * Executes the attack.
     * If no weapon is specified, the attacker's intrinsic weapon is used.
     *
     * @param actor the actor performing the attack
     * @param map   the current game map
     * @return a string describing the result of the attack
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        Weapon attackWeapon = weapon != null ? weapon : actor.getIntrinsicWeapon();
        String result = attackWeapon.attack(actor, target, map);
        if (!target.isConscious()) {
            result += "\n" + target.unconscious(actor, map);
        }

        return result;
    }

    /**
     * Describes this attack action in the menu.
     *
     * @param actor the actor performing the action
     * @return a string describing the attack
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " attacks " + target + " at " + direction + " with " +
                (weapon != null ? weapon : "Intrinsic Weapon");
    }
}
