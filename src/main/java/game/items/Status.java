package game.items;

/**
 * Enum representing special statuses or properties of items.
 * <p>
 * Items can be marked as {@code FOOD} or {@code DRINK} to indicate
 * they can be eaten or drunk by actors.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public enum Status {
    /** Indicates the item is edible */
    FOOD,

    /** Indicates the item is drinkable */
    DRINK
}
