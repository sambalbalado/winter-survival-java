package game.potions;

import game.potions.potionEffects.AreaEffect;
import game.potions.potionEffects.BurnAreaEffect;

/**
 * A throwable/usable potion that applies a burning {@link AreaEffect} on impact/use.
 * <p>
 * When activated, this potion produces a {@link BurnAreaEffect} which typically ignites
 * tiles/actors in its area according to the burn system rules.
 * </p>
 *
 * @version 1.0
 * @see Potion
 * @see AreaEffect
 * @see BurnAreaEffect
 */
public class BurnPotion extends Potion {

    /**
     * Constructs a {@code BurnPotion} with the display character {@code '!'} and name "Burn Potion".
     */
    public BurnPotion() {
        super('!', "Burn Potion");
    }

    /**
     * Returns the {@link AreaEffect} applied by this potion.
     *
     * @return a new {@link BurnAreaEffect} instance
     */
    @Override
    public AreaEffect getAreaEffect() {
        return new BurnAreaEffect();
    }
}
