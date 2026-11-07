package game;

import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import edu.monash.fit2099.engine.displays.Display;
import game.environment.Snow;
import java.util.List;
import game.actions.TameAction;
import game.actions.ConsumeAction;
import game.app.Abilities;
import game.creatures.Bear;
import game.characters.Player;
import game.items.Apple;
import game.items.Bottle;
import game.items.Hazelnut;
import game.items.YewBerry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Regression checks retained from the earlier portfolio and adapted to the team APIs. */
class GameMechanicsTest {

    @Test
    void appleRestoresHealthAndHydrationThenLeavesInventory() {
        Player player = new Player();
        Apple apple = new Apple();
        player.addItemToInventory(apple);
        player.hurt(10);
        player.modifyAttribute(Player.ExplorerAttributes.HYDRATION, ActorAttributeOperation.DECREASE, 5);

        new ConsumeAction(apple, apple).execute(player, null);

        assertEquals(993, player.getAttribute(BaseAttributes.HEALTH));
        assertEquals(997, player.getAttribute(Player.ExplorerAttributes.HYDRATION));
        assertFalse(player.getItemInventory().contains(apple));
    }

    @Test
    void hazelnutIncreasesMaximumHealth() {
        Player player = new Player();
        Hazelnut hazelnut = new Hazelnut();
        player.addItemToInventory(hazelnut);

        new ConsumeAction(hazelnut, hazelnut).execute(player, null);

        assertEquals(1001, player.getMaximumAttribute(BaseAttributes.HEALTH));
        assertFalse(player.getItemInventory().contains(hazelnut));
    }

    @Test
    void yewBerryIsConsumedAndKnocksActorOut() throws Exception {
        Player player = new Player();
        YewBerry berry = new YewBerry();
        player.addItemToInventory(berry);

        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Snow::new);
        GameMap map = new GameMap("Forest", groundCreator, List.of("..."));
        World world = new World(new Display()) {};
        world.addGameMap(map);
        world.addPlayer(player, map.at(1, 0));
        new ConsumeAction(berry, berry).execute(player, map);

        assertEquals(0, player.getAttribute(BaseAttributes.HEALTH));
        assertFalse(player.getItemInventory().contains(berry));
        assertFalse(player.isConscious());
    }

    @Test
    void bottleHasFiveHydrationUses() {
        Player player = new Player();
        Bottle bottle = new Bottle();
        player.modifyAttribute(Player.ExplorerAttributes.HYDRATION, ActorAttributeOperation.DECREASE, 20);

        for (int i = 0; i < 5; i++) {
            bottle.drunkBy(player);
        }
        String emptyResult = bottle.drunkBy(player);

        assertEquals(1000, player.getAttribute(Player.ExplorerAttributes.HYDRATION));
        assertTrue(emptyResult.toLowerCase().contains("empty"));
    }

    @Test
    void tamingConsumesFoodAndAssignsOwner() {
        Player player = new Player();
        Bear bear = new Bear();
        Apple apple = new Apple();
        player.addItemToInventory(apple);

        new TameAction(bear, apple).execute(player, null);

        assertTrue(bear.isTamed());
        assertSame(player, bear.getOwner());
        assertFalse(bear.hasAbility(Abilities.PREDATOR));
        assertFalse(player.getItemInventory().contains(apple));
    }
}
