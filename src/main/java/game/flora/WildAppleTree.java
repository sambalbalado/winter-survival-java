package game.flora;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.TreeBehaviour;
import game.items.Apple;

import java.util.List;
import java.util.Random;

/**
 * A Wild Apple Tree that grows through different stages (Sprout, Sapling, Tree)
 * and drops apples based on its stage and the map it is located on.
 *
 * <p>
 * Growth rules:
 * <ul>
 *     <li>Forest: Sprout (&quot;,&quot;) → Sapling (&quot;t&quot;) → Tree (&quot;T&quot;)</li>
 *     <li>Plains: Sprout (&quot;,&quot;) → Tree (&quot;T&quot;) (skips Sapling)</li>
 * </ul>
 * Apple drop rules:
 * <ul>
 *     <li>Sprout: Only in Plains, every turn</li>
 *     <li>Sapling: Every 2 turns (Forest only)</li>
 *     <li>Tree: Every 3 turns</li>
 * </ul>
 * </p>
 *
 * @author Taabish Farooq Bhat (A1) & Esaam Matudin (A3)
 *
 * @version 2.0
 */
public class WildAppleTree extends Tree {

    private final Random random = new Random();
    private int fruitDropCounter = 0;

    /**
     * Constructs a WildAppleTree with a specific growth stage.
     *
     * @param stage the initial stage of the tree (SPROUT, SAPLING, TREE)
     */
    public WildAppleTree(Stage stage) {
        super(getDisplayCharForStage(stage), getNameForStage(stage), stage); // default drop interval; updated dynamically

        // Add behaviours such as growing and dropping apples
        addBehaviour(10, new GrowthBehaviour());
        addBehaviour(20, new AppleDropBehaviour());
    }

    /**
     * Constructs a WildAppleTree starting at the SPROUT stage.
     */
    public WildAppleTree() {
        this(Stage.SPROUT);
    }

    /**
     * Behaviour that handles growth for Wild Apple Trees.
     */
    private class GrowthBehaviour implements TreeBehaviour {
        @Override
        public void perform(Tree tree, Location location) {
            String mapName = getMapName(location);

            switch (stage) {
                case SPROUT:
                    if (turnsInStage > 3) {
                        WildAppleTree newTree;
                        if ("Plains".equals(mapName)) {
                            newTree = new WildAppleTree(Stage.TREE);
                        } else {
                            newTree = new WildAppleTree(Stage.SAPLING);
                        }
                        newTree.fruitDropCounter = fruitDropCounter;
                        location.setGround(newTree);
                    }
                    break;

                case SAPLING:
                    if (turnsInStage >= 5) {
                        WildAppleTree newTree = new WildAppleTree(Stage.TREE);
                        newTree.fruitDropCounter = fruitDropCounter;
                        location.setGround(newTree);
                    }
                    break;

                case TREE:
                    // No further growth
                    break;
            }
        }
    }

    /**
     * Behaviour that handles apple dropping for Wild Apple Trees.
     */
    private class AppleDropBehaviour implements TreeBehaviour {
        @Override
        public void perform(Tree tree, Location location) {
            String mapName = getMapName(location);
            boolean canProduce = false;
            int dropInterval = Integer.MAX_VALUE;

            // Determine fruit production rules based on map and stage
            if ("Forest".equals(mapName)) {
                if (stage == Stage.SPROUT) {
                    canProduce = false;
                } else if (stage == Stage.SAPLING) {
                    canProduce = true;
                    dropInterval = 2;
                } else if (stage == Stage.TREE) {
                    canProduce = true;
                    dropInterval = 3;
                }
            } else if ("Plains".equals(mapName)) {
                if (stage == Stage.SPROUT) {
                    canProduce = true;
                    dropInterval = 1;
                } else if (stage == Stage.SAPLING) {
                    canProduce = true;
                    dropInterval = 2;
                } else if (stage == Stage.TREE) {
                    canProduce = true;
                    dropInterval = 3;
                }
            }
            // For new maps, can add here using else-if

            if (!canProduce) {
                return;
            }

            fruitDropCounter++;
            if (fruitDropCounter >= dropInterval) {
                dropApple(location);
                fruitDropCounter = 0;
            }
        }

        private void dropApple(Location location) {
            List<Exit> exits = location.getExits();
            if (!exits.isEmpty()) {
                Exit randomExit = exits.get(random.nextInt(exits.size()));
                Location destination = randomExit.getDestination();
                if (destination.getGround().getDisplayChar() == '.') {
                    destination.addItem(new Apple());
                }
            }
        }
    }

    /**
     * Returns the appropriate display character for a given stage.
     *
     * @param stage the growth stage
     * @return the character to display on the map
     */
    private static char getDisplayCharForStage(Stage stage) {
        return switch (stage) {
            case SPROUT -> ',';
            case SAPLING -> 't';
            case TREE -> 'T';
        };
    }

    /**
     * Returns the appropriate name for the tree based on its stage.
     *
     * @param stage the growth stage
     * @return the name of the tree at this stage
     */
    private static String getNameForStage(Stage stage) {
        return switch (stage) {
            case SPROUT -> "Wild Apple Sprout";
            case SAPLING -> "Wild Apple Sapling";
            case TREE -> "Wild Apple Tree";
        };
    }
}
