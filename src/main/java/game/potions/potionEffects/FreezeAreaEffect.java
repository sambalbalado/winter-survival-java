package game.potions.potionEffects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.environment.Ice;
import game.status.Frozen;
import game.status.FrostBitten;

/**
 * Applies a 3×3 freezing field centered on a target tile.
 * <p>
 * For each tile in the Chebyshev radius-1 neighborhood around {@code center}, this effect:
 * </p>
 * <ul>
 *   <li>Sets the ground to {@link Ice} for a limited duration, and</li>
 *   <li>If an {@link Actor} is present, applies a brief {@link Frozen} (2 turns) and a
 *   lingering {@link FrostBitten} chill (tick-based slow/DoT per your implementation).</li>
 * </ul>
 * <p>
 * Intended for use by throwable freeze potions and other AoE ice abilities.
 * </p>
 *
 * @version 1.0
 * @see AreaEffect
 * @see Ice
 * @see Frozen
 * @see FrostBitten
 */
public class FreezeAreaEffect implements AreaEffect {

    /**
     * Number of turns an {@link Ice} ground persists before reverting.
     */
    private static final int ICE_DURATION = 3;

    /**
     * Lays temporary ice on the 3×3 footprint and applies freezing statuses to any actors present.
     *
     * @param map    the game map providing tile access and bounds
     * @param center the central tile where the effect is centered
     */
    @Override
    public void apply(GameMap map, Location center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Location loc = map.at(center.x() + dx, center.y() + dy);
                loc.setGround(new Ice(ICE_DURATION));
                if (loc.containsAnActor()) {
                    Actor a = loc.getActor();
                    if (a != null) {
                        a.addStatus(new Frozen(2));       // brief freeze (stun)
                        a.addStatus(new FrostBitten());   // lingering chill/slow over time
                    }
                }
            }
        }
    }
}
