package game.actions.potionactions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.potions.Potion;
import game.potions.potionEffects.AreaEffect;

/**
 * Throws a {@link Potion} in a straight line up to a capped range and applies its {@link AreaEffect}
 * at the final impact tile.
 * <p>
 * The action performs a step-by-step raycast starting from the performer’s position in the chosen
 * {@link ThrowDirection}, clamping the impact to remain within the map and stopping early if a tile
 * contains an actor (optional collision rule). After finding the impact location, it retrieves the
 * potion’s {@link AreaEffect} and applies it at that tile, then consumes the specific potion instance.
 * </p>
 *
 * <ul>
 *   <li><b>Direction:</b> Uses {@link ThrowDirection} for unit deltas and menu-friendly labels.</li>
 *   <li><b>Range clamp:</b> {@code maxRange} is enforced to be at least 1 and respected by the raycast.</li>
 *   <li><b>Impact rules:</b> Stops before leaving bounds; may stop on actor collision (customize as needed).</li>
 *   <li><b>Effect:</b> Delegates to {@link AreaEffect#apply(GameMap, Location)} at the impact tile.</li>
 *   <li><b>Consumption:</b> Removes the exact thrown {@link Potion} from the inventory on success.</li>
 * </ul>
 *
 * @author Taiyo Arima
 * @version 1.0
 * @see Potion
 * @see AreaEffect
 * @see ThrowDirection
 */
public class ThrowPotionAction extends Action {

    /**
     * The exact potion instance being thrown (and consumed).
     */
    private final Potion potion;

    /**
     * The cardinal direction of travel for the thrown potion.
     */
    private final ThrowDirection dir;

    /**
     * Maximum number of tiles the projectile can traverse; guaranteed {@code >= 1}.
     */
    private final int maxRange;

    /**
     * Cached reference to the effect of this potion (read from the potion on construction).
     */
    private final AreaEffect effect;

    /**
     * Constructs a throw action for a specific {@link Potion}, direction, and range cap.
     *
     * @param potion   the concrete potion instance to throw (and consume)
     * @param dir      the direction of travel
     * @param maxRange maximum steps to attempt; clamped to {@code >= 1}
     */
    public ThrowPotionAction(Potion potion, ThrowDirection dir, int maxRange) {
        this.potion = potion;
        this.dir = dir;
        this.maxRange = Math.max(1, maxRange);
        this.effect = potion.getAreaEffect();
    }

    /**
     * Executes the throw: raycasts to an impact tile, applies the potion’s AoE, and consumes the item.
     *
     * @param actor the performer hurling the potion
     * @param map   the active map
     * @return a short log message describing the throw
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Step-by-step raycast, never OOB
        Location impact = raycastClamped(map, actor, dir.dx(), dir.dy(), maxRange);

        // Apply the potion’s AoE at the clamped impact
        AreaEffect effect = potion.getAreaEffect();
        effect.apply(map, impact);

        // (If consumable)
        try {
            actor.removeItemFromInventory(potion);
        } catch (Throwable ignored) {
            // Swallow in case the item was already removed elsewhere
        }

        return actor + " throws " + potion + " towards " + dir.pretty() + ".";
    }

    /**
     * {@inheritDoc}
     * <p>
     * Presents the action with a compact arrow style and range hint to match other morph/throw menus.
     * </p>
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " → Throw " + potion + " " + dir.pretty() + " (" + maxRange + ")";
    }

    // --- helpers -------------------------------------------------------------

    /**
     * Marches from the actor’s tile along {@code (dx, dy)} up to {@code range} steps, stopping early if
     * a step would go out of bounds or (optionally) collide with an actor. Returns the last valid tile.
     *
     * @param map    the map
     * @param actor  the starting actor
     * @param dx     x-axis unit delta per step
     * @param dy     y-axis unit delta per step
     * @param range  maximum steps to attempt
     * @return the final in-bounds, non-colliding location along the path
     */
    private Location raycastClamped(GameMap map, Actor actor, int dx, int dy, int range) {
        Location here = map.locationOf(actor);
        Location last = here;
        int x = here.x(), y = here.y();

        for (int step = 0; step < range; step++) {
            int nx = x + dx, ny = y + dy;
            try {
                Location next = map.at(nx, ny);   // throws if OOB
                if (next.containsAnActor()) break; // optional: collide with actors
                last = next;
                x = nx; y = ny;
            } catch (Throwable oob) {
                break; // stop before leaving the map
            }
        }
        return last;
    }

    /**
     * Returns whether a projectile may enter {@code loc}. Customize with world-specific rules
     * (e.g., walls, rocks, or actors block). Currently unused by {@link #raycastClamped} but kept
     * as a hook for future line-of-flight checks.
     */
    @SuppressWarnings("unused")
    private boolean canProjectileEnter(Location loc, Actor actor) {
        try {
            if (loc.containsAnActor()) return false;
            // Example: if certain grounds block projectiles, check a capability here.
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Chebyshev (king-move) distance between two locations, useful for range validation.
     */
    @SuppressWarnings("unused")
    private int stepsFrom(Location a, Location b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }
}
