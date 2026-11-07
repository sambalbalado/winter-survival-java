package game.flora;

import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import game.items.Apple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WildAppleTree covering REQ1: Flora II
 *
 * @author Esaam Bin Haji Matudin
 */
class WildAppleTreeTest {

    private Location mockLocation;
    private Location mockDestination;
    private GameMap mockGameMap;
    private Ground mockGround;

    @BeforeEach
    void setUp() {
        // Create mocks
        mockLocation = mock(Location.class);
        mockDestination = mock(Location.class);
        mockGameMap = mock(GameMap.class);
        mockGround = mock(Ground.class);

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
    // REQ1: Trees start at correct initial stage
    // ========================================================================

    @Test
    void testTreeStartsAsSproutByDefault() {
        WildAppleTree tree = new WildAppleTree();
        assertEquals(Stage.SPROUT, tree.getStage());
    }

    @Test
    void testTreeCanStartAsSapling() {
        WildAppleTree tree = new WildAppleTree(Stage.SAPLING);
        assertEquals(Stage.SAPLING, tree.getStage());
    }

    @Test
    void testTreeCanStartAsMatureTree() {
        WildAppleTree tree = new WildAppleTree(Stage.TREE);
        assertEquals(Stage.TREE, tree.getStage());
    }

    // ========================================================================
    // REQ2: Sprout grows to Sapling in Forest after 3 turns
    // ========================================================================

    @Test
    void testSproutGrowsToSaplingInForestAfterThreeTurns() {
        when(mockGameMap.toString()).thenReturn("Forest");
        WildAppleTree tree = new WildAppleTree(Stage.SPROUT);

        for (int i = 0; i < 4; i++) {
            tree.tick(mockLocation);
        }

        verify(mockLocation, times(1)).setGround(any(WildAppleTree.class));
    }

    @Test
    void testSproutDoesNotGrowBeforeThreeTurns() {
        when(mockGameMap.toString()).thenReturn("Forest");
        WildAppleTree tree = new WildAppleTree(Stage.SPROUT);

        for (int i = 0; i < 3; i++) {
            tree.tick(mockLocation);
        }

        verify(mockLocation, never()).setGround(any(WildAppleTree.class));
    }

    @Test
    void testSproutGrowsImmediatelyAfterThreeTurns() {
        when(mockGameMap.toString()).thenReturn("Forest");
        WildAppleTree tree = new WildAppleTree(Stage.SPROUT);

        for (int i = 0; i < 5; i++) {
            tree.tick(mockLocation);
        }

        verify(mockLocation, atLeastOnce()).setGround(any(WildAppleTree.class));
    }

    // ========================================================================
    // REQ3: Sprout skips Sapling stage in Plains
    // ========================================================================

    @Test
    void testSproutGrowsDirectlyToTreeInPlains() {
        when(mockGameMap.toString()).thenReturn("Plains");
        WildAppleTree tree = new WildAppleTree(Stage.SPROUT);

        for (int i = 0; i < 4; i++) {
            tree.tick(mockLocation);
        }

        verify(mockLocation, times(1)).setGround(any(WildAppleTree.class));
    }

    @Test
    void testSproutDoesNotGrowBeforeThreeTurnsInPlains() {
        when(mockGameMap.toString()).thenReturn("Plains");
        WildAppleTree tree = new WildAppleTree(Stage.SPROUT);

        for (int i = 0; i < 3; i++) {
            tree.tick(mockLocation);
        }

        verify(mockLocation, never()).setGround(any(WildAppleTree.class));
    }

    @Test
    void testSaplingGrowsToTreeInPlains() {
        when(mockGameMap.toString()).thenReturn("Plains");
        WildAppleTree tree = new WildAppleTree(Stage.SAPLING);

        for (int i = 0; i < 5; i++) {
            tree.tick(mockLocation);
        }

        verify(mockLocation, times(1)).setGround(any(WildAppleTree.class));
    }

    // ========================================================================
    // REQ4: Apple production rates differ by stage and map
    // ========================================================================

    @Test
    void testSproutProducesNoApplesInForest() {
        when(mockGameMap.toString()).thenReturn("Forest");
        WildAppleTree tree = new WildAppleTree(Stage.SPROUT);

        for (int i = 0; i < 5; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, never()).addItem(any(Apple.class));
    }

    @Test
    void testSproutProducesAppleEveryTurnInPlains() {
        when(mockGameMap.toString()).thenReturn("Plains");
        WildAppleTree tree = new WildAppleTree(Stage.SPROUT);

        for (int i = 0; i < 3; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, times(3)).addItem(any(Apple.class));
    }

    @Test
    void testSaplingProducesAppleEveryTwoTurns() {
        when(mockGameMap.toString()).thenReturn("Forest");
        WildAppleTree tree = new WildAppleTree(Stage.SAPLING);

        for (int i = 0; i < 4; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, times(2)).addItem(any(Apple.class));
    }

    @Test
    void testTreeProducesAppleEveryThreeTurns() {
        when(mockGameMap.toString()).thenReturn("Forest");
        WildAppleTree tree = new WildAppleTree(Stage.TREE);

        for (int i = 0; i < 6; i++) {
            tree.tick(mockLocation);
        }

        verify(mockDestination, times(2)).addItem(any(Apple.class));
    }

    // ========================================================================
    // REQ5: Sapling grows to Tree in Forest after 5 turns
    // ========================================================================

    @Test
    void testSaplingGrowsToTreeAfterFiveTurns() {
        when(mockGameMap.toString()).thenReturn("Forest");
        WildAppleTree tree = new WildAppleTree(Stage.SAPLING);

        for (int i = 0; i < 5; i++) {
            tree.tick(mockLocation);
        }

        verify(mockLocation, times(1)).setGround(any(WildAppleTree.class));
    }

    @Test
    void testSaplingDoesNotGrowBeforeFiveTurns() {
        when(mockGameMap.toString()).thenReturn("Forest");
        WildAppleTree tree = new WildAppleTree(Stage.SAPLING);

        for (int i = 0; i < 4; i++) {
            tree.tick(mockLocation);
        }

        verify(mockLocation, never()).setGround(any(WildAppleTree.class));
    }

    @Test
    void testMatureTreeDoesNotGrowFurther() {
        when(mockGameMap.toString()).thenReturn("Forest");
        WildAppleTree tree = new WildAppleTree(Stage.TREE);

        for (int i = 0; i < 10; i++) {
            tree.tick(mockLocation);
        }

        verify(mockLocation, never()).setGround(any(WildAppleTree.class));
    }
}