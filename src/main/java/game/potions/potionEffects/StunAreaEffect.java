package game.potions.potionEffects;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.environment.LightningGround;
import game.status.Stunned;

import static game.potions.potionEffects.AoE.forEach3x3;

/**
 * Applies a 3×3 stun-and-lightning area-of-effect centered on the impact tile.
 * <p>
 * For each tile in the 3×3 neighborhood, the ground is replaced with
 * {@link LightningGround} for a limited duration, and any actor present receives a
 * {@link Stunned} status for one turn.
 * </p>
 *
 * <ul>
 *   <li><b>Shape:</b> Fixed 3×3 square centered on the target location.</li>
 *   <li><b>Ground effect:</b> Lays {@link LightningGround} for {@link #ZAP_DURATION} turns.</li>
 *   <li><b>Actor effect:</b> Applies {@code new Stunned(1)} to any actor on affected tiles.</li>
 *   <li><b>Bounds-safe:</b> Off-map tiles are skipped by {@link AoE#forEach3x3(GameMap, Location, java.util.function.Consumer)}.</li>
 * </ul>
 *
 * @author Taiyo Arima
 * @version 1.0
 * @see LightningGround
 * @see Stunned
 * @see AoE
 */
public class StunAreaEffect implements AreaEffect {

    /**
     * Number of turns the lightning ground persists on each affected tile.
     */
    private static final int ZAP_DURATION = 3;

    /**
     * Applies the stun/lightning effect over the 3×3 neighborhood of {@code center}.
     *
     * @param map    the active map
     * @param center the center of the AoE
     */
    @Override
    public void apply(GameMap map, Location center) {
        forEach3x3(map, center, loc -> {
            loc.setGround(new LightningGround(ZAP_DURATION));
            if (loc.containsAnActor()) {
                Actor a = loc.getActor();
                if (a != null) {
                    a.addStatus(new Stunned(1));
                }
            }
        });
    }
}
