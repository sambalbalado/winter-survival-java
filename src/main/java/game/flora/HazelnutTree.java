package game.flora;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.TreeBehaviour;
import game.items.Hazelnut;

import java.util.List;
import java.util.Random;

/**
 * A Hazelnut Tree that produces hazelnuts using behaviours.
 *
 * <p>
 * Growth rules:
 * <ul>
 *     <li>Currently fixed at TREE stage; does not grow further</li>
 * </ul>
 * Hazelnut drop rules:
 * <ul>
 *     <li>Drops hazelnuts every 10 turns in adjacent dirt/snow ground locations</li>
 * </ul>
 * </p>
 *
 * @author Taabish Farooq Bhat (A1) & Esaam Matudin (A3)
 * @version 3.0
 */
public class HazelnutTree extends Tree {

    private final Random random = new Random();
    private int fruitDropCounter = 0;

    /**
     * Constructs a HazelnutTree starting at the TREE stage.
     */
    public HazelnutTree() {
        super('A', "Hazelnut Tree", Stage.TREE);

        // Add behaviours
        addBehaviour(20, new HazelnutDropBehaviour());
    }

    /**
     * Behaviour that handles hazelnut dropping.
     */
    private class HazelnutDropBehaviour implements TreeBehaviour {
        @Override
        public void perform(Tree tree, Location location) {
            // Hazelnut trees always produce at TREE stage
            int dropInterval = 10;

            fruitDropCounter++;
            if (fruitDropCounter >= dropInterval) {
                dropHazelnut(location);
                fruitDropCounter = 0;
            }
        }

        private void dropHazelnut(Location location) {
            List<Exit> exits = location.getExits();
            if (!exits.isEmpty()) {
                Exit randomExit = exits.get(random.nextInt(exits.size()));
                Location destination = randomExit.getDestination();
                if (destination.getGround().getDisplayChar() == '.') {
                    destination.addItem(new Hazelnut());
                }
            }
        }
    }
}
