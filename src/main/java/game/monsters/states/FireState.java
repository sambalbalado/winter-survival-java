package game.monsters.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.actions.attackactions.BurningAttackAction;
import game.monsters.Morpher;
import game.status.Ignite;
import game.status.BurnSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A concrete {@link MorpherState} representing the Fire state of a {@link Morpher}.
 * <p>
 * In this state, the {@link Morpher} uses a fire breath attack with high damage
 * and may also set surrounding actors on fire with a {@link Ignite} status.
 * </p>
 *
 * <ul>
 *   <li><b>Attack:</b> Uses {@link BurningAttackAction} with fire breath.</li>
 *   <li><b>Wandering:</b> Moves randomly if no attack target is nearby.</li>
 *   <li><b>Effect:</b> Can apply {@link Ignite} to nearby actors when attacking.</li>
 *   <li><b>Transition:</b> Has a 75% chance to switch to {@link IceState},
 *       and a 25% chance to switch to {@link LightningState}.</li>
 * </ul>
 *
 * @author Esaam
 * @version 1.0
 */
public class FireState implements MorpherState {

    /** Random generator for attack and wandering decisions. */
    private final Random rand = new Random();

    /** The intrinsic fire breath weapon used in this state. */
    private final IntrinsicWeapon fireBreath =
            new IntrinsicWeapon(80, "breathes fire on", 80, "fire breath") {};

    /**
     * Defines the {@link Morpher}'s behaviour for a turn in the Fire state.
     * <p>
     * If an adjacent actor is found, attempts a {@link BurningAttackAction}.
     * There is a chance to also burn the surroundings. Otherwise, the morpher
     * may wander randomly or do nothing if no moves are possible.
     * </p>
     *
     * @param morpher the morpher in this state
     * @param map     the map containing the morpher
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
                    boolean burnSurrounding = false;

                    // 80% chance to burn surroundings
                    if (rand.nextDouble() < 0.8) {
                        applyStateEffect(morpher, map);
                        burnSurrounding = true;
                    }

                    return new BurningAttackAction(
                            target,
                            "burns fire on",
                            fireBreath,
                            burnSurrounding
                    );
                }
            }
        }

        // Otherwise, attempt to move randomly
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
     * Applies the Fire state's environmental effect: spreading {@link Ignite}
     * to nearby actors.
     *
     * @param morpher the morpher in this state
     * @param map     the map containing the morpher
     */
    @Override
    public void applyStateEffect(Morpher morpher, GameMap map) {
        Location here = map.locationOf(morpher);

        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();
            Actor other = destination.getActor();

            // guard first
            if (other == null || other == morpher) continue;

            boolean hasDragonBurn = other.statusesOf(Ignite.class)
                    .stream().anyMatch(b -> b.source() == BurnSource.DRAGON);

            if (!hasDragonBurn) {
                Ignite.applyOrRefresh(other, BurnSource.DRAGON);
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
     * Determines the next state transition from Fire.
     * <p>
     * - 75% chance to transition to {@link IceState}
     * - 25% chance to transition to {@link LightningState}
     * </p>
     *
     * @return the next {@link MorpherState}
     */
    @Override
    public MorpherState nextState() {
        double roll = Math.random(); // 0.0 <= roll < 1.0

        if (roll < 0.75) {
            return new IceState();
        } else {
            return null;
        }
    }

    /**
     * @return the string name of this state ("Fire")
     */
    @Override
    public String toString() {
        return "Fire";
    }
}
