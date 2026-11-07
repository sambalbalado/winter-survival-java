package game.spawning;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.GroundCreator;
import edu.monash.fit2099.engine.positions.Location;
import game.items.YewBerry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BearBerryScatterEffect} covering REQ2: Post-Spawn Effects.
 *
 * <p>Scenarios:
 * <ul>
 *   <li>Normal: center of 3×3 has 8 exits → berries dropped equals seeded expectation</li>
 *   <li>Boundary: 1×2 left tile has 1 exit → 0 or 1 berry based on seed</li>
 *   <li>Edge: 1×1 has 0 exits → no drop, no crash</li>
 * </ul>
 * </p>
 *
 * <p>Uses a tiny real {@link GameMap} (no Mockito) for deterministic exits and tiles.</p>
 *
 * @author Sai Sann Lone
 */
class BearBerryScatterEffectTest {

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

    private BearBerryScatterEffect effect;

    @BeforeEach
    void setUp() {
        effect = new BearBerryScatterEffect();
    }

    private static int berriesAround(Location origin) {
        return origin.getExits().stream()
                .mapToInt(e -> (int) e.getDestination().getItems().stream()
                        .filter(i -> i.getClass() == YewBerry.class).count())
                .sum();
    }

    /** Computes expected drops for a given number of trials with p=0.5 using the same seed. */
    private static int expectedDrops(Random rng, int times) {
        int c = 0;
        for (int i = 0; i < times; i++) if (rng.nextDouble() <= 0.5) c++;
        return c;
    }

    // ========================================================================
    // Normal: center of 3×3 → 8 exits → expected number of berries (seeded)
    // ========================================================================

    @Test
    void normal_centerOf3x3_seeded_expectedNumberOfBerries() throws GameEngineException {
        GameMap map = new GameMap("M", new TestGroundCreator(), Arrays.asList(
                "...", "...", "..."
        ));
        Location center = map.at(1, 1);                  // 8 exits

        int before = berriesAround(center);
        Random seed = new Random(1234L);
        int expected = expectedDrops(new Random(1234L), center.getExits().size());

        effect.apply(center, null, seed);

        int after = berriesAround(center);
        assertEquals(before + expected, after,
                "Should drop exactly expected number of berries (normal).");
    }

    // ========================================================================
    // Boundary: 1×2 → left tile has 1 exit → expected 0 or 1 (seeded)
    // ========================================================================

    @Test
    void boundary_oneExit_seeded_expected0or1() throws GameEngineException {
        GameMap map = new GameMap("M12", new TestGroundCreator(), Arrays.asList(".."));
        Location left = map.at(0, 0);                      // exactly 1 exit
        assertEquals(1, left.getExits().size(), "setup: left tile must have exactly one exit");

        int before = berriesAround(left);
        Random seed = new Random(2025L);
        int expected = expectedDrops(new Random(2025L), left.getExits().size()); // 0 or 1

        effect.apply(left, null, seed);

        int after = berriesAround(left);
        assertEquals(before + expected, after,
                "Boundary (1 exit): drop must match seeded expectation (0 or 1).");
    }

    // ========================================================================
    // Edge: 1×1 → 0 exits → no drop, no crash
    // ========================================================================

    @Test
    void edge_noExits_noDrop_noCrash() throws GameEngineException {
        GameMap map = new GameMap("M11", new TestGroundCreator(), Arrays.asList("."));
        Location only = map.at(0, 0);                      // 0 exits
        assertTrue(only.getExits().isEmpty(), "setup: single cell has 0 exits");

        assertDoesNotThrow(() -> effect.apply(only, null, new Random(9L)));
        assertEquals(0, berriesAround(only),
                "No exits → no berries dropped (edge).");
    }
}
