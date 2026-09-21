package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.capabilities.Drinkable;

/**
 * An action that allows an actor to drink from a {@link Drinkable} source.
 *
 * @author Esaam Matudin
 * @version 2.6
 */
public class DrinkAction extends Action {

    /**
     * The drinkable source.
     */
    private final Drinkable source;

    /**
     * Constructor.
     *
     * @param source the drinkable source
     */
    public DrinkAction(Drinkable source) {
        this.source = source;
    }

    /**
     * Executes the drinking action by the given actor.
     *
     * @param actor the actor performing the action
     * @param map   the current game map
     * @return a string describing the result of drinking
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        return source.drunkBy(actor);
    }

    /**
     * Returns a description of this action for the menu.
     *
     * @param actor the actor performing the action
     * @return a string describing the drinking action
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " drinks from " + source.getClass().getSimpleName();
    }
}
