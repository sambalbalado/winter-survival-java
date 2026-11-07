package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.OpenTreasureAction;

/**
 * A one-shot treasure container ground. When opened, spawns loot and disappears.
 * Display char kept simple & visible.
 */
public class TreasureBox extends Ground {
    public TreasureBox() {
        super('$', "TreasureBox"); // choose any distinct char you like
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList list = new ActionList();
        // Anyone can attempt to open; gate contents/eligibility inside the action if needed
        list.add(new OpenTreasureAction(location));
        return list;
    }
}
