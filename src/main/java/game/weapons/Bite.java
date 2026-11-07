package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * Represents a Bite attack intrinsic weapon.
 * <p>
 * The Bite weapon is used by creatures (such as Wolves) for melee combat.
 * It supports lunar damage bonuses that increase attack power during certain moon phases.
 * <p>
 * Default Properties:
 * <ul>
 *   <li>Hit Rate: 50%</li>
 *   <li>Verb: "bites"</li>
 *   <li>Damage: Base damage + lunar bonus (if applicable)</li>
 * </ul>
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class Bite extends IntrinsicWeapon {

    /**
     * Constructor for Bite weapon with lunar damage bonus.
     * <p>
     * Creates a bite attack with combined base and lunar bonus damage.
     * During Full Moon phases, creatures with this weapon deal additional damage
     * through the lunar bonus system.
     *
     * @param lunarDamageBonus additional damage added during Full Moon phase
     *                         (typically +5 for wolves during Full Moon, 0 otherwise)
     * @param baseDamage the base damage of the bite attack before lunar bonuses
     *                   (e.g., 15 for wolves)
     */
    public Bite(int lunarDamageBonus, int baseDamage) {
        super(baseDamage + lunarDamageBonus, "bites", 50, "Bite ");
    }
}
