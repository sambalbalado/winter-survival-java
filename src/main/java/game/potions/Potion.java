package game.potions;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.potionactions.ThrowDirection;
import game.actions.potionactions.ThrowPotionAction;
import game.app.Abilities;
import game.potions.potionEffects.AreaEffect;

/**
 * Base class for throwable potions that apply an {@link AreaEffect} when used (typically on throw/impact).
 * <p>
 * When the performer has {@link Abilities#POTION_PRIVILEGED}, this item exposes directional throw actions
 * (N/E/S/W) up to a fixed range. Subclasses provide the concrete {@link #getAreaEffect()} to be applied
 * at the target tile on resolution.
 * </p>
 *
 * @version 1.0
 * @see AreaEffect
 * @see ThrowPotionAction
 * @see Abilities#POTION_PRIVILEGED
 */
public abstract class Potion extends Item {

    /**
     * Constructs a portable potion item with the given display character and name.
     *
     * @param displayChar the single-character glyph used to render this potion on the map
     * @param name        the item name shown in inventories and messages
     */
    protected Potion(char displayChar, String name) { super(name, displayChar, /*portable=*/true); }

    /**
     * Provides the effect to apply when this potion is used (e.g., upon impact).
     *
     * @return the {@link AreaEffect} associated with this potion
     */
    public abstract AreaEffect getAreaEffect();

    /**
     * Adds directional throw actions when the actor has {@link Abilities#POTION_PRIVILEGED}.
     * <p>
     * The base implementation contributes four {@link ThrowPotionAction}s (N/E/S/W) with a fixed range.
     * Subclasses typically inherit this behavior unchanged.
     * </p>
     *
     * @param actor the actor holding/using the item
     * @param map   the current map context
     * @return the aggregated list of available actions for this item
     */
    @Override
    public ActionList allowableActions(Actor actor, GameMap map) {
        ActionList actions = super.allowableActions(actor, map);

        // Only show throws when Creative “potion privileges” are on
        if (!actor.hasAbility(Abilities.POTION_PRIVILEGED)) return actions;

        final int MAX_RANGE = 3; // or whatever you want
        actions.add(new ThrowPotionAction(this, ThrowDirection.NORTH, MAX_RANGE));
        actions.add(new ThrowPotionAction(this, ThrowDirection.EAST,  MAX_RANGE));
        actions.add(new ThrowPotionAction(this, ThrowDirection.SOUTH, MAX_RANGE));
        actions.add(new ThrowPotionAction(this, ThrowDirection.WEST,  MAX_RANGE));
        return actions;
    }
}
