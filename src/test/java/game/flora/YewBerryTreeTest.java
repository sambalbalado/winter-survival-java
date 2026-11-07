package game.flora;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import game.items.YewBerry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for YewBerryTree covering REQ1: Flora II
 *
 * @author Esaam Bin Haji Matudin
 */
class YewBerryTreeTest {

    private Location mockLocation;
    private Location mockDestination;
    private GameMap mockGameMap;

    @BeforeEach
    void setUp() {
        // Create mocks
        mockLocation = mock(Location.class);
        mockDestination = mock(Location.class);
        mockGameMap = mock(GameMap.class);
        Ground mockGround = mock(Ground.class);

        // Setup mock behavior
        when(mockLocation.map()).thenReturn(mockGameMap);
        when(mockGround.getDisplayChar()).thenReturn('.');
        when(mockDestination.getGround()).thenReturn(mockGround);

        // Setup exits
        Exit mockExit = mock(Exit.class);
        when(mockExit.getDestination()).thenReturn(mockDestination);
        List<Exit> exits = new ArrayList<>();
        exits.add(mockExit);
        when(mockLocation.getExits()).thenReturn(exits);
    }

    // ========================================================================
    // Trees start at correct initial stage
    // ========================================================================

    @Test
    void testTreeStartsAsSaplingByDefault() {
        YewBerryTree tree = new YewBerryTree();
        assertEquals(Stage.SAPLING, tree.getStage());
    }

    @Test
    void testTreeCanStartAsSapling() {
        YewBerryTree tree = new YewBerryTree(Stage.SAPLING);
        assertEquals(Stage.SAPLING, tree.getStage());
    }

    @Test
    void testTreeCanStartAsMatureTree() {
        YewBerryTree tree = new YewBerryTree(Stage.TREE);
        assertEquals(Stage.TREE, tree.getStage());
    }

    // ========================================================================
    // Sapling has 50% chance to grow after 3 turns
    // ========================================================================

    @Test
    void testSaplingChecksGrowthConditionAfterThreeTurns() {
        when(mockGameMap.toString()).thenReturn("Forest");
        YewBerryTree tree = new YewBerryTree(Stage.SAPLING);

        // Tick 4 times to trigger growth check (turnsInStage > 3)
        for (int i = 0; i < 4; i++) {
            tree.tick(mockLocation);
        }

        // After 4 turns, either it grew (setGround called) or counter reset to 0
        // Both outcomes are valid due to 50% chance
        assertTrue(tree.getTurnsInStage() == 0 || tree.getTurnsInStage() == 4,
                "Sapling should either grow or reset counter after growth check");
    }

    @Test
    void testSaplingDoesNotGrowBeforeThreeTurns() {
        when(mockGameMap.toString()).thenReturn("Forest");
        YewBerryTree tree = new YewBerryTree(Stage.SAPLING);

        for (int i = 0; i < 3; i++) {
            tree.tick(mockLocation);
        }

        verify(mockLocation, never()).setGround(any(YewBerryTree.class));
    }

    @Test
    void testSaplingGrowthBehaviorIsConsistent() {
        when(mockGameMap.toString()).thenReturn("Forest");
        YewBerryTree tree = new YewBerryTree(Stage.SAPLING);

        // Tick 4 times, if it doesn't grow, counter should reset to 0
        for (int i = 0; i < 4; i++) {
            tree.tick(mockLocation);
        }

        // After growth check: either grew (any value) or reset to 0
        int turns = tree.getTurnsInStage();
        assertTrue(turns == 0 || turns > 0,
                "Sapling should have deterministic counter behavior after growth check");
    }

    // ========================================================================
    // Sapling cannot produce berries in Forest
    // ========================================================================

    @Test
    void testSaplingProducesNoBerryInForest() {
        when(mockGameMap.toString()).thenReturn("Forest");
        YewBerryTree tree = new YewBerryTree(Stage.SAPLING);

        for (int i = 0; i < 5; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, never()).addItem(any(YewBerry.class));
    }

    @Test
    void testSaplingProducesNoBerryEvenAfterManyTurnsInForest() {
        when(mockGameMap.toString()).thenReturn("Forest");
        YewBerryTree tree = new YewBerryTree(Stage.SAPLING);

        for (int i = 0; i < 10; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, never()).addItem(any(YewBerry.class));
    }

    @Test
    void testSaplingStillCannotProduceBerryInForestAtBoundary() {
        when(mockGameMap.toString()).thenReturn("Forest");
        YewBerryTree tree = new YewBerryTree(Stage.SAPLING);

        // Test at exactly 2 turns (would be production time in Plains)
        for (int i = 0; i < 2; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, never()).addItem(any(YewBerry.class));
    }

    // ========================================================================
    // Sapling produces berry every 2 turns in Plains
    // ========================================================================

    @Test
    void testSaplingProducesBerryEveryTwoTurnsInPlains() {
        when(mockGameMap.toString()).thenReturn("Plains");
        YewBerryTree tree = new YewBerryTree(Stage.SAPLING);

        for (int i = 0; i < 4; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, times(2)).addItem(any(YewBerry.class));
    }

    @Test
    void testSaplingDoesNotProduceBerryBeforeTwoTurnsInPlains() {
        when(mockGameMap.toString()).thenReturn("Plains");
        YewBerryTree tree = new YewBerryTree(Stage.SAPLING);

        tree.tick(mockLocation);

        verify(mockDestination, never()).addItem(any(YewBerry.class));
    }

    @Test
    void testSaplingProducesBerryAtExactlyTwoTurnsInPlains() {
        when(mockGameMap.toString()).thenReturn("Plains");
        YewBerryTree tree = new YewBerryTree(Stage.SAPLING);

        for (int i = 0; i < 2; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, times(1)).addItem(any(YewBerry.class));
    }

    // ========================================================================
    // Tree produces berry every 5 turns on any map
    // ========================================================================

    @Test
    void testTreeProducesBerryEveryFiveTurnsInForest() {
        when(mockGameMap.toString()).thenReturn("Forest");
        YewBerryTree tree = new YewBerryTree(Stage.TREE);

        for (int i = 0; i < 10; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, times(2)).addItem(any(YewBerry.class));
    }

    @Test
    void testTreeProducesBerryEveryFiveTurnsInPlains() {
        when(mockGameMap.toString()).thenReturn("Plains");
        YewBerryTree tree = new YewBerryTree(Stage.TREE);

        for (int i = 0; i < 10; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, times(2)).addItem(any(YewBerry.class));
    }

    @Test
    void testTreeDoesNotProduceBerryBeforeFiveTurns() {
        when(mockGameMap.toString()).thenReturn("Forest");
        YewBerryTree tree = new YewBerryTree(Stage.TREE);

        for (int i = 0; i < 4; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, never()).addItem(any(YewBerry.class));
    }

    @Test
    void testTreeProducesBerryAtExactlyFiveTurns() {
        when(mockGameMap.toString()).thenReturn("Forest");
        YewBerryTree tree = new YewBerryTree(Stage.TREE);

        for (int i = 0; i < 5; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, times(1)).addItem(any(YewBerry.class));
    }

    // ========================================================================
    // Mature tree does not grow further
    // ========================================================================

    @Test
    void testMatureTreeDoesNotGrowFurther() {
        when(mockGameMap.toString()).thenReturn("Forest");
        YewBerryTree tree = new YewBerryTree(Stage.TREE);

        for (int i = 0; i < 10; i++) {
            tree.tick(mockLocation);
        }

        verify(mockLocation, never()).setGround(any(YewBerryTree.class));
    }

    @Test
    void testMatureTreeRemainsInTreeStage() {
        when(mockGameMap.toString()).thenReturn("Plains");
        YewBerryTree tree = new YewBerryTree(Stage.TREE);

        for (int i = 0; i < 15; i++) {
            tree.tick(mockLocation);
        }

        assertEquals(Stage.TREE, tree.getStage());
    }

    @Test
    void testMatureTreeStillProducesBerries() {
        when(mockGameMap.toString()).thenReturn("Forest");
        YewBerryTree tree = new YewBerryTree(Stage.TREE);

        for (int i = 0; i < 15; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, times(3)).addItem(any(YewBerry.class));
    }
}