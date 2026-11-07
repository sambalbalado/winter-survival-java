package game.actions.playermorphactions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import game.app.Abilities;
import game.flora.WildAppleTree;
import game.flora.YewBerryTree;
import game.flora.HazelnutTree;

import game.items.Apple;
import game.items.YewBerry;
import game.items.Hazelnut;

import java.util.List;
import java.util.Random;

/**
 * Shakes adjacent trees to force bonus fruit drops; usable only in {@link Abilities#FORM_BEAR}.
 * <p>
 * For each neighboring tile that contains a {@link WildAppleTree}, {@link YewBerryTree}, or {@link HazelnutTree},
 * this action mimics the placement logic of {@code WildAppleTree.maybeDrop}: it repeatedly selects a random adjacent
 * exit around the tree and drops fruit on tiles whose ground display char is {@code '.'}.
 * The number of drop attempts per tree is {@code baseMultiplier..baseMultiplier+maxExtra}.
 * </p>
 *
 * <ul>
 *   <li><b>Eligibility:</b> Performer must have {@link Abilities#FORM_BEAR}.</li>
 *   <li><b>AoE:</b> Checks all immediate neighbors of the performer for tree grounds.</li>
 *   <li><b>Fruits:</b> Apple for {@link WildAppleTree}, YewBerry for {@link YewBerryTree}, Hazelnut for
 *   {@link HazelnutTree} (falls back to Apple if construction fails).</li>
 * </ul>
 *
 * @author Taiyo Arima
 * @version 1.0
 * @see WildAppleTree
 * @see YewBerryTree
 * @see HazelnutTree
 */
public class ShakeTreeAction extends Action {

    /**
     * The guaranteed minimum number of drop attempts per adjacent tree.
     */
    private final int baseMultiplier; // e.g. 2

    /**
     * The additional random attempts (inclusive), making total attempts in
     * {@code [baseMultiplier, baseMultiplier + maxExtra]}.
     */
    private final int maxExtra;       // e.g. 1  (so total is 2..3)

    /**
     * RNG used for random exit selection and attempt counts.
     */
    private static final Random rng = new Random();

    /**
     * Creates a shake-tree action with the given drop attempt parameters.
     *
     * @param baseMultiplier minimum number of fruit-drop attempts per adjacent tree
     * @param maxExtra       maximum extra attempts added uniformly at random (inclusive)
     */
    public ShakeTreeAction(int baseMultiplier, int maxExtra) {
        this.baseMultiplier = baseMultiplier;
        this.maxExtra = maxExtra;
    }

    /**
     * Executes the shake effect if the performer is in {@link Abilities#FORM_BEAR}.
     * <p>
     * Iterates all adjacent tiles, detects supported tree grounds, and calls
     * {@link #dropLikeMaybeDrop(Location, FruitFactory)} to perform randomized drops.
     * Returns a summary of total fruits dropped or an explanation if no eligible trees were found.
     * </p>
     *
     * @param actor the performer; must have {@link Abilities#FORM_BEAR}
     * @param map   the current map, used to resolve position and neighbors
     * @return a result string describing the number of bonus fruits dropped, or the failure reason
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Gate by form-flag. If you're gating by PlayerForm instead, remove this check.
        if (!actor.hasAbility(Abilities.FORM_BEAR)) {
            return actor + " cannot shake trees right now.";
        }

        Location here = map.locationOf(actor);
        int total = 0;

        // Iterate adjacent tiles and apply per-tree shake
        for (Exit e : here.getExits()) {
            Location treeLoc = e.getDestination();
            var ground = treeLoc.getGround();

            int dropsForThisTree = 0;
            if (ground instanceof WildAppleTree) {
                dropsForThisTree = dropLikeMaybeDrop(treeLoc, Apple::new);
            } else if (ground instanceof YewBerryTree) {
                dropsForThisTree = dropLikeMaybeDrop(treeLoc, YewBerry::new);
            } else if (ground instanceof HazelnutTree) {
                // Use Hazelnut if you have it; otherwise fallback to Apple.
                dropsForThisTree = dropLikeMaybeDrop(treeLoc, () -> {
                    try { return new Hazelnut(); }
                    catch (Throwable __ignore) { return new Apple(); }
                });
            }
            total += dropsForThisTree;
        }

        return total > 0
                ? "Trees sway — bonus fruit drops: " + total
                : "No shakeable trees adjacent.";
    }

    /**
     * {@inheritDoc}
     * <p>
     * Presents this action as a bear-only utility for forcing fruit drops.
     * </p>
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " → Shake nearby tree)";
    }

    // --- helpers ---

    /**
     * Emulates {@code WildAppleTree.maybeDrop} placement:
     * choose a random adjacent {@link Exit} from the tree tile and, if the destination ground's display
     * character is {@code '.'}, place a newly created fruit item there.
     * <p>
     * The number of attempts is uniformly selected in
     * {@code [baseMultiplier, baseMultiplier + maxExtra]}. Failures (e.g., blocked ground, exceptions
     * from fruit creation) are ignored, and the method continues attempting remaining drops.
     * </p>
     *
     * @param treeLoc the location of the tree ground being shaken
     * @param fruit   factory that provides a new fruit instance for each successful drop
     * @return the count of fruits successfully dropped
     */
    private int dropLikeMaybeDrop(Location treeLoc, FruitFactory fruit) {
        int repeated = baseMultiplier + rng.nextInt(maxExtra + 1);
        int success = 0;

        List<Exit> exits = treeLoc.getExits();
        if (exits.isEmpty()) return 0;

        for (int i = 0; i < repeated; i++) {
            Exit randomExit = exits.get(rng.nextInt(exits.size()));
            Location dest = randomExit.getDestination();

            try {
                // Match maybeDrop rule: only drop when ground shows '.'
                if (dest.getGround() != null && dest.getGround().getDisplayChar() == '.') {
                    dest.addItem(fruit.make());
                    success++;
                }
            } catch (Throwable __ignore) {
                // Ignore and continue trying remaining repeats
            }
        }
        return success;
    }

    /**
     * Factory abstraction for producing fruit items on demand.
     */
    @FunctionalInterface
    private interface FruitFactory {
        edu.monash.fit2099.engine.items.Item make();
    }
}
