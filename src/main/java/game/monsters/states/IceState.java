package game.monsters.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.actions.attackactions.FreezingAttackAction;
import game.monsters.Morpher;
import game.status.Frozen;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A concrete {@link MorpherState} representing the Ice state of a {@link Morpher}.
 * <p>
 * In this state, the {@link Morpher} uses an ice breath attack that can freeze enemies,
 * potentially immobilising them with the {@link Frozen} status.
 * </p>
 *
 * <ul>
 *   <li><b>Attack:</b> Uses {@link FreezingAttackAction} with ice breath.</li>
 *   <li><b>Effect:</b> Has a 40% chance to apply {@link Frozen} to nearby actors.</li>
 *   <li><b>Wandering:</b> Moves randomly if no targets are adjacent.</li>
 *   <li><b>Transition:</b> 70% chance to switch to {@link LightningState},
 *       30% chance to switch to {@link FireState}.</li>
 * </ul>
 *
 * @author Esaam
 * @version 1.0
 */
public class IceState implements MorpherState {

    /** Random generator for deciding attack/behaviour. */
    private final Random rand = new Random();

    /** The intrinsic ice breath weapon unique to this state. */
    private final IntrinsicWeapon iceBreath =
            new IntrinsicWeapon(40, "slows down the", 80, "ice breath") {};

    /**
     * Defines the {@link Morpher}'s behaviour for a turn in the Ice state.
     * <p>
     * If there is a target in an adjacent tile, the morpher will attempt to use
     * {@link FreezingAttackAction}. Otherwise, it may wander randomly or do nothing.
     * </p>
     *
     * @param morpher the morpher in this state
     * @param map     the game map containing the morpher
     * @return the action chosen for this turn
     */
    @Override
    public Action playTurn(Morpher morpher, GameMap map) {
        Location here = map.locationOf(morpher);

        // Attempt to attack any adjacent actor
        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()) {
                Actor target = destination.getActor();
                if (target != null) {
                    boolean freezeSurrounding = false;

                    // 40% chance to apply freezing effect to nearby actors
                    if (rand.nextDouble() < 0.4) {
                        applyStateEffect(morpher, map);
                        freezeSurrounding = true;
                    }

                    return new FreezingAttackAction(
                            target,
                            "slows the",
                            iceBreath,
                            freezeSurrounding
                    );
                }
            }
        }

        // Otherwise, attempt to wander randomly
        List<Exit> exits = new ArrayList<>(here.getExits());
        if (!exits.isEmpty()) {
            Exit randomExit = exits.get(rand.nextInt(exits.size()));
            Location dest = randomExit.getDestination();
            if (!dest.containsAnActor()) {
                return new MoveActorAction(dest, randomExit.getName());
            }
        }

        // Fallback: do nothing
        return new DoNothingAction();
    }

    /**
     * Applies the Ice state's environmental effect: freezing nearby actors.
     * <p>
     * Applies the {@link Frozen} status to adjacent actors (excluding the morpher),
     * lasting for 3 turns.
     * </p>
     *
     * @param morpher the morpher in this state
     * @param map     the game map containing the morpher
     */
    @Override
    public void applyStateEffect(Morpher morpher, GameMap map) {
        Location here = map.locationOf(morpher);

        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();
            Actor actor = destination.getActor();
            if (actor != null && actor != morpher && !actor.hasStatus(Frozen.class)) {
                // Apply Frozen status: lasts 3 turns
                actor.addStatus(new Frozen(3));
            }
        }
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public void onEnter(Actor actor) {

    }

    @Override
    public void onExit(Actor actor) {

    }

    /**
     * Determines the next state transition from Ice.
     * <p>
     * - 70% chance to transition to {@link LightningState}
     * - 30% chance to transition to {@link FireState}
     * </p>
     *
     * @return the next {@link MorpherState}
     */
    @Override
    public MorpherState nextState() {
        double roll = Math.random(); // 0.0 <= roll < 1.0

        if (roll < 0.7) {
            return new LightningState();
        } else {
            return null;
        }
    }

    /**
     * @return the string name of this state ("Ice")
     */
    @Override
    public String toString() {
        return "Ice";
    }
}
