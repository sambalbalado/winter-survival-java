package game.potions;

import game.potions.potionEffects.AreaEffect;
import game.potions.potionEffects.StunAreaEffect;

/**
 * A throwable potion that applies a stunning area-of-effect on impact.
 * <p>
 * The concrete behavior is advertised via {@link #getAreaEffect()}, which returns a
 * {@link StunAreaEffect} instance for callers (e.g., a throw action) to execute.
 * </p>
 *
 * <ul>
 *   <li><b>Icon:</b> {@code '!'}</li>
 *   <li><b>Name:</b> {@code "Stun Potion"}</li>
 *   <li><b>Effect:</b> Delegates to {@link StunAreaEffect} to apply the AoE.</li>
 * </ul>
 *
 * @author Taiyo Arima
 * @version 1.0
 * @see AreaEffect
 * @see StunAreaEffect
 */
public class StunPotion extends Potion {

    /**
     * Constructs a stun potion with the standard display icon and name.
     */
    public StunPotion() {
        super('!', "Stun Potion");
    }

    /**
     * Returns the area effect to apply when this potion is used (e.g., thrown).
     *
     * @return a new {@link StunAreaEffect} instance
     */
    @Override
    public AreaEffect getAreaEffect() {
        return new StunAreaEffect();
    }
}
