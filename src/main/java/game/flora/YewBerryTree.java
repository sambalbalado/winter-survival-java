package game.flora;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.TreeBehaviour;
import game.items.YewBerry;

import java.util.List;
import java.util.Random;

/**
 * A Yew Berry Tree that grows from a sapling into a mature tree
 * and drops yew berries based on its stage and the map it is located on.
 *
 * <p>
 * Growth rules:
 * <ul>
 *     <li>Sapling: 50% chance to grow into Tree every 3 turns</li>
 *     <li>Tree: Fully grown, does not grow further</li>
 * </ul>
 * Yew berry drop rules:
 * <ul>
 *     <li>Sapling: Forest (cannot drop), Plains (every 2 turns)</li>
 *     <li>Tree: Drops every 5 turns</li>
 * </ul>
 * </p>
 *
 * @author Taabish Farooq Bhat (A1) & Esaam Matudin (A3)
 * @version 3.0
 */
public class YewBerryTree extends Tree {

    private final Random random = new Random();
    private int fruitDropCounter = 0;

    /**
     * Constructs a YewBerryTree with a specific growth stage.
     *
     * @param stage the initial stage of the tree
     */
    public YewBerryTree(Stage stage) {
        super(getDisplayCharForStage(stage), getNameForStage(stage), stage);

        // Add behaviours such as growing and dropping yew berries
        addBehaviour(10, new GrowthBehaviour());
        addBehaviour(20, new YewBerryDropBehaviour());
    }

    /**
     * Constructs a YewBerryTree starting at the SAPLING stage.
     */
    public YewBerryTree() {
        this(Stage.SAPLING);
    }

    /**
     * Behaviour that handles growth for Yew Berry Trees.
     */
    private class GrowthBehaviour implements TreeBehaviour {
        @Override
        public void perform(Tree tree, Location location) {
            if (stage == Stage.SAPLING) {
                if (turnsInStage > 3) {
                    // 50% chance to grow
                    if (random.nextDouble() < 0.5) {
                        YewBerryTree newTree = new YewBerryTree(Stage.TREE);
                        newTree.fruitDropCounter = fruitDropCounter;
                        location.setGround(newTree);
                    } else {
                        // Reset counter to check again in 3 turns
                        turnsInStage = 0;
                    }
                }
            }
            // Trees don't grow further
        }
    }

    /**
     * Behaviour that handles yew berry dropping.
     */
    private class YewBerryDropBehaviour implements TreeBehaviour {
        @Override
        public void perform(Tree tree, Location location) {
            String mapName = getMapName(location);
            boolean canProduce = false;
            int dropInterval = Integer.MAX_VALUE;

            // Determine fruit production rules based on map and stage
            if ("Forest".equals(mapName)) {
                if (stage == Stage.SAPLING) {
                    canProduce = false;
                } else if (stage == Stage.TREE) {
                    canProduce = true;
                    dropInterval = 5;
                }
            } else if ("Plains".equals(mapName)) {
                if (stage == Stage.SAPLING) {
                    canProduce = true;
                    dropInterval = 2;
                } else if (stage == Stage.TREE) {
                    canProduce = true;
                    dropInterval = 5;
                }
            }
            // For new maps, can add here using else-if

            if (!canProduce) {
                return;
            }

            fruitDropCounter++;
            if (fruitDropCounter >= dropInterval) {
                dropYewBerry(location);
                fruitDropCounter = 0;
            }
        }

        private void dropYewBerry(Location location) {
            List<Exit> exits = location.getExits();
            if (!exits.isEmpty()) {
                Exit randomExit = exits.get(random.nextInt(exits.size()));
                Location destination = randomExit.getDestination();
                if (destination.getGround().getDisplayChar() == '.') {
                    destination.addItem(new YewBerry());
                }
            }
        }
    }

    private static char getDisplayCharForStage(Stage stage) {
        return switch (stage) {
            case SPROUT -> '.'; // Change if YewBerryTree sprout exist
            case SAPLING -> 'b';
            case TREE -> 'Y';
        };
    }

    private static String getNameForStage(Stage stage) {
        return switch (stage) {
            case SPROUT -> "Yew Berry Sprout";
            case SAPLING -> "Yew Berry Sapling";
            case TREE -> "Yew Berry Tree";
        };
    }
}
