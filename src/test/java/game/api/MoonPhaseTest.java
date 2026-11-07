package game.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MoonPhase enum and API value conversion
 * Tests the fromApiValue method with various boundary and edge cases
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
class MoonPhaseTest {


    @Test
    @DisplayName("Test 1: API value 0.0 converts to NEW_MOON (boundary)")
    void testApiValueZeroIsNewMoon() {
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(0.0),
                "API value 0.0 should map to NEW_MOON");
    }

    @Test
    @DisplayName("Test 2: API value 1.0 converts to NEW_MOON (boundary)")
    void testApiValueOneIsNewMoon() {
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(1.0),
                "API value 1.0 should map to NEW_MOON");
    }

    @Test
    @DisplayName("Test 3: API value 0.5 converts to FULL_MOON (typical)")
    void testApiValueHalfIsFullMoon() {
        assertEquals(MoonPhase.FULL_MOON, MoonPhase.fromApiValue(0.5),
                "API value 0.5 should map to FULL_MOON");
    }

    @Test
    @DisplayName("Test 4: API value 0.25 converts to NORMAL (typical)")
    void testApiValueQuarterIsNormal() {
        assertEquals(MoonPhase.NORMAL, MoonPhase.fromApiValue(0.25),
                "API value 0.25 should map to NORMAL");
    }

    @Test
    @DisplayName("Test 5: API value 0.75 converts to NORMAL (typical)")
    void testApiValueThreeQuartersIsNormal() {
        assertEquals(MoonPhase.NORMAL, MoonPhase.fromApiValue(0.75),
                "API value 0.75 should map to NORMAL");
    }

    @Test
    @DisplayName("Test 6: NEW_MOON range boundaries (0.0-0.15)")
    void testNewMoonLowerBoundary() {
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(0.0));
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(0.05));
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(0.10));
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(0.15),
                "Values up to 0.15 should be NEW_MOON");
    }

    @Test
    @DisplayName("Test 7: NEW_MOON range boundaries (0.85-1.0)")
    void testNewMoonUpperBoundary() {
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(0.85),
                "Value 0.85 should be NEW_MOON");
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(0.90));
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(0.95));
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(1.0));
    }

    @Test
    @DisplayName("Test 8: FULL_MOON range boundaries (0.40-0.60)")
    void testFullMoonBoundaries() {
        assertEquals(MoonPhase.FULL_MOON, MoonPhase.fromApiValue(0.40),
                "Value 0.40 should be FULL_MOON");
        assertEquals(MoonPhase.FULL_MOON, MoonPhase.fromApiValue(0.45));
        assertEquals(MoonPhase.FULL_MOON, MoonPhase.fromApiValue(0.50));
        assertEquals(MoonPhase.FULL_MOON, MoonPhase.fromApiValue(0.55));
        assertEquals(MoonPhase.FULL_MOON, MoonPhase.fromApiValue(0.60),
                "Value 0.60 should be FULL_MOON");
    }

    @Test
    @DisplayName("Test 9: NORMAL phase for values outside NEW_MOON and FULL_MOON ranges")
    void testNormalPhaseRanges() {
        assertEquals(MoonPhase.NORMAL, MoonPhase.fromApiValue(0.16),
                "Value just after NEW_MOON lower bound");
        assertEquals(MoonPhase.NORMAL, MoonPhase.fromApiValue(0.20));
        assertEquals(MoonPhase.NORMAL, MoonPhase.fromApiValue(0.30));
        assertEquals(MoonPhase.NORMAL, MoonPhase.fromApiValue(0.39),
                "Value just before FULL_MOON lower bound");
        assertEquals(MoonPhase.NORMAL, MoonPhase.fromApiValue(0.61),
                "Value just after FULL_MOON upper bound");
        assertEquals(MoonPhase.NORMAL, MoonPhase.fromApiValue(0.70));
        assertEquals(MoonPhase.NORMAL, MoonPhase.fromApiValue(0.84),
                "Value just before NEW_MOON upper bound");
    }

    @Test
    @DisplayName("Test 10: Edge case - very small positive value")
    void testVerySmallPositiveValue() {
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(0.001),
                "Very small value should be NEW_MOON");
    }

    @Test
    @DisplayName("Test 11: Edge case - value very close to 1.0")
    void testValueCloseToOne() {
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(0.999),
                "Value close to 1.0 should be NEW_MOON");
    }

    @Test
    @DisplayName("Test 12: Precise boundary at 0.15 (should be NEW_MOON)")
    void testPreciseBoundary015() {
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(0.15),
                "Exact value 0.15 should be NEW_MOON (inclusive)");
    }

    @Test
    @DisplayName("Test 13: Just after boundary 0.15 (should be NORMAL)")
    void testJustAfterBoundary015() {
        assertEquals(MoonPhase.NORMAL, MoonPhase.fromApiValue(0.151),
                "Value 0.151 should be NORMAL (exclusive)");
    }

    @Test
    @DisplayName("Test 14: Precise boundary at 0.85 (should be NEW_MOON)")
    void testPreciseBoundary085() {
        assertEquals(MoonPhase.NEW_MOON, MoonPhase.fromApiValue(0.85),
                "Exact value 0.85 should be NEW_MOON (inclusive)");
    }

    @Test
    @DisplayName("Test 15: Just before boundary 0.85 (should be NORMAL)")
    void testJustBeforeBoundary085() {
        assertEquals(MoonPhase.NORMAL, MoonPhase.fromApiValue(0.849),
                "Value 0.849 should be NORMAL (exclusive)");
    }

    // ==================== ENUM PROPERTY TESTS ====================

    @Test
    @DisplayName("Test 16: NEW_MOON has correct spawn multiplier")
    void testNewMoonSpawnMultiplier() {
        assertEquals(0.5, MoonPhase.NEW_MOON.getWolfSpawnMultiplier(),
                "NEW_MOON should have 0.5x spawn rate");
    }

    @Test
    @DisplayName("Test 17: NORMAL has correct spawn multiplier")
    void testNormalSpawnMultiplier() {
        assertEquals(1.0, MoonPhase.NORMAL.getWolfSpawnMultiplier(),
                "NORMAL should have 1.0x spawn rate");
    }

    @Test
    @DisplayName("Test 18: FULL_MOON has correct spawn multiplier")
    void testFullMoonSpawnMultiplier() {
        assertEquals(2.0, MoonPhase.FULL_MOON.getWolfSpawnMultiplier(),
                "FULL_MOON should have 2.0x spawn rate");
    }




    @Test
    @DisplayName("Test 22: All phases have non-null display messages")
    void testDisplayMessagesNotNull() {
        assertNotNull(MoonPhase.NEW_MOON.getDisplayMessage(),
                "NEW_MOON should have a display message");
        assertNotNull(MoonPhase.NORMAL.getDisplayMessage(),
                "NORMAL should have a display message");
        assertNotNull(MoonPhase.FULL_MOON.getDisplayMessage(),
                "FULL_MOON should have a display message");
    }

    @Test
    @DisplayName("Test 23: Display messages contain phase names")
    void testDisplayMessagesContent() {
        assertTrue(MoonPhase.NEW_MOON.getDisplayMessage().contains("NEW MOON"),
                "NEW_MOON message should contain 'NEW MOON'");
        assertTrue(MoonPhase.NORMAL.getDisplayMessage().contains("NORMAL"),
                "NORMAL message should contain 'NORMAL'");
        assertTrue(MoonPhase.FULL_MOON.getDisplayMessage().contains("FULL MOON"),
                "FULL_MOON message should contain 'FULL MOON'");
    }

    @Test
    @DisplayName("Test 24: Display messages contain emojis")
    void testDisplayMessagesHaveEmojis() {
        assertTrue(MoonPhase.NEW_MOON.getDisplayMessage().contains("🌑"),
                "NEW_MOON should have new moon emoji");
        assertTrue(MoonPhase.NORMAL.getDisplayMessage().contains("🌓"),
                "NORMAL should have half moon emoji");
        assertTrue(MoonPhase.FULL_MOON.getDisplayMessage().contains("🌕"),
                "FULL_MOON should have full moon emoji");
    }
}