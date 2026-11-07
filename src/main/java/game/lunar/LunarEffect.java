package game.lunar;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.api.MoonPhase;
import game.items.LunarBerry;

import java.util.*;

/**
 * Applies lunar phase effects to the game world.
 * Manages wolf spawn rates and Lunar Berry availability based on current moon phase.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public class LunarEffect {

    private final MoonPhase phase;
    private final Map<GameMap, Double> wolfSpawnMultipliers = new HashMap<>();

    /**
     * Creates a LunarEffect for the specified moon phase.
     *
     * @param phase the current moon phase
     */
    public LunarEffect(MoonPhase phase) {
        this.phase = phase;
    }

    /**
     * Sets the wolf spawn rate multiplier for the map based on current phase.
     * Spawning grounds can retrieve this multiplier to adjust spawn rates.
     *
     * @param map the game map to apply spawn rate effects to
     */
    public void applySpawnRateEffect(GameMap map) {
        double multiplier = phase.getWolfSpawnMultiplier();
        wolfSpawnMultipliers.put(map, multiplier);
    }

    /**
     * Spawns Lunar Berries on all Yew Berry Trees during Full Moon.
     * Identifies trees by display character 'Y' and checks for existing berries
     * before spawning to avoid duplicates.
     *
     * @param map the game map to spawn berries on
     */
    public void spawnLunarBerries(GameMap map) {
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                if (loc.getGround().getDisplayChar() == 'Y' || "Yew Berry Tree".equals(loc.getGround().toString())) {
                    boolean hasBerry = false;
                    for (Item item : loc.getItems()) {
                        if ("Lunar Berry".equals(item.toString()) || item.getDisplayChar() == 'L') {
                            break;
                        }
                    }
                    if (!hasBerry) {
                        loc.addItem(new LunarBerry());
                    }
                }
            }
        }
    }

    /**
     * Removes all Lunar Berries from the map when moon phase changes.
     * Identifies berries by display character 'L' or name "Lunar Berry".
     *
     * @param map the game map to remove berries from
     */
    public void removeLunarBerries(GameMap map) {
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                Location loc = map.at(x, y);
                List<Item> toRemove = new ArrayList<>();
                for (Item item : loc.getItems()) {
                    if ("Lunar Berry".equals(item.toString()) || item.getDisplayChar() == 'L') {
                        toRemove.add(item);
                    }
                }
                for (Item item : toRemove) {
                    loc.removeItem(item);
                }
            }
        }
    }
}
