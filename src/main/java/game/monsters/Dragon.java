package game.monsters;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.app.Abilities;
import game.monsters.states.FireState;

/**
 * A {@link Dragon} is a powerful {@link Morpher} that begins in the {@link FireState}.
 * <p>
 * Dragons are hostile monsters that:
 * <ul>
 *     <li>Attack nearby actors if possible</li>
 *     <li>Wander randomly if no target is nearby</li>
 *     <li>Cannot be tamed by the player</li>
 * </ul>
 * </p>
 * <p>
 * The Dragon also possesses the {@link Abilities#CAN_ATTACK} and
 * {@link Abilities#PREDATOR} abilities, making it a dangerous enemy on the map.
 * </p>
 *
 * @author Esaam
 * @version 1.0
 */
public class Dragon extends Morpher {

    /**
     * Creates a new Dragon that starts in the {@link FireState}.
     * <p>
     * By default, a Dragon:
     * <ul>
     *     <li>Has 500 hit points</li>
     *     <li>Is represented by the character 'D'</li>
     *     <li>Is hostile and functions as a predator</li>
     * </ul>
     * </p>
     */
    public Dragon() {
        super("Dragon", 'D', 500);
        setState(new FireState()); // Dragon starts with FireState
        this.enableAbility(Abilities.CAN_ATTACK);
        this.enableAbility(Abilities.PREDATOR);
    }

    /**
     * Defines what actions other actors can perform on this Dragon.
     *
     * @param otherActor the actor interacting with the Dragon
     * @param direction the direction of the other actor from this Dragon
     * @param map the map containing both actors
     * @return a list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        return super.allowableActions(otherActor, direction, map);
    }

    /**
     * Returns a string representation of this Dragon,
     * prefixed by its current {@link game.monsters.states.MorpherState}.
     *
     * @return the string representation of the Dragon with its current state
     */
    @Override
    public String toString() {
        return currentState.toString() + " " + super.toString();
    }
}
