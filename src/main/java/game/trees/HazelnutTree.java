package game.trees;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Hazelnut;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Hazelnut Tree that drops hazelnuts every 10 turns.
 * <p>
 * The tree randomly places a hazelnut in an adjacent location if the ground
 * allows actors to enter it.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class HazelnutTree extends Ground {

    /** Counter to track turns for hazelnut dropping */
    private int turnCounter = 0;

    /**
     * Constructor for HazelnutTree.
     * Sets the display character and name.
     */
    public HazelnutTree() {
        super('A', "Hazelnut Tree");  // 'A' is the display character
    }

    @Override
    public void tick(Location location) {
        super.tick(location);
        turnCounter++;

        if (turnCounter >= 10) {  // drop hazelnut every 10 turns
            dropHazelnut(location);
            turnCounter = 0;
        }
    }

    /**
     * Drops a hazelnut in the first available adjacent location.
     *
     * @param location the location of the tree
     */
    private void dropHazelnut(Location location) {
        // Make a shuffled copy of adjacent exits for randomness
        List<Exit> exits = new ArrayList<>(location.getExits());
        Collections.shuffle(exits);

        for (Exit exit : exits) {
            Location adjacent = exit.getDestination();

            if (adjacent.getGround().canActorEnter(null)) {
                adjacent.addItem(new Hazelnut());
                break;
            }
        }
    }
}
