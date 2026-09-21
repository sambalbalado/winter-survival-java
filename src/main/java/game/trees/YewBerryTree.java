package game.trees;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.YewBerry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Yew Berry Tree that drops poisonous Yew Berries every 5 turns.
 * <p>
 * The tree randomly places a Yew Berry in an adjacent location if the ground
 * allows actors to enter it. Consuming these berries is fatal to actors.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class YewBerryTree extends Ground {

    /** Counter to track turns for berry dropping */
    private int turnCounter = 0;

    /**
     * Constructor for YewBerryTree.
     * Sets the display character and name.
     */
    public YewBerryTree() {
        super('Y', "Yew Berry Tree"); // 'Y' represents the tree
    }

    @Override
    public void tick(Location location) {
        super.tick(location);
        turnCounter++;

        if (turnCounter >= 5) { // drop every 5 turns
            dropYewBerry(location);
            turnCounter = 0;
        }
    }

    /**
     * Drops a Yew Berry in the first available adjacent location.
     *
     * @param location the location of the tree
     */
    private void dropYewBerry(Location location) {
        List<Exit> exits = new ArrayList<>(location.getExits());
        Collections.shuffle(exits);

        for (Exit exit : exits) {
            Location adjacent = exit.getDestination();
            if (adjacent.getGround().canActorEnter(null)) {
                adjacent.addItem(new YewBerry());
                break; // drop one berry per tick
            }
        }
    }
}
