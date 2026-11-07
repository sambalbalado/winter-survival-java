package game.monsters;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.MorphAction;
import game.monsters.states.MorpherState;

/**
 * An abstract base class for monsters that can morph between different {@link MorpherState}s.
 * <p>
 * Morphers cycle through different states (e.g., {@code FireState}, {@code IceState}, {@code LightningState}),
 * each of which controls the monster's behaviour, attacks, and special effects.
 * </p>
 * <p>
 * By default, a Morpher:
 * <ul>
 *     <li>Morphs into its next state every 5 turns</li>
 *     <li>Delegates its turn behaviour to the current state</li>
 * </ul>
 * </p>
 *
 * @author Esaam
 * @version 1.0
 */
public abstract class Morpher extends Actor {

    /** Number of turns spent in the current state before morphing */
    private int turnsInState = 0;

    /** The current state of this Morpher (e.g., Fire, Ice, Lightning) */
    protected MorpherState currentState;

    /**
     * Constructs a new Morpher with the given parameters.
     *
     * @param name        the name of the Morpher
     * @param displayChar the character used to represent the Morpher on the map
     * @param hitPoints   the starting hit points of the Morpher
     */
    public Morpher(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints);
    }

    /**
     * Decides the Morpher's action each turn.
     * <p>
     * The Morpher will:
     * <ul>
     *     <li>Morph into its next state if enough turns have passed</li>
     *     <li>Otherwise, perform the action defined by its current state</li>
     * </ul>
     * </p>
     *
     * @param actions    list of possible actions for this turn
     * @param lastAction the last action performed
     * @param map        the current game map
     * @param display    the I/O display for showing messages
     * @return the action to perform this turn
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (shouldMorph()) {
            MorpherState next = currentState.nextState();
            if (next != null) { // only morph if there's a next state
                return new MorphAction(this, next);
            }
        }
        return currentState.playTurn(this, map);
    }

    /**
     * Determines whether the Morpher should morph into a new state.
     * <p>
     * By default, the Morpher morphs every 5 turns, resetting its turn counter when it does.
     * </p>
     *
     * @return {@code true} if the Morpher should morph this turn, {@code false} otherwise
     */
    private boolean shouldMorph() {
        turnsInState++;
        if (turnsInState >= 5) {
            turnsInState = 0;
            return true;
        }
        return false;
    }

    /**
     * Sets the Morpher's current state.
     *
     * @param state the new {@link MorpherState} to assign
     */
    public void setState(MorpherState state) {
        this.currentState = state;
    }

    /**
     * Gets the Morpher's current state.
     *
     * @return the current {@link MorpherState}
     */
    public MorpherState getState() {
        return currentState;
    }
}
