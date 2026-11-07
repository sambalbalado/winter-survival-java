package game.spawning;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.GroundCreator;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Apple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DeerAppleEffect} covering REQ2: Post-Spawn Effects.
 *
 * <p>Scenarios:
 * <ul>
 *   <li>Normal: center of 3×3 has 8 exits → exactly one apple dropped</li>
 *   <li>Boundary: 1×2 left tile has 1 exit → exactly one apple dropped on that exit</li>
 *   <li>Edge: 1×1 has 0 exits → no drop, no crash</li>
 * </ul>
 * </p>
 *
 * <p>Uses a tiny real {@link GameMap} (no Mockito) for deterministic exits and tiles.</p>
 *
 * @author Sai Sann Lone
 */
class DeerAppleEffectTest {

    // ------------------------------------------------------------------------
    // Minimal test-ground setup
    // ------------------------------------------------------------------------

    static final class TestFloor extends Ground {
        TestFloor() { super('.', "TestFloor"); }
        @Override public boolean canActorEnter(edu.monash.fit2099.engine.actors.Actor actor) { return true; }
    }

    static final class TestGroundCreator implements GroundCreator {
        @Override public Ground createGround(char displayChar) { return new TestFloor(); }
        @Override public void registerGround(char displayChar, Supplier<? extends Ground> supplier) { /* no-op */ }
    }

    // ------------------------------------------------------------------------

    private DeerAppleEffect effect;

    @BeforeEach
    void setUp() {
        effect = new DeerAppleEffect();
    }

    private static int applesAround(Location origin) {
        return origin.getExits().stream()
                .mapToInt(e -> (int) e.getDestination().getItems().stream()
                        .filter(i -> i.getClass() == Apple.class).count())
                .sum();
    }

    // ========================================================================
    // Normal: center of 3×3 → 8 exits → exactly one drop
    // ========================================================================

    @Test
    void normal_centerOf3x3_dropsExactlyOne() throws GameEngineException {
        GameMap map = new GameMap("M", new TestGroundCreator(), Arrays.asList(
                "...", "...", "..."
        ));
        Location center = map.at(1, 1);              // 8 exits
        int before = applesAround(center);

        effect.apply(center, null, new Random(42L)); // seeded for determinism

        int after = applesAround(center);
        assertEquals(before + 1, after,
                "Deer should drop exactly one apple among exits (normal case).");
    }

    // ========================================================================
    // Boundary: 1×2 → left tile has 1 exit → exactly one drop on that exit
    // ========================================================================

    @Test
    void boundary_oneExit_dropsExactlyOneOnThatExit() throws GameEngineException {
        GameMap map = new GameMap("M12", new TestGroundCreator(), Arrays.asList(".."));
        Location left = map.at(0, 0);                 // exactly 1 exit
        assertEquals(1, left.getExits().size(), "setup: left tile must have exactly one exit");
        int before = applesAround(left);

        effect.apply(left, null, new Random(7L));

        int after = applesAround(left);
        assertEquals(before + 1, after,
                "Deer should still drop exactly one apple when only one exit exists (boundary).");
    }

    // ========================================================================
    // Edge: 1×1 → 0 exits → no drop, no crash
    // ========================================================================

    @Test
    void edge_noExits_noDrop_noCrash() throws GameEngineException {
        GameMap map = new GameMap("M11", new TestGroundCreator(), Arrays.asList("."));
        Location only = map.at(0, 0);                 // 0 exits
        assertTrue(only.getExits().isEmpty(), "setup: single cell has 0 exits");

        assertDoesNotThrow(() -> effect.apply(only, null, new Random(99L)));
        assertEquals(0, applesAround(only),
                "No exits → no apple should be dropped (edge).");
    }
}
