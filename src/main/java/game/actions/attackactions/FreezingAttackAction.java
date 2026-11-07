package game.actions.attackactions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.actions.AttackAction;

/**
 * Represents a specialized {@link AttackAction} that includes a freezing effect.
 * <p>
 * When executed, this attack may not only damage the target but also freeze
 * the surroundings, depending on the configuration.
 * </p>
 *
 * @author Esaam matudin
 * @version 1.0
 */
public class FreezingAttackAction extends AttackAction {

    /**
     * Flag indicating whether the attack also freezes the surroundings.
     */
    private final boolean frozeSurroundings;

    /**
     * Constructs a FreezingAttackAction with the specified target, verb,
     * weapon, and freezing effect.
     *
     * @param target             the target actor of the attack
     * @param verb               the verb describing the attack (e.g., "freezes")
     * @param weapon             the intrinsic weapon used in the attack
     * @param frozeSurroundings  true if the attack should also freeze the surroundings,
     *                           false otherwise
     */
    public FreezingAttackAction(Actor target, String verb, IntrinsicWeapon weapon, boolean frozeSurroundings) {
        super(target, verb, weapon);
        this.frozeSurroundings = frozeSurroundings;
    }

    /**
     * Executes the freezing attack action. The base attack is performed first,
     * and if {@code frozeSurroundings} is true, a message indicating that the
     * surroundings are frozen is appended to the result.
     *
     * @param actor the actor performing the attack
     * @param map   the map in which the attack takes place
     * @return a descriptive string of the action’s result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        String result = super.execute(actor, map);

        // If the attack freezes surroundings, append a descriptive message
        if (frozeSurroundings) {
            result += " and freezes the surroundings!";
        }
        return result;
    }
}
