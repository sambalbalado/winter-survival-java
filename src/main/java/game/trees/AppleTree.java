package game.trees;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Apple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Wild Apple Tree that drops apples every 3 turns.
 * <p>
 * The tree randomly places an apple in an adjacent location if the ground
 * allows actors to enter it.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class AppleTree extends Ground {
    /** Counter to track turns for apple dropping */
    private int turnCounter = 0;

    /**
     * Constructor for AppleTree.
     * Sets the display character and name.
     */
    public AppleTree() {
        super('T', "Wild Apple Tree");  // 'T' is the display character
    }

    @Override
    public void tick(Location location) {
        super.tick(location);
        turnCounter++;

        if (turnCounter >= 3) {  // drop apple every 3 turns
            dropApple(location);
            turnCounter = 0;
        }
    }

    /**
     * Drops an apple in the first available adjacent location.
     *
     * @param location the location of the tree
     */
    private void dropApple(Location location) {
        // Make a shuffled copy of adjacent exits for randomness
        List<Exit> exits = new ArrayList<>(location.getExits());
        Collections.shuffle(exits);

        for (Exit exit : exits) {
            Location adjacent = exit.getDestination();

            if (adjacent.getGround().canActorEnter(null)) {
                adjacent.addItem(new Apple());
                break;
            }
        }
    }
}
