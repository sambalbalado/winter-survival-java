package game.actions.attackactions;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;
import game.actions.AttackAction;

/**
 * Represents a specialized {@link AttackAction} that includes a burning effect.
 * <p>
 * When executed, this attack may not only damage the target but also burn
 * the surroundings, depending on the configuration.
 * </p>
 *
 * @author Esaam matudin
 * @version 1.0
 */
public class BurningAttackAction extends AttackAction {

    /**
     * Flag indicating whether the attack also burns the surroundings.
     */
    private final boolean burnedSurroundings;

    /**
     * Constructs a BurningAttackAction with the specified target, verb,
     * weapon, and burning effect.
     *
     * @param target              the target actor of the attack
     * @param verb                the verb describing the attack (e.g., "burns")
     * @param weapon              the weapon used in the attack
     * @param burnedSurroundings  true if the attack should also burn the surroundings,
     *                            false otherwise
     */
    public BurningAttackAction(Actor target, String verb, Weapon weapon, boolean burnedSurroundings) {
        super(target, verb, weapon);
        this.burnedSurroundings = burnedSurroundings;
    }

    /**
     * Executes the burning attack action. The base attack is performed first,
     * and if {@code burnedSurroundings} is true, a message indicating that the
     * surroundings are burned is appended to the result.
     *
     * @param actor the actor performing the attack
     * @param map   the map in which the attack takes place
     * @return a descriptive string of the action’s result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        String result = super.execute(actor, map);

        // If the attack burns surroundings, append a descriptive message
        if (burnedSurroundings) {
            String[] lines = result.split("\n", 2);
            lines[0] += " and burns surroundings!";
            result = String.join("\n", lines);
        }

        return result;
    }
}
