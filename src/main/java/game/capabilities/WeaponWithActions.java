package game.capabilities;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;

/**
 * Interface representing a weapon that can provide additional actions beyond basic attacks.
 * Extends the base Weapon interface to support melee and ranged attack capabilities.
 *
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public interface WeaponWithActions extends Weapon {

    /**
     * Gets the name of the weapon.
     *
     * @return the weapon's name
     */
    String getName();

    /**
     * Determines if this weapon is a ranged weapon.
     *
     * @return true if the weapon can attack at range, false otherwise
     */
    boolean isRangedWeapon();

    /**
     * Returns the list of allowable actions for this weapon.
     * Default implementation returns an empty ActionList.
     *
     * @param a the actor holding the weapon
     * @param m the game map
     * @return list of allowable actions
     */
    default ActionList allowableActions(Actor a, GameMap m) {
        return new ActionList();
    }

    /**
     * Returns the list of ranged attack actions available for this weapon.
     * Default implementation returns an empty ActionList.
     *
     * @param a the actor holding the weapon
     * @param m the game map
     * @return list of ranged attack actions
     */
    default ActionList getRangedAttackActions(Actor a, GameMap m) {
        return new ActionList();
    }

    /**
     * Performs an attack with this weapon against a target.
     *
     * @param attacker the actor performing the attack
     * @param target the actor being attacked
     * @param map the game map
     * @return description of the attack result
     */
    @Override
    String attack(Actor attacker, Actor target, GameMap map);
}
