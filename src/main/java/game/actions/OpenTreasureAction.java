package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.items.Item;
import game.app.Abilities;
import game.environment.Dirt;
import game.items.SpeedBoostPotion;


public class OpenTreasureAction extends Action {
    private final Location boxLoc;

    public OpenTreasureAction(Location boxLoc) { this.boxLoc = boxLoc; }

    @Override
    public String execute(Actor actor, GameMap map) {
        // Gate on morph/ability if required
        if (!actor.hasAbility(Abilities.FORM_WOLF)) {
            return actor + " lacks the required form to open this treasure.";
        }

        // Create portable loot and grant it directly
        Item loot = new SpeedBoostPotion(5); // ensure portable=true in its ctor
        actor.addItemToInventory(loot);

        // Remove/transform the box so it can't be opened again
        boxLoc.setGround(new Dirt());
        return actor + " opens the treasure box and receives " + loot + "!";
    }

    @Override
    public String menuDescription(Actor actor) {
        return actor + " Open the treasure box";
    }
}
