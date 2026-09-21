package game;

import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import game.actions.TameAction;
import game.actors.Bear;
import game.actors.Player;
import game.items.Apple;
import game.items.Bottle;
import game.items.Hazelnut;
import game.items.YewBerry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameMechanicsTest {

    @Test
    void appleRestoresHealthAndHydrationThenLeavesInventory() {
        Player player = new Player("Explorer", '@', 100);
        Apple apple = new Apple();
        player.addItemToInventory(apple);
        player.hurt(10);
        player.modifyAttribute(GameAttributes.HYDRATION, ActorAttributeOperation.DECREASE, 5);

        apple.eatenBy(player);

        assertEquals(93, player.getAttribute(BaseAttributes.HEALTH));
        assertEquals(17, player.getAttribute(GameAttributes.HYDRATION));
        assertFalse(player.getItemInventory().contains(apple));
    }

    @Test
    void hazelnutIncreasesMaximumHealth() {
        Player player = new Player("Explorer", '@', 100);
        Hazelnut hazelnut = new Hazelnut();
        player.addItemToInventory(hazelnut);

        hazelnut.eatenBy(player);

        assertEquals(101, player.getMaximumAttribute(BaseAttributes.HEALTH));
        assertFalse(player.getItemInventory().contains(hazelnut));
    }

    @Test
    void yewBerryIsConsumedAndKnocksActorOut() {
        Player player = new Player("Explorer", '@', 100);
        YewBerry berry = new YewBerry();
        player.addItemToInventory(berry);

        berry.eatenBy(player);

        assertEquals(0, player.getAttribute(BaseAttributes.HEALTH));
        assertFalse(player.getItemInventory().contains(berry));
        assertFalse(player.isConscious());
    }

    @Test
    void bottleHasFiveHydrationUses() {
        Player player = new Player("Explorer", '@', 100);
        Bottle bottle = new Bottle("Bottle", 'o', true);
        player.modifyAttribute(GameAttributes.HYDRATION, ActorAttributeOperation.DECREASE, 20);

        for (int i = 0; i < 5; i++) {
            bottle.drunkBy(player);
        }
        String emptyResult = bottle.drunkBy(player);

        assertEquals(20, player.getAttribute(GameAttributes.HYDRATION));
        assertTrue(emptyResult.contains("empty"));
    }

    @Test
    void tamingConsumesFoodAndAssignsOwner() {
        Player player = new Player("Explorer", '@', 100);
        Bear bear = new Bear("Bear", 'B', 200);
        Apple apple = new Apple();
        player.addItemToInventory(apple);

        new TameAction(bear, apple).execute(player, null);

        assertTrue(bear.isTamed());
        assertSame(player, bear.getOwner());
        assertFalse(bear.hasAbility(Abilities.PREDATOR));
        assertFalse(player.getItemInventory().contains(apple));
    }
}
