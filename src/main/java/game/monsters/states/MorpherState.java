package game.monsters.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.monsters.Morpher;

/**
 * The {@code MorpherState} interface defines the different elemental or behavioral
 * states a {@link Morpher} can take on.
 * <p>
 * Each state controls:
 * <ul>
 *     <li>What the Morpher does each turn ({@link #playTurn(Morpher, GameMap)})</li>
 *     <li>What extra effects the state applies to surrounding actors ({@link #applyStateEffect(Morpher, GameMap)})</li>
 *     <li>Which state the Morpher will transition into next ({@link #nextState()})</li>
 * </ul>
 * </p>
 *
 * This interface enables the Morpher to dynamically switch between states
 * (e.g., Fire, Ice, Lightning) following the State design pattern.
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public interface MorpherState {

    /**
     * Defines the Morpher's action for the current turn while in this state.
     *
     * @param morpher the {@link Morpher} currently in this state
     * @param map the {@link GameMap} where the Morpher exists
     * @return an {@link Action} that the Morpher will perform
     */
    Action playTurn(Morpher morpher, GameMap map);

    /**
     * Applies any special effects this state may impose on nearby actors
     * (e.g., freezing, stunning, or burning).
     *
     * @param morpher the {@link Morpher} applying the effect
     * @param map the current {@link GameMap}
     */
    void applyStateEffect(Morpher morpher, GameMap map);

    String name();

    void onEnter(Actor actor);

    void onExit(Actor actor);

    /**
     * Determines the next state the Morpher will transition into.
     *
     * @return the next {@link MorpherState}
     */
    MorpherState nextState();
}
