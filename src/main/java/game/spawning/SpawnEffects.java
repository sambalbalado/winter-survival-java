package game.spawning;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import game.spawning.AnimalRegistry.AnimalType;
import game.spawning.*;

/**
 * Registry/dispatcher for post-spawn effects keyed by {@link AnimalType}.
 * <p>Pre-registers defaults (Deer → apple, Bear → berry scatter, Wolf → tree growth, Crocodile → poison).
 * Open for extension via {@link #register(AnimalType, PostSpawnEffect)}.</p>
 *
 * @author Sai Sann Lone
 * @version 1.0
 */
public final class SpawnEffects {

    private final Random rng;
    private final Map<AnimalType, PostSpawnEffect> effects = new EnumMap<>(AnimalType.class);

    public SpawnEffects() { this(new Random()); }

    /**
     * Creates a registry with the provided RNG and registers default effects.
     *
     * @param rng randomness source used by effects
     */
    public SpawnEffects(Random rng) {
        this.rng = Objects.requireNonNull(rng);
        // Default registrations (open for extension)
        register(AnimalType.DEER, new DeerAppleEffect());
        register(AnimalType.BEAR, new BearBerryScatterEffect());
        register(AnimalType.WOLF, new WolfTreeGrowthEffect());
        register(AnimalType.CROCODILE, new CrocodilePoisonEffect());
    }

    /**
     * Registers or replaces the effect for a given animal type.
     *
     * @param type   animal type key
     * @param effect effect implementation
     */

    public void register(AnimalType type, PostSpawnEffect effect) {
        effects.put(Objects.requireNonNull(type), Objects.requireNonNull(effect));
    }

    /**
     * Applies the post-spawn effect for the given type, if registered.
     * No-op when no effect is associated with the type.
     *
     * @param type            the spawned animal type
     * @param spawnerLocation the location where spawning occurred
     * @param spawnedActor    the spawned actor
     */

    public void apply(AnimalType type, Location spawnerLocation, Actor spawnedActor) {
        PostSpawnEffect effect = effects.get(type);
        if (effect != null) {
            effect.apply(spawnerLocation, spawnedActor, rng);
        }
        // else: no post-spawn effect for this type → intentionally no-op
    }
}
