package game.potions;

import game.potions.potionEffects.AreaEffect;
import game.potions.potionEffects.FreezeAreaEffect;

/**
 * A throwable potion that applies a freezing area-of-effect on impact.
 * <p>
 * This potion advertises its behavior via {@link #getAreaEffect()}, which returns a
 * {@link FreezeAreaEffect} to be executed by the caller (e.g., a throw action).
 * </p>
 *
 * <ul>
 *   <li><b>Icon:</b> {@code '!'}</li>
 *   <li><b>Name:</b> {@code "Freeze Potion"}</li>
 *   <li><b>Effect:</b> Delegates to {@link FreezeAreaEffect} for AoE application.</li>
 * </ul>
 *
 * @author Taiyo Arima
 * @version 1.0
 * @see AreaEffect
 * @see FreezeAreaEffect
 */
public class FreezePotion extends Potion {

    /**
     * Constructs a freeze potion with the standard display icon and name.
     */
    public FreezePotion() {
        super('!', "Freeze Potion");
    }

    /**
     * Returns the area effect to apply when this potion is used (e.g., thrown).
     *
     * @return a new {@link FreezeAreaEffect} instance
     */
    @Override
    public AreaEffect getAreaEffect() {
        return new FreezeAreaEffect();
    }
}
