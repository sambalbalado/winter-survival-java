package game;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.actors.Bear;
import game.actors.Deer;
import game.actors.Player;
import game.actors.Wolf;
import game.trees.AppleTree;
import game.trees.HazelnutTree;
import game.trees.YewBerryTree;

import java.util.List;

/**
 * Represents the Earth world for the Explorer game.
 * <p>
 * This class is responsible for constructing the game map, adding actors (Player, Deer, Wolf, Bear),
 * and placing special terrain features like trees (AppleTree, HazelnutTree, YewBerryTree) on the map.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class Earth extends World {

    /**
     * Constructor for Earth world.
     *
     * @param display the display to use for the world
     */
    public Earth(Display display) {
        super(display);
    }

    /**
     * Constructs the game world by creating the map, adding actors, and setting up special trees.
     *
     * @throws Exception if an error occurs during map or actor creation
     */
    public void constructWorld() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Snow::new);

        List<String> map = List.of(
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................"
        );

        GameMap gameMap = new GameMap("Forest", groundCreator, map);
        this.addGameMap(gameMap);

        Player player = new Player("Explorer", '@', 100);
        this.addPlayer(player, gameMap.at(1, 1));

        Deer deer = new Deer("Deer",'d', 50);
        gameMap.at(2,1).addActor(deer);

        Wolf wolf = new Wolf("Wolf", 'e', 100);
        gameMap.at(18,2).addActor(wolf);

        Bear bear = new Bear("Bear", 'B',200);
        gameMap.at(15,2).addActor(bear);

        gameMap.at(10, 5).setGround(new AppleTree());
        gameMap.at(1,3).setGround(new HazelnutTree());
        gameMap.at(12,0).setGround(new YewBerryTree());
    }
}
