package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.weapons.Bow;

/**
 * Action for performing ranged attacks with a bow.
 * Checks if the target is within range before executing the attack.
 *
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public class RangedAttackAction extends Action {

    /**
     * The target actor to be attacked
     */
    private Actor target;

    /**
     * The direction of the attack relative to the actor
     */
    private String direction;

    /**
     * The weapon used for the ranged attack
     */
    private Weapon weapon;

    /**
     * Constructor for RangedAttackAction.
     *
     * @param target the actor to attack
     * @param direction the direction of the target
     * @param weapon the weapon used for the attack
     */
    public RangedAttackAction(Actor target, String direction, Weapon weapon) {
        this.target = target;
        this.direction = direction;
        this.weapon = weapon;
    }

    /**
     * Executes the ranged attack action.
     * First checks if the target is within range, then performs the attack if valid.
     *
     * @param actor the actor performing the action
     * @param map the game map
     * @return description of the attack result or failure message if out of range
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!Bow.isWithinRange(actor, target, map)) {
            return actor + " cannot reach " + target + " with the bow!";
        }

        return weapon.attack(actor, target, map);
    }

    /**
     * Returns a description for the menu.
     *
     * @param actor the actor who would perform the action
     * @return menu description
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " shoots " + target + " at " + direction + " with " + weapon;
    }
}
