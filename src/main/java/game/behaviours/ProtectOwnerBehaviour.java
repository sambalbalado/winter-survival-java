package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.app.Abilities;
import game.actions.AttackAction;


/**
 * A behaviour that makes a tamed animal protect its owner.
 * <p>
 * The animal will attack any adjacent actor that has the {@code PREDATOR} ability
 * or any actor that is threatening its owner.
 * </p>
 *
 * @author Esaam Matudin
 * @version 3.1
 */
public class ProtectOwnerBehaviour implements Behaviour {

    /** The tamable animal performing this behaviour. */
    private final Tamable animal;

    /**
     * Constructor for ProtectOwnerBehaviour.
     *
     * @param animal the tamable animal that will protect its owner
     */
    public ProtectOwnerBehaviour(Tamable animal) {
        this.animal = animal;
    }

    /**
     * Generates an attack action if an adjacent actor is a threat to the owner.
     *
     * @param actor the actor performing the behaviour
     * @param map   the game map containing the actor
     * @return an {@link AttackAction} targeting a threatening actor, or null if no threat exists
     */
    @Override
    public Action generateAction(Actor actor, GameMap map) {
        Actor owner = animal.getOwner();

        if (owner == null || !map.contains(owner) || !map.contains(actor)) {
            return null;
        }

        Location here = map.locationOf(actor);

        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();

            if (destination.containsAnActor()) {
                Actor target = destination.getActor();

                // Skip attacking owner
                if (target == owner) continue;

                // Attack if target has PREDATOR ability (predator)
                boolean isPredator = target.hasAbility(Abilities.PREDATOR);

                if (isPredator) {
                    return new AttackAction(target, exit.getName());
                }
            }
        }

        return null; // nothing to attack
    }
}
