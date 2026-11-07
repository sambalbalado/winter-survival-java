package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.Bottle;
import game.items.Drinkable;

/**
 * An action that allows an actor to drink from a bottle.

 * This action handles the drinking mechanic where an actor attempts to consume
 * liquid from a bottle. The action checks if the bottle contains any liquid
 * before allowing the drinking to proceed. If the bottle is empty, the action
 * will fail with an appropriate message.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class DrinkBottleAction extends Action {
    private final Bottle bottle;
    private final Drinkable drinker;

    /**
     * Constructor for creating a DrinkBottleAction.
     *
     * @param bottle the Bottle that will be consumed from
     * @param drinker the Drinkable interface that handles the drinking effects
     */
    public DrinkBottleAction(Bottle bottle, Drinkable drinker) {
        this.bottle = bottle;
        this.drinker = drinker;
    }

    /**
     * Executes the drink bottle action.
     *
     * First checks if the bottle contains any liquid. If the bottle is empty,
     * returns a message indicating the failure. If the bottle contains liquid,
     * the drinking is processed through the bottle's drunkBy method which applies
     * the appropriate effects to the drinker.
     *
     * @param actor the Actor attempting to drink from the bottle
     * @param map the GameMap where the action takes place
     * @return a String describing the result of the drinking attempt
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (bottle.isEmpty()) return actor + " tries to drink, but the bottle is empty.";
        return bottle.drunkBy(drinker);
    }

    /**
     * Returns a description of this action suitable for display in a menu.
     *
     * @param actor the Actor who would perform this action
     * @return a String describing the drinking action in a user-friendly format
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " drinks from the bottle";
    }
}
