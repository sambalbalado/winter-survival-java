package game.items;

import edu.monash.fit2099.engine.items.Item;
import game.app.Abilities;

/**
 * Represents snow coating material for weapons.
 * This item can be used to coat weapons, providing special effects during combat.
 * Snow coating is not portable and is applied directly from snow terrain.
 *
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public class SnowCoatingItem extends Item {

    /**
     * Constructor for SnowCoatingItem.
     * Creates a non-portable snow coating item with the '.' display character.
     * Enables the CAN_COAT_WEAPONS ability.
     */
    public SnowCoatingItem() {
        super("Snow", '.', false);
        this.enableAbility(Abilities.CAN_COAT_WEAPONS);
    }

    /**
     * Returns the string representation of the snow coating item.
     *
     * @return "Snow"
     */
    @Override
    public String toString() {
        return "Snow";
    }
}
