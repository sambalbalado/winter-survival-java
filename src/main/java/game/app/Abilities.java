package game.app;

/**
 * An enumeration representing various abilities that actors and items can possess.
 *
 * This enum provides a standardized way to define and check for specific capabilities
 * throughout the game system. Abilities can be attached to actors, items, or other
 * game entities to control their interactions and available actions.
 *
 * Example usage: if a player can attack enemies, you can attach Abilities.CAN_ATTACK
 * to the Player class and check for this ability before allowing attack actions.
 *
 * @author Taabish Farooq Bhat
 * @version 1.0
 */
public enum Abilities {
CAN_ATTACK,
    CAN_DRINK,
    CAN_CONSUME,
    CAN_TAME,
    EAT_FRUIT,
    PREDATOR,
    COLD_RESISTANT,
    CONSUMES_GROUND_ITEMS,
    CAN_COAT_WEAPONS,
    CREATIVE_IMMUNE,
    FORM_WOLF,
    FORM_BEAR,
    FORM_DRAGON,
    CREATIVE_MODE,
    MORPH_PRIVILEGED,
    POTION_PRIVILEGED,
    HOSTILE,
    DOCILE,
    MOON_RAGE
}
