package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;
import game.capabilities.Tamable;

/**
 * A behaviour that makes a {@link Tamable} actor attack any valid adjacent actor.
 * <p>
 * If the actor is tamed, it will not attack. If the actor is untamed, it will attack
 * any adjacent actor that is not its owner.
 * </p>
 *
 * @author Esaam Matudin
 * @version 3.2
 */
public class AttackBehaviour implements Behaviour {

    /** The tamable actor this behaviour belongs to. */
    private final Tamable animal;

    /**
     * Constructor.
     *
     * @param animal the tamable actor to apply this attack behaviour
     */
    public AttackBehaviour(Tamable animal) {
        this.animal = animal;
    }

    /**
     * Generates an attack action if there is a valid target adjacent to the actor.
     * <p>
     * Iterates through all exits around the actor's current location. If a valid target
     * is found, returns an {@link AttackAction} towards that target. Returns null if
     * the actor is tamed or no valid target exists.
     * </p>
     *
     * @param actor the actor performing the behaviour
     * @param map   the map containing the actor
     * @return an {@link AttackAction} targeting a nearby actor, or null if no target
     */
    @Override
    public Action generateAction(Actor actor, GameMap map) {
        Location here = map.locationOf(actor);

        // Check all surrounding locations
        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();
            if (destination.containsAnActor()) {
                Actor target = destination.getActor();
                // If tamed, do not attack
                if (animal.isTamed()) {
                    return null;
                    // If untamed, attack actors that are not the owner & Itself
                } else if (target != actor && !target.equals(animal.getOwner())) {
                    return new AttackAction(target, exit.getName());
                }
            }
        }
        return null; // no attack possible
    }
}
