package game;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Apple;
import game.items.Hazelnut;
import game.items.YewBerry;
import game.flora.WildAppleTree;
import game.flora.Stage;
import game.environment.Snow;
import game.flora.HazelnutTree;
import game.flora.YewBerryTree;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Mature-tree regression checks retained from the earlier portfolio. */
class TreeProductionTest {

    @Test
    void appleTreeProducesOneAppleEveryThreeTurns() throws Exception {
        GameMap map = snowMap();
        Location treeLocation = map.at(1, 1);
        treeLocation.setGround(new WildAppleTree(Stage.TREE));

        tick(treeLocation, 3);

        assertEquals(1, countItems(map, Apple.class));
    }

    @Test
    void hazelnutTreeProducesOneHazelnutEveryTenTurns() throws Exception {
        GameMap map = snowMap();
        Location treeLocation = map.at(1, 1);
        treeLocation.setGround(new HazelnutTree());

        tick(treeLocation, 10);

        assertEquals(1, countItems(map, Hazelnut.class));
    }

    @Test
    void yewTreeProducesOneBerryEveryFiveTurns() throws Exception {
        GameMap map = snowMap();
        Location treeLocation = map.at(1, 1);
        treeLocation.setGround(new YewBerryTree(Stage.TREE));

        tick(treeLocation, 5);

        assertEquals(1, countItems(map, YewBerry.class));
    }

    private GameMap snowMap() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Snow::new);
        return new GameMap("Forest", groundCreator, List.of("...", "...", "..."));
    }

    private void tick(Location location, int turns) {
        for (int i = 0; i < turns; i++) {
            location.tick();
        }
    }

    private long countItems(GameMap map, Class<? extends Item> itemType) {
        long count = 0;
        for (int x : map.getXRange()) {
            for (int y : map.getYRange()) {
                count += map.at(x, y).getItems().stream().filter(itemType::isInstance).count();
            }
        }
        return count;
    }
}
