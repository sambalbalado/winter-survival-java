package game.actions.playermorphactions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.app.Abilities;
import game.grounds.TreasureBox;
import game.items.ScentMarker;

import java.util.Random;
import java.util.WeakHashMap;

/**
 * Wolf-form utility that "sniffs out" nearby points of interest and spawns a {@link TreasureBox}.
 * <p>
 * The action scans within a Chebyshev radius for tiles that look "scent-worthy" (either because
 * the ground name suggests a notable biome—cave, tundra, swamp, meadow, tree—or the tile carries
 * items with treasure-like names). It then:
 * </p>
 * <ol>
 *   <li>Places a {@link TreasureBox} on a nearby free {@code '.'} tile (preferably adjacent to the
 *   scented target; otherwise adjacent to the player, then fallback to the target tile or the player's tile),
 *   and</li>
 *   <li>Drops a {@link ScentMarker} on the scented target, guiding the player toward it.</li>
 * </ol>
 * <p>
 * To avoid lingering breadcrumbs, the class tracks the last marker and last spawned box per-actor
 * using {@link WeakHashMap}s. If the previous box has been opened (i.e., that tile is no longer a
 * {@link TreasureBox}), the prior marker is cleared automatically.
 * </p>
 *
 * <ul>
 *   <li><b>Form gate:</b> Requires {@link Abilities#FORM_WOLF}.</li>
 *   <li><b>Distance reporting:</b> Reports approximate distance and compass direction toward the target.</li>
 *   <li><b>Safety:</b> Chests are placed only on free {@code '.'} tiles to avoid blocking actors.</li>
 * </ul>
 *
 * @version 1.0
 * @see Abilities#FORM_WOLF
 * @see TreasureBox
 * @see ScentMarker
 */
public class SmellTreasureAction extends Action {
    /**
     * Maximum Chebyshev distance to scan for a scented target; clamped to at least {@code 1}.
     */
    private final int radius;

    /**
     * Random source for tie-breaking and adjacent-tile selection.
     */
    private static final Random RNG = new Random();

    /**
     * Remembers the last tile where a {@link ScentMarker} was dropped for a given actor.
     * <p>
     * Uses weak keys so memory can be reclaimed when actors leave the game.
     * </p>
     */
    private static final WeakHashMap<Actor, Location> lastMarked = new WeakHashMap<>();

    /**
     * Remembers the last tile where a {@link TreasureBox} was spawned for a given actor.
     */
    private static final WeakHashMap<Actor, Location> lastBox = new WeakHashMap<>();

    /**
     * Creates a smell-treasure action with the specified scan radius.
     *
     * @param radius Chebyshev radius to search; values &le; 0 are treated as {@code 1}
     */
    public SmellTreasureAction(int radius) {
        this.radius = Math.max(1, radius);
    }

    /**
     * Executes the treasure-sniffing routine if the performer has {@link Abilities#FORM_WOLF}.
     * <p>
     * Steps:
     * </p>
     * <ol>
     *   <li>Clean up old marker if the previous box was already opened.</li>
     *   <li>Find the best scented tile within {@link #radius}.</li>
     *   <li>Choose a nearby free {@code '.'} to place a {@link TreasureBox}.</li>
     *   <li>Refresh the {@link ScentMarker} on the target (if any) to guide the player.</li>
     * </ol>
     *
     * @param actor the performer; must have {@link Abilities#FORM_WOLF}
     * @param map   the current map, used for locating neighbors and bounds
     * @return a descriptive string indicating placement and guidance, or a failure reason
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasAbility(Abilities.FORM_WOLF)) {
            return actor + " cannot smell treasure right now.";
        }

        // Clean trail if previous box is already opened (tile no longer a TreasureBox)
        cleanupOldTrail(actor);

        Location here = map.locationOf(actor);
        Location target = findScentedTile(here, map);
        int dist = (target == null) ? -1 : chebyshev(here, target);

        // Prefer placing the box next to the target (or next to the player if no target),
        // on a free '.' tile and not on the player's tile if possible.
        Location around = (target != null) ? target : here;
        Location placeAt = pickAdjacentDot(around, here);

        // Fallbacks: use target tile if '.' and free; else use player's tile if '.' and free
        if (placeAt == null) {
            if (target != null && isFreeDot(target)) placeAt = target;
            else if (isFreeDot(here)) placeAt = here;
        }

        if (placeAt != null) {
            placeAt.setGround(new TreasureBox());
            lastBox.put(actor, placeAt);
        }

        // Refresh marker on the scented target (if any)
        if (target != null) {
            removeMarkerAt(lastMarked.get(actor));
            target.addItem(new ScentMarker(6));
            lastMarked.put(actor, target);
        }

        if (placeAt == null) {
            return actor + " smells treasure but finds no space for a chest.";
        }

        if (target == null) {
            return actor + " sniffs the air but can't fix a direction. "
                    + "A Treasure Box appears at (" + placeAt.x() + "," + placeAt.y() + ").";
        } else {
            String dir = directionFrom(here, target);
            return actor + " sniffs something valuable " + dir + " (~" + dist + " tiles) at ("
                    + target.x() + "," + target.y() + "). "
                    + "A Treasure Box appears at (" + placeAt.x() + "," + placeAt.y() + "). "
                    + "Follow the '•' Scent Marker.";
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Presents this action as a wolf-form search ability.
     * </p>
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " → Smells for hidden treasure";
    }

    // --- helpers ---

    /**
     * Removes the previous {@link ScentMarker}, if any, when the last spawned {@link TreasureBox}
     * has been opened (i.e., the tile no longer hosts a TreasureBox).
     *
     * @param actor the actor whose breadcrumbs are being cleaned
     */
    private void cleanupOldTrail(Actor actor) {
        Location prevBox = lastBox.get(actor);
        if (prevBox != null) {
            if (!(prevBox.getGround() instanceof TreasureBox)) {
                removeMarkerAt(lastMarked.get(actor));
                lastMarked.remove(actor);
                lastBox.remove(actor);
            }
        }
    }

    /**
     * Removes a {@link ScentMarker} from the given location, if present.
     *
     * @param loc location to clear
     */
    private void removeMarkerAt(Location loc) {
        if (loc == null) return;
        loc.getItems().removeIf(i -> i instanceof ScentMarker);
    }

    /**
     * Checks whether a tile is a free {@code '.'} ground with no actor present.
     *
     * @param loc location to test
     * @return {@code true} if the tile is open for placing a chest/marker; otherwise {@code false}
     */
    private boolean isFreeDot(Location loc) {
        return loc.getGround() != null
                && loc.getGround().getDisplayChar() == '.'
                && !loc.containsAnActor();
    }

    /**
     * Chooses a random adjacent {@code '.'} tile to place a chest, avoiding the player's current tile.
     *
     * @param around the tile to search around (target or player)
     * @param avoid  a location to avoid (typically the player's tile)
     * @return a suitable adjacent {@code '.'} tile, or {@code null} if none
     */
    private Location pickAdjacentDot(Location around, Location avoid) {
        java.util.List<Exit> exits = new java.util.ArrayList<>(around.getExits());
        exits.removeIf(e -> {
            Location d = e.getDestination();
            return !isFreeDot(d) || (avoid != null && d == avoid);
        });
        if (exits.isEmpty()) return null;
        return exits.get(RNG.nextInt(exits.size())).getDestination();
    }

    /**
     * Finds the "best" scented tile within {@link #radius} around a center tile.
     * <p>
     * A tile is considered if it either contains treasure-like items (names containing
     * "treasure", "gem", "cube", "coin", "berry", "apple", "hazelnut") or its ground name
     * suggests a notable biome ("cave", "tundra", "swamp", "meadow", "tree"). Among candidates,
     * the closest by Chebyshev distance is selected.
     * </p>
     *
     * @param center origin of the search (typically the player's position)
     * @param map    the game map providing bounds and tiles
     * @return the chosen target location, or {@code null} if none found
     */
    private Location findScentedTile(Location center, GameMap map) {
        Location best = null;
        int bestDist = Integer.MAX_VALUE;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                if (dx == 0 && dy == 0) continue;
                int x = center.x() + dx, y = center.y() + dy;
                if (!map.getXRange().contains(x) || !map.getYRange().contains(y)) continue;

                Location there = map.at(x, y);
                String groundName = (there.getGround() == null) ? "" : there.getGround().toString().toLowerCase();

                boolean hasTreasureLikeItem = there.getItems().stream().anyMatch(i -> {
                    String n = i.toString().toLowerCase();
                    return n.contains("treasure") || n.contains("gem") || n.contains("cube")
                            || n.contains("coin") || n.contains("berry") || n.contains("apple")
                            || n.contains("hazelnut");
                });

                boolean groundLooksSpecial = groundName.contains("cave")
                        || groundName.contains("tundra")
                        || groundName.contains("swamp")
                        || groundName.contains("meadow")
                        || groundName.contains("tree");

                if (hasTreasureLikeItem || groundLooksSpecial) {
                    int d = Math.max(Math.abs(dx), Math.abs(dy)); // Chebyshev distance
                    if (d < bestDist) {
                        bestDist = d;
                        best = there;
                    }
                }
            }
        }
        return best;
    }

    /**
     * Computes Chebyshev distance between two locations.
     *
     * @param a first location
     * @param b second location
     * @return {@code max(|dx|, |dy|)}
     */
    private int chebyshev(Location a, Location b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }

    /**
     * Rough compass direction from {@code a} to {@code b}.
     *
     * @param a origin
     * @param b destination
     * @return human-readable direction (e.g., "North-East") or "nearby" if ambiguous
     */
    private static String directionFrom(Location a, Location b) {
        int dx = Integer.compare(b.x(), a.x());
        int dy = Integer.compare(b.y(), a.y());
        if (dx == 0 && dy < 0) return "North";
        if (dx > 0 && dy < 0)   return "North-East";
        if (dx > 0 && dy == 0)  return "East";
        if (dx > 0 && dy > 0)   return "South-East";
        if (dx == 0 && dy > 0)  return "South";
        if (dx < 0 && dy > 0)   return "South-West";
        if (dx < 0 && dy == 0)  return "West";
        if (dx < 0 && dy < 0)   return "North-West";
        return "nearby";
    }
}
