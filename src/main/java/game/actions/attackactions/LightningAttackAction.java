package game.actions.attackactions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.actions.AttackAction;

/**
 * Represents a specialized {@link AttackAction} that includes a lightning effect.
 * <p>
 * When executed, this attack may not only damage the target but also shock
 * the surroundings, depending on the configuration.
 * </p>
 *
 * @author Esaam matudin
 * @version 1.0
 */
public class LightningAttackAction extends AttackAction {

    /**
     * Flag indicating whether the attack also shocks the surroundings.
     */
    private final boolean shockedSurroundings;

    /**
     * Constructs a LightningAttackAction with the specified target, verb,
     * weapon, and lightning effect.
     *
     * @param target              the target actor of the attack
     * @param verb                the verb describing the attack (e.g., "shocks")
     * @param weapon              the intrinsic weapon used in the attack
     * @param shockedSurroundings true if the attack should also shock the surroundings,
     *                            false otherwise
     */
    public LightningAttackAction(Actor target, String verb, IntrinsicWeapon weapon, boolean shockedSurroundings) {
        super(target, verb, weapon);
        this.shockedSurroundings = shockedSurroundings;
    }

    /**
     * Executes the lightning attack action. The base attack is performed first,
     * and if {@code shockedSurroundings} is true, a message indicating that the
     * surroundings are shocked is appended to the result.
     *
     * @param actor the actor performing the attack
     * @param map   the map in which the attack takes place
     * @return a descriptive string of the action’s result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        String result = super.execute(actor, map);

        // If the attack shocks surroundings, append a descriptive message
        if (shockedSurroundings) {
            result += " and shocks the surroundings!";
        }
        return result;
    }
}
