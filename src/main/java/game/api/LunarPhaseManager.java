package game.api;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.app.Abilities;
import game.lunar.LunarEffect;

/**
 * Manages the lunar phase system and coordinates all moon-related game effects.
 * Fetches real-world moon phase data from API and applies effects to wolves,
 * spawn rates, and special items.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class LunarPhaseManager {

    private final MoonPhaseService moonPhaseService;
    private int turnNumber = 1;
    private MoonPhase currentPhase = MoonPhase.NORMAL;
    private LunarEffect lunarEffect;
    private static LunarPhaseManager instance;

    /**
     * Creates a LunarPhaseManager with the specified moon phase service.
     *
     * @param moonPhaseService the service used to fetch moon phase data
     */
    public LunarPhaseManager(MoonPhaseService moonPhaseService) {
        this.moonPhaseService = moonPhaseService;
        this.lunarEffect = new LunarEffect(currentPhase);
    }

    /**
     * Advances the turn counter and cycles through phases for testing.
     * Cycles every 27 turns: 9 Normal, 9 Full Moon, 9 New Moon.
     */
    public void advanceTurn() {
        turnNumber++;
        int cycle = (turnNumber - 1) % 27;
        if (cycle < 9) {
            currentPhase = MoonPhase.NORMAL;
        } else if (cycle < 18) {
            currentPhase = MoonPhase.FULL_MOON;
        } else {
            currentPhase = MoonPhase.NEW_MOON;
        }
    }

    /**
     * Returns the current moon phase.
     *
     * @return the current MoonPhase
     */
    public MoonPhase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Returns the lunar effect applicator for current phase.
     *
     * @return the LunarEffect instance
     */
    public LunarEffect getLunarEffect() {
        return lunarEffect;
    }

    /**
     * Fetches the current moon phase from API at game start.
     * Falls back to NORMAL phase if API is unavailable or fails.
     */
    public void initialize() {
        try {
            if (moonPhaseService.isAvailable()) {
                currentPhase = moonPhaseService.fetchCurrentPhase();
            } else {
                System.out.println("Moon phase service currently unavailable. Defaulting to NORMAL phase.");
                currentPhase = MoonPhase.NORMAL;
            }
        } catch (Exception e) {
            System.err.println("Error fetching moon phase: " + e.getMessage());
            currentPhase = MoonPhase.NORMAL;
        }
    }

    /**
     * Sets the singleton instance of LunarPhaseManager.
     *
     * @param manager the LunarPhaseManager instance to set
     */
    public static void setInstance(LunarPhaseManager manager) {
        instance = manager;
    }

    /**
     * Returns the singleton instance of LunarPhaseManager.
     *
     * @return the LunarPhaseManager instance, or null if not initialized
     */
    public static LunarPhaseManager getInstance() {
        return instance;
    }

    /**
     * Applies all lunar phase effects to the game world each turn.
     * <p>
     * Effects Applied:
     * <ul>
     *   <li>Sets wolf spawn rate multipliers based on phase</li>
     *   <li>Spawns/removes Lunar Berries on Yew Berry Trees</li>
     *   <li>Manages wolf abilities:
     *     <ul>
     *       <li>New Moon: Wolves become docile, lose CAN_ATTACK</li>
     *       <li>Full Moon: Wolves gain MOON_RAGE for +5 damage</li>
     *       <li>Normal: Standard wolf behavior</li>
     *     </ul>
     *   </li>
     *   <li>Displays current phase message to console</li>
     * </ul>
     *
     * @param map the game map to apply effects to
     * @param player the player actor (currently unused)
     */
    public void applyPhaseEffects(GameMap map, Actor player) {
        LunarEffect lunarEffect = new LunarEffect(currentPhase);

        // Adjust wolf spawn rate based on current phase
        lunarEffect.applySpawnRateEffect(map);

        // Handle Lunar Berries depending on phase
        if (currentPhase == MoonPhase.FULL_MOON) {
            lunarEffect.spawnLunarBerries(map);
        } else {
            lunarEffect.removeLunarBerries(map);
        }

        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.containsAnActor()) {
                    Actor actor = loc.getActor();
                    // Check if it's a wolf
                    if (actor.toString().contains("Wolf") || actor.hasAbility(Abilities.HOSTILE)) {

                        if (currentPhase == MoonPhase.NEW_MOON) {
                            // Wolves become docile
                            actor.enableAbility(Abilities.DOCILE);
                            actor.disableAbility(Abilities.CAN_ATTACK);
                        } else {
                            // Wolves are aggressive
                            actor.disableAbility(Abilities.DOCILE);
                            actor.enableAbility(Abilities.CAN_ATTACK);

                            // Full moon: add damage bonus
                            if (currentPhase == MoonPhase.FULL_MOON) {
                                actor.enableAbility(Abilities.MOON_RAGE);
                            } else {
                                actor.disableAbility(Abilities.MOON_RAGE);
                            }
                        }
                    }
                }
            }
        }

        System.out.println(currentPhase.getDisplayMessage());
    }

    /**
     * Returns the display message for the current moon phase.
     *
     * @return the phase description string
     */
    public String getPhaseDescription() {
        return currentPhase.getDisplayMessage();
    }
}
