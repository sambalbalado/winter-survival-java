package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.monsters.Morpher;
import game.monsters.states.MorpherState;

/**
 * Represents an {@link Action} that allows a {@link Morpher} monster to
 * change into a new {@link MorpherState}.
 * <p>
 * When executed, the morpher updates its state and a descriptive
 * message is returned to indicate the transformation.
 * </p>
 *
 * @author Esaam matudin
 * @version 1.0
 */
public class MorphAction extends Action {

    /**
     * The {@link Morpher} performing the morph action.
     */
    private final Morpher morpher;

    /**
     * The new {@link MorpherState} the morpher will transform into.
     */
    private final MorpherState newState;

    /**
     * Constructs a {@code MorphAction} with the given morpher and target state.
     *
     * @param morpher  the morpher that will transform
     * @param newState the state into which the morpher will morph
     */
    public MorphAction(Morpher morpher, MorpherState newState) {
        this.morpher = morpher;
        this.newState = newState;
    }

    /**
     * Executes the morph action by updating the morpher's state and
     * returning a descriptive message of the transformation.
     *
     * @param actor the actor performing the action
     * @param map   the game map where the action occurs
     * @return a string describing the result of the morph action
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        String string = morpher + " morphs into " + newState + " state!";
        morpher.setState(newState); // update to new state
        return string;
    }

    /**
     * Provides a short menu description of the morph action.
     *
     * @param actor the actor performing the action
     * @return a string describing the morph action for menus
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " morphs into " + newState + " state";
    }
}
