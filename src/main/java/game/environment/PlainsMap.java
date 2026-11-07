package game.environment;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import game.api.LunarPhaseManager;
import game.api.MoonPhase;
import game.api.MoonPhaseService;
import game.api.OpenMeteoService;
import game.grounds.Tundra;
import game.grounds.Cave;
import game.grounds.Meadow;
import game.grounds.Swamp;

import game.environment.Snow;
//import game.creatures.AnimalGameMap;

import java.util.Arrays;
import java.util.List;

/**
 * Creates the "Plains" map (40x10).
 * Legend: '.' Snow, '_' Tundra, 'C' Cave, 'w' Meadow, '~' Swamp.
 * Most tiles start as Snow; special grounds are placed by coordinates.
 * @author Sai Sann Lone
 * @version 2.0
 */

public final class PlainsMap {
    private PlainsMap() {}


    /**
     * Creates and returns the "Plains" {@link GameMap}.
     *
     * @return the configured Plains map
     * @throws GameEngineException if an unknown glyph is used in the layout
     */

    public static GameMap create() throws GameEngineException {
        // Use engine's ground creation system properly
        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('.', Snow::new);
        creator.registerGround('_', Tundra::new);
        creator.registerGround('C', Cave::new);
        creator.registerGround('w', Meadow::new);
        creator.registerGround('~', Swamp::new);

        List<String> layout = Arrays.asList(
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

        GameMap map = new GameMap("Plains", creator, layout);

        // Coordinate-based ground placement


        // Tundra
        map.at(31, 1).setGround(new Tundra());
        map.at(14, 2).setGround(new Tundra());
        map.at(25, 6).setGround(new Tundra());
        map.at(14, 9).setGround(new Tundra());

        // Caves
        map.at(31, 3).setGround(new Cave());
        map.at(23, 3).setGround(new Cave());
        map.at(18, 9).setGround(new Cave());
        map.at(15, 7).setGround(new Cave());

        // Meadows
        map.at(21, 2).setGround(new Meadow());
        map.at(8, 4).setGround(new Meadow());
        map.at(31, 7).setGround(new Meadow());


        // Swamps
        map.at(24, 1).setGround(new Swamp());
        map.at(19, 3).setGround(new Swamp());
        map.at(4, 7).setGround(new Swamp());

        return map;
    }
}
