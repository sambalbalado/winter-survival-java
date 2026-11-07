package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.status.Stunned;

import java.util.List;

/**
 * Represents an {@link Action} where an {@link Actor} is stunned
 * and unable to perform any meaningful action.
 * <p>
 * When executed, the actor takes damage from being stunned
 * and a message is displayed indicating the stun duration.
 * If no duration is found, a fallback message is used instead.
 * </p>
 *
 * @author Esaam matudin
 * @version 1.0
 */
public class StunnedAction extends Action {

    /**
     * Executes the stunned action by applying damage to the actor and
     * returning a message describing the stunned condition.
     * <p>
     * If the actor has a {@link Stunned} status, its duration is displayed.
     * Otherwise, a default stunned message is returned.
     * </p>
     *
     * @param actor the actor that is stunned
     * @param map   the game map where the action takes place
     * @return a descriptive string of the action’s result
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Retrieve the Stunned status from the actor
        List<Stunned> stunnedList = actor.statusesOf(Stunned.class);
        if (!stunnedList.isEmpty()) {
            // Apply damage due to being stunned
            actor.hurt(7);
            Stunned stunned = stunnedList.get(0); // Assume one stunned status
            return actor + " is stunned and cannot move for " + stunned.getDuration() + " round(s)";
        }
        // Fallback if no explicit Stunned status duration is found
        return actor + " is stunned and cannot move";
    }

    /**
     * Provides the menu description for this stunned action.
     *
     * @param actor the actor performing the action
     * @return a short string describing the stunned condition
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " is stunned and cannot move";
    }
}
