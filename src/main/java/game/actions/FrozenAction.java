package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.status.Frozen;

import java.util.List;

/**
 * Represents an {@link Action} that prevents an {@link Actor} from acting due to being frozen.
 * <p>
 * When executed, this action checks the {@link Frozen} status of the actor and
 * describes how many rounds the actor remains frozen. If no duration is available,
 * a fallback frozen message is returned.
 * </p>
 *
 * @author Esaam matudin
 * @version 1.0
 */
public class FrozenAction extends Action {

    /**
     * Executes the frozen action, preventing the actor from taking any turn-based action.
     * <p>
     * If the actor has a {@link Frozen} status, the duration of the freeze is displayed
     * in the result. Otherwise, a generic frozen message is shown.
     * </p>
     *
     * @param actor the actor that is frozen
     * @param map   the map in which the action occurs
     * @return a descriptive string of the action’s result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Retrieve the Frozen status from the actor
        List<Frozen> frozenList = actor.statusesOf(Frozen.class);
        if (!frozenList.isEmpty()) {
            Frozen frozen = frozenList.get(0); // Assume only one Frozen status
            return actor + " is frozen solid for " + frozen.getDuration() + " round(s)";
        }
        // Fallback if no explicit Frozen status duration is found
        return actor + " is frozen solid";
    }

    /**
     * Provides a short description for menus and UI when the actor is frozen.
     *
     * @param actor the actor performing the action
     * @return a string describing the frozen state
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " is frozen solid";
    }
}
