package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Eatable;

/**
 * An action that allows an actor to eat an {@link Eatable} item.
 * The action executes the eating behavior of the food.
 *
 * @author Esaam Matudin
 * @version 4.2
 */
public class EatAction extends Action {

    /** The food item to be eaten. */
    private final Eatable food;

    /**
     * Constructor.
     *
     * @param food the food item to be eaten
     */
    public EatAction(Eatable food) {
        this.food = food;
    }

    /**
     * Executes the eating action by the given actor.
     *
     * @param actor the actor performing the action
     * @param map   the current game map
     * @return a string describing the result of eating
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return food.eatenBy(actor);
    }

    /**
     * Returns a description of this action for the menu.
     *
     * @param actor the actor performing the action
     * @return a string describing the eating action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " eats " + food.getClass().getSimpleName();
    }
}
