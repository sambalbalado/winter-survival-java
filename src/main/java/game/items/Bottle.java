package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.DrinkBottleAction;
import game.app.Abilities;

/**
 * A bottle that contains water and can be consumed for hydration.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class Bottle extends Item {
    private int sips = 5;
    private Drinkable assignedDrinker;

    /**
     * Constructs a Bottle with 5 sips of water.
     */
    public Bottle() {
        super("Bottle", 'o', true);
    }

    /**
     * Allows a drinkable actor to consume water from the bottle.
     *
     * @param drinkable the actor drinking from the bottle
     * @return message describing the drink action
     */
    public String drunkBy(Drinkable drinkable) {
        if (sips > 0) {
            sips--;
            drinkable.increaseHydration(4);
            return drinkable + " drinks from the bottle. Hydration +4. Sips left: " + sips;
        }
        return "Bottle is empty!";
    }

    /**
     * Sets the assigned drinker for this bottle.
     *
     * @param drinker the drinkable actor assigned to this bottle
     */
    public void setDrinker(Drinkable drinker) {
        this.assignedDrinker = drinker;
    }

    /**
     * Returns available actions for this bottle.
     *
     * @param owner the actor who owns the bottle
     * @param map the game map
     * @return list of allowable actions
     */
    @Override
    public ActionList allowableActions(Actor owner, GameMap map) {
        ActionList actions = new ActionList();
        if (!isEmpty() && owner.hasAbility(Abilities.CAN_DRINK) && assignedDrinker != null) {
            actions.add(new DrinkBottleAction(this, assignedDrinker));
        }
        return actions;
    }

    /**
     * Checks if the bottle is empty.
     *
     * @return true if the bottle has no sips left, false otherwise
     */
    public boolean isEmpty() {
        return sips <= 0;
    }
}
