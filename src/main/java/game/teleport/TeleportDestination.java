package game.teleport;

import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Represents a fixed teleport destination consisting of a {@link GameMap},
 * coordinate pair, and descriptive label.
 * <p>
 * The {@code TeleportDestination} record:
 * <ul>
 *     <li>Encapsulates the target map and coordinates for teleportation</li>
 *     <li>Is immutable and used by all {@link Teleporter} implementations</li>
 *     <li>Provides a readable label for user-facing messages</li>
 * </ul>
 * </p>
 * <p>
 * Instances of this record are typically registered by teleporters such as
 * {@link game.items.TeleportCube} or {@link game.teleport.TeleportCircle}.
 * </p>
 *
 * @param map   the {@link GameMap} representing the destination world or area
 * @param x     the x-coordinate of the destination location
 * @param y     the y-coordinate of the destination location
 * @param label a human-readable label identifying this destination
 *
 * @author Taiyo
 * @version 1.0
 */
public record TeleportDestination(
        GameMap map,
        int x,
        int y,
        String label
) {}
