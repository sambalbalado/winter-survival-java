package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.FrozenAction;
import game.actions.StunnedAction;
import game.status.Frozen;
import game.status.Stunned;

/**
 * A {@link Behaviour} that prevents an {@link Actor} from taking normal actions
 * when immobilised by statuses such as {@link Frozen} or {@link Stunned}.
 * <p>
 * If the actor has the {@link Frozen} status, a {@link FrozenAction} is returned.
 * If the actor has the {@link Stunned} status, a {@link StunnedAction} is returned.
 * If neither status is present, no action is generated.
 * </p>
 *
 * <p>This ensures that immobilisation effects override other behaviours.</p>
 *
 * @author Esaam
 * @version 1.0
 */
public class ImmobilizedBehaviour implements Behaviour {

    /**
     * Generates an immobilisation action depending on the actor’s current status.
     *
     * @param actor the actor whose behaviour is being evaluated
     * @param map   the game map containing the actor
     * @return a {@link FrozenAction} if the actor is frozen,
     *         a {@link StunnedAction} if the actor is stunned,
     *         or {@code null} if neither status applies
     */
    @Override
    public Action generateAction(Actor actor, GameMap map) {
        if (actor.hasStatus(Frozen.class)) {
            return new FrozenAction();
        }
        if (actor.hasStatus(Stunned.class)) {
            return new StunnedAction();
        }
        return null;
    }
}
