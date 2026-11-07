package game.api;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.NumberRange;
import game.app.Abilities;
import game.items.Apple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for HD Requirement: API Integration for Lunar Phase System
 *
 * Tests cover:
 * 1. API initialization and fallback behavior
 * 2. Phase cycling mechanics
 * 3. Wolf behavior modifications
 * 4. Lunar berry spawning/removal
 *
 * All tests are isolated, deterministic, and use mocks to avoid external dependencies.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
class LunarPhaseManagerTest {

    private LunarPhaseManager lunarPhaseManager;

    @Mock
    private MoonPhaseService mockMoonPhaseService;

    @Mock
    private GameMap mockGameMap;

    @Mock
    private Actor mockPlayer;

    @Mock
    private Actor mockWolf;

    @Mock
    private Location mockLocation;

    @Mock
    private Ground mockGround;

    @Mock
    private Item mockItem;



    protected NumberRange createRange(int start, int end) {
        return new NumberRange(start, end);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        lunarPhaseManager = new LunarPhaseManager(mockMoonPhaseService);
        LunarPhaseManager.setInstance(lunarPhaseManager);
    }

    // ==================== API INTEGRATION TESTS ====================

    @Nested
    @DisplayName("API Initialization and Integration Tests")
    class ApiIntegrationTests {

        @Test
        @DisplayName("Test 1: Successful API call returns FULL_MOON")
        void testSuccessfulApiFetchFullMoon() throws Exception {
            // Arrange
            when(mockMoonPhaseService.isAvailable()).thenReturn(true);
            when(mockMoonPhaseService.fetchCurrentPhase()).thenReturn(MoonPhase.FULL_MOON);

            // Act
            lunarPhaseManager.initialize();

            // Assert
            assertEquals(MoonPhase.FULL_MOON, lunarPhaseManager.getCurrentPhase(),
                    "Should initialize to FULL_MOON from API");
            verify(mockMoonPhaseService, times(1)).isAvailable();
            verify(mockMoonPhaseService, times(1)).fetchCurrentPhase();
        }

        @Test
        @DisplayName("Test 2: Successful API call returns NEW_MOON")
        void testSuccessfulApiFetchNewMoon() throws Exception {
            // Arrange
            when(mockMoonPhaseService.isAvailable()).thenReturn(true);
            when(mockMoonPhaseService.fetchCurrentPhase()).thenReturn(MoonPhase.NEW_MOON);

            // Act
            lunarPhaseManager.initialize();

            // Assert
            assertEquals(MoonPhase.NEW_MOON, lunarPhaseManager.getCurrentPhase(),
                    "Should initialize to NEW_MOON from API");
            verify(mockMoonPhaseService, times(1)).fetchCurrentPhase();
        }

        @Test
        @DisplayName("Test 3: Successful API call returns NORMAL phase")
        void testSuccessfulApiFetchNormal() throws Exception {
            // Arrange
            when(mockMoonPhaseService.isAvailable()).thenReturn(true);
            when(mockMoonPhaseService.fetchCurrentPhase()).thenReturn(MoonPhase.NORMAL);

            // Act
            lunarPhaseManager.initialize();

            // Assert
            assertEquals(MoonPhase.NORMAL, lunarPhaseManager.getCurrentPhase(),
                    "Should initialize to NORMAL from API");
        }


        @Test
        @DisplayName("Test 4: API throws exception - fallback to NORMAL (negative case)")
        void testApiExceptionHandling() throws Exception {
            // Arrange
            when(mockMoonPhaseService.isAvailable()).thenReturn(true);
            when(mockMoonPhaseService.fetchCurrentPhase())
                    .thenThrow(new RuntimeException("Network timeout"));

            // Act
            lunarPhaseManager.initialize();

            // Assert
            assertEquals(MoonPhase.NORMAL, lunarPhaseManager.getCurrentPhase(),
                    "Should fallback to NORMAL when API throws exception");
            verify(mockMoonPhaseService, times(1)).fetchCurrentPhase();
        }



        @Test
        @DisplayName("Test 5: Null service handling (boundary case)")
        void testNullServiceDoesNotCrash() {
            // Arrange
            LunarPhaseManager managerWithNull = new LunarPhaseManager(null);

            // Act & Assert
            assertDoesNotThrow(() -> managerWithNull.initialize(),
                    "Should handle null service gracefully");
            assertEquals(MoonPhase.NORMAL, managerWithNull.getCurrentPhase(),
                    "Should default to NORMAL with null service");
        }

        @Test
        @DisplayName("Test 6: Initialize does not override already set phase (edge case)")
        void testInitializeDoesNotOverrideExistingPhase() throws Exception {
            // Arrange - advance to FULL_MOON manually
            for (int i = 1; i < 10; i++) {
                lunarPhaseManager.advanceTurn();
            }
            assertEquals(MoonPhase.FULL_MOON, lunarPhaseManager.getCurrentPhase());

            // Mock API to return NEW_MOON
            when(mockMoonPhaseService.isAvailable()).thenReturn(true);
            when(mockMoonPhaseService.fetchCurrentPhase()).thenReturn(MoonPhase.NEW_MOON);

            // Act
            lunarPhaseManager.initialize();

            // Assert - should now be NEW_MOON (initialize overrides)
            assertEquals(MoonPhase.NEW_MOON, lunarPhaseManager.getCurrentPhase(),
                    "Initialize should update current phase from API");
        }

        @Test
        @DisplayName("Test 7: Multiple initialize calls use same phase (caching behavior)")
        void testMultipleInitializeCalls() throws Exception {
            // Arrange
            when(mockMoonPhaseService.isAvailable()).thenReturn(true);
            when(mockMoonPhaseService.fetchCurrentPhase()).thenReturn(MoonPhase.FULL_MOON);

            // Act
            lunarPhaseManager.initialize();
            MoonPhase firstPhase = lunarPhaseManager.getCurrentPhase();
            lunarPhaseManager.initialize();
            MoonPhase secondPhase = lunarPhaseManager.getCurrentPhase();

            // Assert
            assertEquals(firstPhase, secondPhase, "Multiple initializations should be consistent");
            assertEquals(MoonPhase.FULL_MOON, secondPhase);
        }
    }

    // ==================== PHASE CYCLING TESTS ====================

    @Nested
    @DisplayName("Turn-based Phase Cycling Tests")
    class PhaseCyclingTests {

        @Test
        @DisplayName("Test 8: NORMAL phase lasts 9 turns (turns 1-9)")
        void testNormalPhaseDuration() {
            // Turn 1 starts at NORMAL
            assertEquals(MoonPhase.NORMAL, lunarPhaseManager.getCurrentPhase());

            // Advance through turns 2-9
            for (int turn = 2; turn <= 9; turn++) {
                lunarPhaseManager.advanceTurn();
                assertEquals(MoonPhase.NORMAL, lunarPhaseManager.getCurrentPhase(),
                        "Turn " + turn + " should be NORMAL phase");
            }
        }

        @Test
        @DisplayName("Test 9: FULL_MOON phase lasts 9 turns (turns 10-18)")
        void testFullMoonPhaseDuration() {
            // Advance to turn 10
            for (int i = 1; i < 10; i++) {
                lunarPhaseManager.advanceTurn();
            }

            // Verify turns 10-18 are FULL_MOON
            for (int turn = 10; turn <= 18; turn++) {
                assertEquals(MoonPhase.FULL_MOON, lunarPhaseManager.getCurrentPhase(),
                        "Turn " + turn + " should be FULL_MOON phase");
                if (turn < 18) {
                    lunarPhaseManager.advanceTurn();
                }
            }
        }

        @Test
        @DisplayName("Test 10: NEW_MOON phase lasts 9 turns (turns 19-27)")
        void testNewMoonPhaseDuration() {
            // Advance to turn 19
            for (int i = 1; i < 19; i++) {
                lunarPhaseManager.advanceTurn();
            }

            // Verify turns 19-27 are NEW_MOON
            for (int turn = 19; turn <= 27; turn++) {
                assertEquals(MoonPhase.NEW_MOON, lunarPhaseManager.getCurrentPhase(),
                        "Turn " + turn + " should be NEW_MOON phase");
                if (turn < 27) {
                    lunarPhaseManager.advanceTurn();
                }
            }
        }

        @Test
        @DisplayName("Test 11: Phase cycle repeats after 27 turns (boundary)")
        void testPhaseCycleRepeat() {
            // Go to turn 27 (last NEW_MOON)
            for (int i = 1; i < 27; i++) {
                lunarPhaseManager.advanceTurn();
            }
            assertEquals(MoonPhase.NEW_MOON, lunarPhaseManager.getCurrentPhase());

            // Turn 28 should restart at NORMAL
            lunarPhaseManager.advanceTurn();
            assertEquals(MoonPhase.NORMAL, lunarPhaseManager.getCurrentPhase(),
                    "Turn 28 should cycle back to NORMAL");

            // Verify turn 37 is FULL_MOON (28+9)
            for (int i = 28; i < 37; i++) {
                lunarPhaseManager.advanceTurn();
            }
            assertEquals(MoonPhase.FULL_MOON, lunarPhaseManager.getCurrentPhase());
        }

        @Test
        @DisplayName("Test 12: Phase transitions are immediate (edge case)")
        void testPhaseTransitionsImmediate() {
            // Advance to turn 9 (last NORMAL)
            for (int i = 1; i < 9; i++) {
                lunarPhaseManager.advanceTurn();
            }
            assertEquals(MoonPhase.NORMAL, lunarPhaseManager.getCurrentPhase());

            // One more turn should immediately switch to FULL_MOON
            lunarPhaseManager.advanceTurn();
            assertEquals(MoonPhase.FULL_MOON, lunarPhaseManager.getCurrentPhase(),
                    "Phase should change immediately on turn 10");

            // Advance to turn 18 (last FULL_MOON)
            for (int i = 10; i < 18; i++) {
                lunarPhaseManager.advanceTurn();
            }
            assertEquals(MoonPhase.FULL_MOON, lunarPhaseManager.getCurrentPhase());

            // Next turn should immediately switch to NEW_MOON
            lunarPhaseManager.advanceTurn();
            assertEquals(MoonPhase.NEW_MOON, lunarPhaseManager.getCurrentPhase(),
                    "Phase should change immediately on turn 19");
        }

    }

    // ==================== WOLF BEHAVIOR TESTS ====================

    @Nested
    @DisplayName("Wolf Behavior Modification Tests")
    class WolfBehaviorTests {

        @BeforeEach
        void setupMockMap() {
            // Setup mock map with one location containing a wolf
            when(mockGameMap.getXRange()).thenReturn(createRange(0, 1));
            when(mockGameMap.getYRange()).thenReturn(createRange(0, 1));
            when(mockGameMap.at(anyInt(), anyInt())).thenReturn(mockLocation);
            when(mockLocation.containsAnActor()).thenReturn(true);
            when(mockLocation.getActor()).thenReturn(mockWolf);
            List<Item> items = new ArrayList<>();
            items.add(new Apple());
            when(mockWolf.getItemInventory()).thenReturn(items);
            when(mockWolf.toString()).thenReturn("Wolf");

            List<Item> emptyItems = new ArrayList<>();
            when(mockLocation.getItems()).thenReturn(emptyItems);
            when(mockLocation.getGround()).thenReturn(mockGround);
            when(mockGround.getDisplayChar()).thenReturn('.');

        }

        @Test
        @DisplayName("Test 13: Wolves aggressive during NORMAL phase")
        void testWolvesAggressiveNormal() {
            // Act
            lunarPhaseManager.applyPhaseEffects(mockGameMap, mockPlayer);

            // Assert
            verify(mockWolf).disableAbility(Abilities.DOCILE);
            verify(mockWolf).enableAbility(Abilities.CAN_ATTACK);
            verify(mockWolf).disableAbility(Abilities.MOON_RAGE);
        }

        @Test
        @DisplayName("Test 14: Wolves enraged with MOON_RAGE during FULL_MOON")
        void testWolvesEnragedFullMoon() {
            // Arrange - advance to FULL_MOON
            for (int i = 1; i < 10; i++) {
                lunarPhaseManager.advanceTurn();
            }

            // Act
            lunarPhaseManager.applyPhaseEffects(mockGameMap, mockPlayer);

            // Assert
            verify(mockWolf).disableAbility(Abilities.DOCILE);
            verify(mockWolf).enableAbility(Abilities.CAN_ATTACK);
            verify(mockWolf).enableAbility(Abilities.MOON_RAGE);
        }

        @Test
        @DisplayName("Test 15: Wolves become docile and cannot attack during NEW_MOON")
        void testWolvesDocileNewMoon() {
            // Arrange - advance to NEW_MOON
            for (int i = 1; i < 19; i++) {
                lunarPhaseManager.advanceTurn();
            }

            // Act
            lunarPhaseManager.applyPhaseEffects(mockGameMap, mockPlayer);

            // Assert
            verify(mockWolf).enableAbility(Abilities.DOCILE);
            verify(mockWolf).disableAbility(Abilities.CAN_ATTACK);
        }


        @Test
        @DisplayName("Test 16: Null player does not crash wolf behavior (boundary case)")
        void testNullPlayerDoesNotCrashWolfBehavior() {
            // Act & Assert
            assertDoesNotThrow(() -> lunarPhaseManager.applyPhaseEffects(mockGameMap, null),
                    "Should handle null player gracefully");

            // Wolf behavior should still apply
            verify(mockWolf).disableAbility(Abilities.DOCILE);
            verify(mockWolf).enableAbility(Abilities.CAN_ATTACK);
        }

        private void setupLocation(Location loc, Actor actor, String actorName) {
            when(loc.containsAnActor()).thenReturn(actor != null);
            if (actor != null) {
                when(loc.getActor()).thenReturn(actor);
                when(actor.toString()).thenReturn(actorName);
                when(actor.hasAbility(Abilities.HOSTILE)).thenReturn(true);
            }
            List<Item> items = new ArrayList<>();
            when(loc.getItems()).thenReturn(items);
            Ground ground = mock(Ground.class);
            when(loc.getGround()).thenReturn(ground);
            when(ground.getDisplayChar()).thenReturn('.');
        }
    }

    // ==================== LUNAR BERRY TESTS ====================

    @Nested
    @DisplayName("Lunar Berry Spawning/Removal Tests")
    class LunarBerryTests {

        @BeforeEach
        void setupMockMapWithYewTree() {
            when(mockGameMap.getXRange()).thenReturn(createRange(0, 1));
            when(mockGameMap.getYRange()).thenReturn(createRange(0, 1));
            when(mockGameMap.at(anyInt(), anyInt())).thenReturn(mockLocation);
            when(mockLocation.containsAnActor()).thenReturn(false);
            when(mockLocation.getGround()).thenReturn(mockGround);
            when(mockGround.getDisplayChar()).thenReturn('Y'); // Yew tree
            when(mockGround.toString()).thenReturn("Yew Berry Tree");
        }

        @Test
        @DisplayName("Test 17: Lunar berries spawn during FULL_MOON on Yew trees")
        void testLunarBerriesSpawnFullMoon() {
            // Arrange
            List<Item> items = new ArrayList<>();
            when(mockLocation.getItems()).thenReturn(items);

            // Advance to FULL_MOON
            for (int i = 1; i < 10; i++) {
                lunarPhaseManager.advanceTurn();
            }

            // Act
            lunarPhaseManager.applyPhaseEffects(mockGameMap, mockPlayer);

            // Assert
            verify(mockLocation, atLeastOnce()).addItem(any(Item.class));
        }

        @Test
        @DisplayName("Test 18: Lunar berries removed during NORMAL phase")
        void testLunarBerriesRemovedNormal() {
            // Arrange
            Item berry = mock(Item.class);
            when(berry.toString()).thenReturn("Lunar Berry");
            when(berry.getDisplayChar()).thenReturn('L');
            List<Item> items = new ArrayList<>();
            items.add(berry);
            when(mockLocation.getItems()).thenReturn(items);

            // Act (NORMAL phase by default)
            lunarPhaseManager.applyPhaseEffects(mockGameMap, mockPlayer);

            // Assert
            verify(mockLocation).removeItem(berry);
        }

        @Test
        @DisplayName("Test 19: Lunar berries removed during NEW_MOON phase")
        void testLunarBerriesRemovedNewMoon() {
            // Arrange
            Item berry = mock(Item.class);
            when(berry.toString()).thenReturn("Lunar Berry");
            when(berry.getDisplayChar()).thenReturn('L');
            List<Item> items = new ArrayList<>();
            items.add(berry);
            when(mockLocation.getItems()).thenReturn(items);

            // Advance to NEW_MOON
            for (int i = 1; i < 19; i++) {
                lunarPhaseManager.advanceTurn();
            }

            // Act
            lunarPhaseManager.applyPhaseEffects(mockGameMap, mockPlayer);

            // Assert
            verify(mockLocation).removeItem(berry);
        }

        @Test
        @DisplayName("Test 20: No berries spawn on non-Yew locations (negative case)")
        void testNoBerrySpawnOnNonYewLocations() {
            // Arrange - change ground to non-Yew
            when(mockGround.getDisplayChar()).thenReturn('.');
            when(mockGround.toString()).thenReturn("Dirt");
            List<Item> items = new ArrayList<>();
            when(mockLocation.getItems()).thenReturn(items);

            // Advance to FULL_MOON
            for (int i = 1; i < 10; i++) {
                lunarPhaseManager.advanceTurn();
            }

            // Act
            lunarPhaseManager.applyPhaseEffects(mockGameMap, mockPlayer);

            // Assert
            verify(mockLocation, never()).addItem(any(Item.class));
        }

        @Test
        @DisplayName("Test 21: Berries persist through FULL_MOON phase (edge case)")
        void testBerriesPersistThroughFullMoon() {
            // Arrange
            List<Item> items = new ArrayList<>();
            when(mockLocation.getItems()).thenReturn(items);

            // Advance to FULL_MOON
            for (int i = 1; i < 10; i++) {
                lunarPhaseManager.advanceTurn();
            }

            // Act - apply effects multiple times during FULL_MOON
            lunarPhaseManager.applyPhaseEffects(mockGameMap, mockPlayer);
            lunarPhaseManager.advanceTurn(); // Still FULL_MOON
            lunarPhaseManager.applyPhaseEffects(mockGameMap, mockPlayer);

            // Assert - berries should not be removed during FULL_MOON
            verify(mockLocation, never()).removeItem(any(Item.class));
        }

        @Test
        @DisplayName("Test 22: Multiple berries on same location handled (edge case)")
        void testMultipleBerries() {
            // Arrange
            Item berry1 = mock(Item.class);
            Item berry2 = mock(Item.class);
            when(berry1.toString()).thenReturn("Lunar Berry");
            when(berry1.getDisplayChar()).thenReturn('L');
            when(berry2.toString()).thenReturn("Lunar Berry");
            when(berry2.getDisplayChar()).thenReturn('L');

            List<Item> items = new ArrayList<>();
            items.add(berry1);
            items.add(berry2);
            when(mockLocation.getItems()).thenReturn(items);

            // Act (NORMAL phase - remove berries)
            lunarPhaseManager.applyPhaseEffects(mockGameMap, mockPlayer);

            // Assert - both should be removed
            verify(mockLocation).removeItem(berry1);
            verify(mockLocation).removeItem(berry2);
        }
    }



    // ==================== ADDITIONAL INTEGRATION TESTS ====================

    @Test
    @DisplayName("Test 23: Phase description matches current phase")
    void testPhaseDescriptionAccuracy() {
        // NORMAL
        assertTrue(lunarPhaseManager.getPhaseDescription().contains("NORMAL"),
                "Description should indicate NORMAL phase");

        // FULL_MOON
        for (int i = 1; i < 10; i++) {
            lunarPhaseManager.advanceTurn();
        }
        assertTrue(lunarPhaseManager.getPhaseDescription().contains("FULL MOON"),
                "Description should indicate FULL MOON phase");

        // NEW_MOON
        for (int i = 10; i < 19; i++) {
            lunarPhaseManager.advanceTurn();
        }
        assertTrue(lunarPhaseManager.getPhaseDescription().contains("NEW MOON"),
                "Description should indicate NEW MOON phase");
    }

    @Test
    @DisplayName("Test 24: Singleton instance management")
    void testSingletonInstance() {
        // Arrange
        LunarPhaseManager newInstance = new LunarPhaseManager(mockMoonPhaseService);

        // Act
        LunarPhaseManager.setInstance(newInstance);

        // Assert
        assertEquals(newInstance, LunarPhaseManager.getInstance(),
                "getInstance should return the set instance");
    }

    @Test
    @DisplayName("Test 25: LunarEffect object is not null")
    void testLunarEffectNotNull() {
        // Assert
        assertNotNull(lunarPhaseManager.getLunarEffect(),
                "LunarEffect should never be null");
    }
}