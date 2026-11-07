package game.monsters.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.actions.attackactions.LightningAttackAction;
import game.monsters.Morpher;
import game.status.Stunned;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The {@code LightningState} represents the lightning form of the {@link Morpher}.
 * <p>
 * In this state, the Morpher primarily uses a {@code lightningStrike} as its weapon,
 * with a high chance to stun surrounding enemies. Stunned enemies take damage over time
 * and are temporarily immobilized.
 * </p>
 *
 * Behaviour:
 * <ul>
 *     <li>Attempts to attack any adjacent actor using a lightning strike.</li>
 *     <li>Has a chance (80%) to apply a {@link Stunned} status to surrounding enemies.</li>
 *     <li>If no enemies are nearby, the Morpher wanders randomly.</li>
 *     <li>If unable to move, it does nothing.</li>
 * </ul>
 *
 * Transitions:
 * <ul>
 *     <li>60% chance to morph into {@link FireState}.</li>
 *     <li>40% chance to morph into {@link IceState}.</li>
 * </ul>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class LightningState implements MorpherState {
    /** Random number generator for attack and state transition chances. */
    private final Random rand = new Random();

    /** The lightning-based intrinsic weapon used by the Morpher in this state. */
    private final IntrinsicWeapon lightningStrike =
            new IntrinsicWeapon(35, "shocks the", 85, "lightning strike") {};

    /**
     * Defines the Morpher's behaviour for a single turn while in the lightning state.
     * <p>
     * The Morpher prioritizes attacking nearby targets, applying lightning shock effects,
     * or moving randomly if no targets are found.
     * </p>
     *
     * @param morpher the {@link Morpher} in its lightning state
     * @param map the current {@link GameMap}
     * @return an {@link Action} that the Morpher will perform
     */
    @Override
    public Action playTurn(Morpher morpher, GameMap map) {
        Location here = map.locationOf(morpher);

        // Try to attack if there's a nearby target
        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()) {
                Actor target = destination.getActor();
                if (target != null) {

                    boolean shockSurrounding = false;
                    // 80% chance to shock surrounding enemies when attacking
                    if (rand.nextDouble() < 0.8) {
                        applyStateEffect(morpher, map);
                        shockSurrounding = true;
                    }

                    // Perform a lightning strike attack
                    return new LightningAttackAction(
                            target,
                            "shocks the",
                            lightningStrike,
                            shockSurrounding
                    );
                }
            }
        }

        // Otherwise, wander randomly
        List<Exit> exits = new ArrayList<>(here.getExits());
        if (!exits.isEmpty()) {
            Exit randomExit = exits.get(rand.nextInt(exits.size()));
            Location dest = randomExit.getDestination();
            if (!dest.containsAnActor()) {
                return new MoveActorAction(dest, randomExit.getName());
            }
        }

        // If nothing else can be done, do nothing
        return new DoNothingAction();
    }

    /**
     * Applies the lightning state's effect to actors surrounding the Morpher.
     * <p>
     * Nearby actors (excluding the Morpher itself) are given the {@link Stunned} status,
     * lasting for 2 turns. Stunned actors cannot act and take damage each turn.
     * </p>
     *
     * @param morpher the {@link Morpher} applying the effect
     * @param map the current {@link GameMap}
     */
    @Override
    public void applyStateEffect(Morpher morpher, GameMap map) {
        Location here = map.locationOf(morpher);

        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();
            Actor actor = destination.getActor();
            if (actor != null && actor != morpher && !actor.hasStatus(Stunned.class)) {
                // Apply Stunned status: immobilizes and deals damage for 2 turns
                actor.addStatus(new Stunned(2));
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
     * Determines the next state the Morpher will morph into.
     *
     * @return {@link FireState} with 60% probability, {@link IceState} with 40% probability
     */
    @Override
    public MorpherState nextState() {
        double roll = Math.random(); // 0.0 <= roll < 1.0

        if (roll < 0.6) {           // 60% chance
            return new FireState();
        } else
            return null; // next 40%
    }

    /**
     * Provides a human-readable representation of this state.
     *
     * @return the string "Lightning"
     */
    @Override
    public String toString() {
        return "Lightning";
    }
}
