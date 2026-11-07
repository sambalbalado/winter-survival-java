// game/actions/BrewSpecificPotionAction.java
package game.actions.potionactions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.app.Abilities;
import game.potions.Potion;
import game.potions.PotionRecipe;

import java.util.Optional;

/**
 * Brews a specific {@link Potion} defined by a {@link PotionRecipe}, consuming ingredients from inventory.
 * <p>
 * The performer must have {@link Abilities#POTION_PRIVILEGED}. When executed, this action searches the
 * actor's inventory for one instance of each required ingredient class
 * ({@link PotionRecipe#ingA} and {@link PotionRecipe#ingB}). If found, both items are removed and a new
 * potion instance from {@link PotionRecipe#maker} is created and added to the actor's inventory.
 * </p>
 *
 * <ul>
 *   <li><b>Permissions:</b> Requires {@link Abilities#POTION_PRIVILEGED}.</li>
 *   <li><b>Consumption:</b> Exactly one of each ingredient is removed on success.</li>
 *   <li><b>Feedback:</b> Returns a descriptive message for both success and failure cases.</li>
 * </ul>
 *
 * @version 1.0
 * @see PotionRecipe
 * @see Potion
 * @see Abilities#POTION_PRIVILEGED
 */
public class BrewAction extends Action {
    /**
     * The concrete recipe specifying the potion label, ingredient classes, and maker.
     */
    private final PotionRecipe recipe;

    /**
     * Creates a brew action bound to a specific {@link PotionRecipe}.
     *
     * @param recipe the recipe describing required ingredients and how to create the potion
     */
    public BrewAction(PotionRecipe recipe) {
        this.recipe = recipe;
    }

    /**
     * Attempts to brew the potion:
     * <ol>
     *   <li>Checks {@link Abilities#POTION_PRIVILEGED} on the performer.</li>
     *   <li>Finds one instance of each ingredient class in the actor's inventory.</li>
     *   <li>Removes those items and adds the crafted {@link Potion}.</li>
     * </ol>
     *
     * @param actor the performer attempting to brew
     * @param map   the current map (not used directly; present for API consistency)
     * @return a descriptive message indicating success or the reason for failure
     */
    @Override
    public String execute(Actor actor, GameMap map) {

        if (!actor.hasAbility(Abilities.POTION_PRIVILEGED)) {
            return actor + " lacks potion privileges.";
        }

        // find one of each ingredient in inventory
        Optional<Item> a = actor.getItemInventory().stream()
                .filter(recipe.ingA::isInstance).findFirst();
        Optional<Item> b = actor.getItemInventory().stream()
                .filter(recipe.ingB::isInstance).findFirst();

        if (a.isEmpty() || b.isEmpty()) {
            return "You don't have the required ingredients for " + recipe.label + ".";
        }

        // consume ingredients
        actor.removeItemFromInventory(a.get());
        actor.removeItemFromInventory(b.get());

        // add potion
        Potion p = recipe.maker.get();
        actor.addItemToInventory(p);
        return "You brew a " + p + " using " + a.get() + " + " + b.get() + ".";
    }

    /**
     * {@inheritDoc}
     * <p>
     * Shows a succinct menu entry with the potion name and required ingredient class names.
     * </p>
     */
    @Override
    public String menuDescription(Actor actor) {
        return "Brew " + recipe.label + " (" +
                recipe.ingA.getSimpleName() + " + " + recipe.ingB.getSimpleName() + ")";
    }

}
