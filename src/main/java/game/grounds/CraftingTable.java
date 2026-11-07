// game/grounds/CraftingTable.java
package game.grounds;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.potionactions.BrewAction;
import game.potions.PotionRecipe;

public class CraftingTable extends Ground {

    public CraftingTable() {
        super('田', "Crafting Table");
    }

    @Override
    public ActionList allowableActions(Actor actor, Location location, String direction) {
        ActionList actions = new ActionList();

        // Robust: actor must be standing on THIS tile (no string magic)
        if (location.containsAnActor() && location.getActor() == actor) {
            // Add recipe actions only for craftable pairs
            boolean hasApple =  has(actor, game.items.Apple.class);
            boolean hasYew =    has(actor, game.items.YewBerry.class);
            boolean hasHaz =    has(actor, game.items.Hazelnut.class);

            if (hasApple && hasHaz) actions.add(new BrewAction(PotionRecipe.BURN));
            if (hasApple && hasYew) actions.add(new BrewAction(PotionRecipe.STUN));
            if (hasYew && hasHaz)   actions.add(new BrewAction(PotionRecipe.FREEZE));
        }
        return actions;
    }

    private static boolean has(Actor actor, Class<? extends edu.monash.fit2099.engine.items.Item> c) {
        return actor.getItemInventory().stream().anyMatch(c::isInstance);
    }
}
