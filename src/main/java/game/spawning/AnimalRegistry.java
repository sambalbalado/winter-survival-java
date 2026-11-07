// AnimalRegistry.java
package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.GameEngineException;
import game.creatures.Bear;
import game.creatures.Deer;
import game.creatures.Wolf;
import game.creatures.Crocodile;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A central registry for creating animals in the game.
 * <p>
 * Works like the engine’s {@code DefaultGroundCreator}, allowing new animal
 * types to be registered and created dynamically. This design makes the
 * spawning system extensible without modifying existing code.
 *
 * @author Sai Sann Lone
 * @version 1.1
 */
public class AnimalRegistry {

    /** Supported animal types. */
    public enum AnimalType {
        BEAR, DEER, WOLF, CROCODILE
        // Future: RABBIT, FOX, EAGLE, SNAKE, etc.
    }

    /** Stores mappings from animal type to its creation function. */
    private static final Map<AnimalType, Supplier<Actor>> registry = new HashMap<>();

    static {
        // Register default animals following engine initialization patterns
        registry.put(AnimalType.BEAR, Bear::new);
        registry.put(AnimalType.DEER, Deer::new);
        registry.put(AnimalType.WOLF, Wolf::new);
        registry.put(AnimalType.CROCODILE, Crocodile::new);
    }

    /**
     * Creates a new animal instance based on its registered type.
     * <p>
     * Returns {@code null} if the type is not found, following the engine’s
     * defensive handling pattern.
     *
     * @param type the animal type to create
     * @return a new {@link Actor} instance, or {@code null} if unregistered
     */
    public static Actor createAnimal(AnimalType type) {
        if (type == null) {
            return null;
        }
        Supplier<Actor> supplier = registry.get(type);
        return supplier != null ? supplier.get() : null;
    }

    /**
     * Registers a new animal type at runtime.
     * <p>
     * Throws an exception if the type is already registered, ensuring no duplicates.
     *
     * @param type      the animal type to register
     * @param supplier  a factory function that creates the animal
     * @throws GameEngineException if the type is already registered
     */
    public static void registerAnimal(AnimalType type, Supplier<Actor> supplier) throws GameEngineException {
        if (registry.containsKey(type)) {
            throw new GameEngineException("Animal type '" + type + "' already registered.");
        }
        registry.put(type, supplier);
    }
}
