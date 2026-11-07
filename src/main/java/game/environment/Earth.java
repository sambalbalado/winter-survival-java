package game.environment;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.api.LunarPhaseManager;
import game.api.MoonPhaseService;
import game.api.OpenMeteoService;
import game.characters.Player;
import game.creatures.Deer;
import game.creatures.Wolf;
import game.creatures.*;
import game.flora.HazelnutTree;
import game.flora.WildAppleTree;
import game.flora.YewBerryTree;
import game.grounds.Cave;
import game.grounds.Meadow;
import game.grounds.Tundra;
import game.grounds.Swamp;
import game.items.Bedroll;
import game.items.Bottle;
import game.items.TeleportCube;
import game.items.*;
import game.monsters.Dragon;
import game.grounds.CraftingTable;

import game.teleport.*;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the Earth world environment in the game.
 * <li>Create the Forest and Plains maps</li>
 * <li>Register terrain (Snow, Tundra, Cave, Meadow, Swamp)</li>
 * <li>Place flora, NPCs (animals/monsters), items, and teleporters</li>
 * <li>Spawn the player with starter equipment</li>
 * <li>Initialize the lunar phase manager</li>
 * @author Sai Sann Lone
 * @version 2.0
 */
public class Earth extends World {

    private LunarPhaseManager lunarManager;


    /**
     * Constructs a new Earth world instance.
     *
     * @param display the display interface used to render the game world
     */
    public Earth(Display display) {
        super(display);
    }

    /**
     * Builds and wires up the entire world state.
     * <p>Steps performed (high level):</p>
     * <ol>
     *   <li>Register ground glyphs and create the Forest map</li>
     *   <li>Create the Plains map via {@link PlainsMap#create()}</li>
     *   <li>Place flora, spawning grounds, items, and NPCs</li>
     *   <li>Add teleport fixtures and a teleport cube with destinations</li>
     *   <li>Create the player and add starter items (bedroll, bottle, cube)</li>
     *   <li>Initialize and set the global lunar phase manager</li>
     * </ol>
     *
     * @throws Exception if any world construction step fails
     */
    public void constructWorld() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Snow::new);
        groundCreator.registerGround('_', Tundra::new);    // Add tundra spawning
        groundCreator.registerGround('C', Cave::new);      // Add cave spawning
        groundCreator.registerGround('w', Meadow::new);
        groundCreator.registerGround('~', Swamp::new);



        List<String> map = Arrays.asList(
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

        // Flora

        gameMap.at(23, 2).setGround(new WildAppleTree());
        gameMap.at(36, 5).setGround(new HazelnutTree());
        gameMap.at(18, 7).setGround(new YewBerryTree());

        // Second map
        GameMap plainsMap = PlainsMap.create();
        this.addGameMap(plainsMap);

        plainsMap.at(15, 3).setGround(new WildAppleTree());
        plainsMap.at(8, 2).setGround(new HazelnutTree());
        plainsMap.at(25, 6).setGround(new YewBerryTree());

        // Player + starter items
        Player explorer = new Player();
        this.addPlayer(explorer, gameMap.at(9, 7));

        explorer.addItemToInventory(new Bedroll(explorer));

        Bottle bottle = new Bottle();
        bottle.setDrinker(explorer);
        explorer.addItemToInventory(bottle);


        TeleportCube cube = new TeleportCube();
        explorer.addItemToInventory(cube);

        gameMap.at(23, 2).setGround(new WildAppleTree());
        gameMap.at(16, 5).setGround(new HazelnutTree());
        gameMap.at(38, 7).setGround(new YewBerryTree());


        // Add Wolf
        gameMap.addActor(new Wolf(),gameMap.at(19,8));

        // Added monster (dragon)
        gameMap.addActor(new Dragon(), gameMap.at(29,6));

        // Add Bear
        gameMap.addActor(new Bear(), gameMap.at(39,5));

        // Add Deer
        gameMap.addActor(new Deer(), gameMap.at(8,5));

        //Teleport fixtures
        TeleportDoor doorForest = new TeleportDoor();
        TeleportCircle circleForest = new TeleportCircle();
        TeleportDoor doorPlains = new TeleportDoor();
        TeleportCircle circlePlains = new TeleportCircle();

        doorForest.addDestination(gameMap, 30, 4, "Forest (30,4)")
                .addDestination(plainsMap, 10, 2, "Plains (10,2)");
        circleForest.addDestination(gameMap, 5, 6, "Forest (5,6)")
                .addDestination(plainsMap, 20, 1, "Plains (20,1)");

        doorPlains.addDestination(plainsMap, 7, 3, "Plains (7,3)")
                .addDestination(gameMap, 2, 8, "Forest (2,8)");
        circlePlains.addDestination(plainsMap, 15, 0, "Plains (15,0)")
                .addDestination(gameMap, 18, 7, "Forest (18,7)");

        gameMap.at(2, 2).setGround(doorForest);
        gameMap.at(12, 6).setGround(circleForest);
        plainsMap.at(3, 1).setGround(doorPlains);
        plainsMap.at(22, 5).setGround(circlePlains);

        cube
            .addDestination(gameMap, 1, 1, "Forest (1,1)")
            .addDestination(plainsMap, 10, 2, "Plains (10,2)");

        // Place spawning terrain at specific coordinates
        gameMap.at(25, 1).setGround(new Tundra());     // Tundra spawns Bears (5% chance)
        gameMap.at(12, 3).setGround(new Tundra());    // Another tundra location
        gameMap.at(20, 0).setGround(new Cave());      // Cave spawns Bear/Wolf/Deer (every 5 turns)
        gameMap.at(34, 6).setGround(new Cave());       // Another cave location
        gameMap.at(1, 2).setGround(new Meadow());
        gameMap.at(22, 9).setGround(new Meadow());
        gameMap.at(27, 4).setGround(new Swamp());
        gameMap.at(11, 5).setGround(new Swamp());


        // Ground items
        gameMap.at(5, 5).addItem(new YewBerry());  // Where meadow deer spawn
        gameMap.at(10, 8).addItem(new Apple());     // Where meadow deer spawn
        gameMap.at(26, 8).addItem(new Hazelnut());

        MoonPhaseService apiService = new OpenMeteoService();
        lunarManager = new LunarPhaseManager(apiService);
        lunarManager.initialize();  // Fetch moon phase from API

        LunarPhaseManager.setInstance(lunarManager);




        gameMap.at(9, 9).setGround(new CraftingTable());
        plainsMap.at(9, 9).setGround(new CraftingTable());

        player.addItemToInventory(new Apple());
        player.addItemToInventory(new YewBerry());
        player.addItemToInventory(new Hazelnut());

    }



}
