package game.potions.potionEffects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.environment.Fire;
import game.status.BurnSource;
import game.status.Ignite;

/**
 * Applies a 3×3 burning field centered on a target tile.
 * <p>
 * For each tile in the Chebyshev radius-1 neighborhood around {@code center}, this effect:
 * </p>
 * <ul>
 *   <li>Sets the ground to {@link Fire} with a limited lifetime, and</li>
 *   <li>If an {@link Actor} occupies the tile, applies/refreshes one stack of Ignite from
 *   {@link BurnSource#FireTile} via {@link Ignite#applyOrRefresh(Actor, BurnSource)}.</li>
 * </ul>
 * <p>
 * Intended for use by throwable burn potions and similar AoE fire abilities.
 * </p>
 *
 * @version 1.0
 * @see AreaEffect
 * @see Fire
 * @see Ignite
 * @see BurnSource
 */
public class BurnAreaEffect implements AreaEffect {

    /**
     * Number of turns a {@link Fire} ground persists before reverting.
     */
    private static final int FIRE_DURATION = 3; // turns before ground reverts

    /**
     * Lays temporary fire on the 3×3 footprint and applies a single Ignite stack to any actors present.
     *
     * @param map    the game map providing tile access and bounds
     * @param center the central tile where the effect is centered
     */
    @Override
    public void apply(GameMap map, Location center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Location loc = map.at(center.x() + dx, center.y() + dy);
                // Lay fire ground with limited lifetime
                loc.setGround(new Fire(BurnSource.FireTile, FIRE_DURATION));
                // Apply one Ignite stack from FireTile source if an actor is present
                if (loc.containsAnActor()) {
                    Actor a = loc.getActor();
                    if (a != null) Ignite.applyOrRefresh(a, BurnSource.FireTile);
                }
            }
        }
    }
}
