package game.potions.potionEffects;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Strategy interface for applying an area-of-effect centered on a tile.
 * <p>
 * Implementations typically affect a 3×3 neighborhood (Chebyshev radius 1) around the
 * provided {@code center}, but the exact radius/shape is left to the concrete effect.
 * Common uses include laying temporary ground, damaging/affecting actors, or spawning
 * items within the affected area.
 * </p>
 *
 * @version 1.0
 */
public interface AreaEffect {

    /**
     * Applies this effect using the given map context and center location.
     *
     * @param map    the {@link GameMap} whose tiles/actors may be modified
     * @param center the central {@link Location} of the effect footprint
     */
    void apply(GameMap map, Location center);
}
