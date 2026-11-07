package game.potions;

import edu.monash.fit2099.engine.items.Item;
import game.items.*;
import java.util.function.Supplier;

/**
 * Declarative registry of craftable potion recipes.
 * <p>
 * Each enum constant specifies:
 * </p>
 * <ul>
 *   <li><b>label</b> – human-friendly potion name for menus/messages,</li>
 *   <li><b>ingA / ingB</b> – the two ingredient {@link Item} classes required (one instance each), and</li>
 *   <li><b>maker</b> – a {@link Supplier} that constructs the resulting {@link Potion}.</li>
 * </ul>
 * <p>
 * Typical usage: a brew action looks up a {@code PotionRecipe}, verifies the actor has one of each
 * ingredient class in inventory, then consumes them and adds {@code maker.get()}.
 * </p>
 *
 * @version 1.0
 * @see Potion
 * @see game.actions.potionactions.BrewAction
 */
public enum PotionRecipe {
    /** Apple + Hazelnut ⇒ {@link BurnPotion}. */
    BURN("Burn Potion", Apple.class, Hazelnut.class, BurnPotion::new),

    /** Apple + YewBerry ⇒ {@link StunPotion}. */
    STUN("Stun Potion", Apple.class, YewBerry.class, StunPotion::new),

    /** YewBerry + Hazelnut ⇒ {@link FreezePotion}. */
    FREEZE("Freeze Potion", YewBerry.class, Hazelnut.class, FreezePotion::new);

    /**
     * User-facing potion name (e.g., for menu descriptions).
     */
    public final String label;

    /**
     * First required ingredient class.
     */
    public final Class<? extends Item> ingA;

    /**
     * Second required ingredient class.
     */
    public final Class<? extends Item> ingB;

    /**
     * Factory for creating the resulting {@link Potion} upon successful brew.
     */
    public final Supplier<Potion> maker;

    /**
     * Constructs a recipe definition.
     *
     * @param label human-readable potion name
     * @param ingA  first ingredient class (one instance required)
     * @param ingB  second ingredient class (one instance required)
     * @param maker supplier that constructs the brewed {@link Potion}
     */
    PotionRecipe(String label,
                 Class<? extends Item> ingA,
                 Class<? extends Item> ingB,
                 Supplier<Potion> maker) {
        this.label = label;
        this.ingA = ingA;
        this.ingB = ingB;
        this.maker = maker;
    }
}
