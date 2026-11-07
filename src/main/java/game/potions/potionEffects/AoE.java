package game.potions.potionEffects;

import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.function.Consumer;

/**
 * Utility helpers for applying area-of-effect (AoE) logic over map tiles.
 * <p>
 * These methods iterate tiles around a {@code center} location and invoke a caller-supplied
 * {@link Consumer} for each in-bounds {@link Location}. Out-of-bounds coordinates are skipped
 * silently, making the iteration safe at map edges and corners.
 * </p>
 *
 * <ul>
 *   <li><b>Bounds-safe:</b> Attempts to access off-map tiles are caught and ignored.</li>
 *   <li><b>Composable:</b> Callers pass a lambda/consumer to perform per-tile effects.</li>
 *   <li><b>Convenience:</b> {@link #forEach3x3(GameMap, Location, Consumer)} covers the common 3×3 case.</li>
 * </ul>
 *
 * <p>
 * Typical usage:
 * </p>
 * <pre>{@code
 * AoE.forEach3x3(map, impact, loc -> {
 *     // apply effect to 'loc'
 * });
 * }</pre>
 *
 * @author Taiyo Arima
 * @version 1.0
 */
public final class AoE {

    /** Not instantiable. */
    private AoE() {}

    /**
     * Iterates the 3×3 neighborhood centered on {@code center} (i.e., radius = 1) and applies {@code fn}.
     * <p>
     * The order is left-to-right, top-to-bottom relative to the center, but callers should not rely on
     * iteration order for correctness.
     * </p>
     *
     * @param map    the active map
     * @param center the center location
     * @param fn     a consumer invoked for each in-bounds tile in the 3×3 area
     */
    public static void forEach3x3(GameMap map, Location center, Consumer<Location> fn) {
        forEachSquare(map, center, 1, fn);
    }

    /**
     * Iterates a square AoE of side length {@code 2 * radius + 1} centered at {@code center}
     * (e.g., {@code radius = 1} yields 3×3), invoking {@code fn} for each in-bounds tile.
     * <p>
     * Any out-of-bounds access attempts are skipped by catching {@link IndexOutOfBoundsException}.
     * </p>
     *
     * @param map     the active map
     * @param center  the center location
     * @param radius  non-negative radius; {@code 0} visits only {@code center}
     * @param fn      a consumer invoked for each in-bounds tile within the square
     * @throws IllegalArgumentException if {@code radius} is negative
     */
    public static void forEachSquare(GameMap map, Location center, int radius, Consumer<Location> fn) {
        if (radius < 0) {
            throw new IllegalArgumentException("radius must be >= 0");
        }
        int cx = center.x(), cy = center.y();
        for (int ox = -radius; ox <= radius; ox++) {
            for (int oy = -radius; oy <= radius; oy++) {
                int nx = cx + ox, ny = cy + oy;
                try {
                    Location loc = map.at(nx, ny); // throws if OOB
                    fn.accept(loc);
                } catch (IndexOutOfBoundsException ignored) {
                    // skip off-map tiles
                }
            }
        }
    }
}
