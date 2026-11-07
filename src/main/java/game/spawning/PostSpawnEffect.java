// file: game/spawning/effects/PostSpawnEffect.java
package game.spawning;

import java.util.Random;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Strategy applied immediately after an animal is spawned.
 * <p>Examples: drop items nearby, poison neighbours, trigger terrain effects.</p>
 *
 * @author Sai Sann Lone
 * @version 1.0
 */

@FunctionalInterface
public interface PostSpawnEffect {
    /**
     * Applies a post-spawn side effect.
     *
     * @param spawnerLocation the location where the spawn occurred
     * @param spawnedActor    the newly spawned actor
     * @param rng             randomness source for probabilistic effects
     */
    void apply(Location spawnerLocation, Actor spawnedActor, Random rng);
}
