package game.weapons;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.actions.CoatWeaponAction;
import game.actions.RangedAttackAction;
import game.app.Abilities;
import game.capabilities.CoatableWeapon;
import game.capabilities.WeaponWithActions;
import game.items.Coatable;
import game.api.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Represents a Bow weapon for ranged combat.
 *
 * The Bow deals 5 damage with a 25% hit rate and can attack targets within 3 tiles range.
 * It can be coated with special materials:
 * - Snow coating: Inflicts FrostBitten status
 * - Yewberry coating: Inflicts Poisoned status
 *
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public class Bow extends IntrinsicWeapon implements CoatableWeapon, WeaponWithActions, Coatable {

    /**
     * Random number generator for chance calculations
     */
    private static final Random random = new Random();

    /**
     * The current coating applied to the bow
     */
    private String coating;

    /**
     * Constructor for Bow.
     * Creates a bow with 5 damage, 25% hit rate, and no initial coating.
     */
    public Bow() {
        super(5, "shoots", 25, "bow");
        this.coating = null;
    }

    /**
     * Determines if this weapon can attack at range.
     *
     * @return true, as the bow is a ranged weapon
     */
    public boolean isRangedWeapon() {
        return true;
    }

    /**
     * Determines if this bow can be coated.
     *
     * @return true, as the bow supports coating
     */
    @Override
    public boolean canBeCoated() {
        return true;
    }

    /**
     * Applies a coating to the bow.
     *
     * @param coatingItem the coating material to apply
     */
    @Override
    public void applyCoating(String coatingItem) {
        this.coating = coatingItem;
    }

    /**
     * Removes the current coating from the bow.
     */
    @Override
    public void removeCoating() {
        this.coating = null;
    }

    /**
     * Gets the current coating on the bow.
     *
     * @return the coating material, or null if no coating is applied
     */
    @Override
    public String getCoating() {
        return coating;
    }

    /**
     * Returns allowable actions for coating the bow.
     * Checks the actor's inventory for coating materials and adds coating actions.
     *
     * @param actor the actor holding the bow
     * @param map the game map
     * @return list of available coating actions
     */
    @Override
    public ActionList allowableActions(Actor actor, GameMap map) {
        ActionList actions = new ActionList();

        for (Item item : actor.getItemInventory()) {
            if (item.hasAbility(Abilities.CAN_COAT_WEAPONS)) {
                actions.add(new CoatWeaponAction(this, item));
            }
        }

        return actions;
    }

    /**
     * Performs an attack with the bow and applies special effects from coatings.
     *
     * On a successful hit:
     * - If coated with Snow: inflicts FrostBitten status (3 turns, 1 warmth reduction per turn)
     * - If coated with Yewberry: inflicts Poisoned status (5 turns, 4 damage per turn)
     *
     * @param attacker the actor performing the attack
     * @param target the actor being attacked
     * @param map the game map
     * @return description of the attack result and applied effects
     */
    @Override
    public String attack(Actor attacker, Actor target, GameMap map) {
        String result = super.attack(attacker, target, map);

        if (result.contains("for") && result.contains("damage")) {
            System.out.println("Current coating: " + coating);

            if (coating != null && coating.equalsIgnoreCase("Snow")) {
                target.addStatus(new game.status.FrostBitten());
                int frostbiteStacks = 0;
                for (game.status.FrostBitten frostbitten : target.statusesOf(game.status.FrostBitten.class)) {
                    frostbiteStacks++;
                }
                System.out.println(target + " received FrostBitten. Current stacks: " + frostbiteStacks);
                result += " " + target + " is frostbitten!";
            }

            if (coating != null && coating.equalsIgnoreCase("Yew Berry")) {
                target.addStatus(new game.status.Poisoned());
                System.out.println("Poison applied to " + target);
                result += " " + target + " is poisoned by yewberry!";
            }
        }

        return result;
    }

    /**
     * Gets ranged attack actions for all valid targets within bow range (3 tiles).
     * <p>
     * Scanning Behavior:
     * <ul>
     *   <li>Scans a 3-tile radius around the attacker in both x and y directions</li>
     *   <li>Uses Chebyshev distance (max of absolute differences) to determine range</li>
     *   <li>Skips the attacker's own location</li>
     *   <li>Stays within map boundaries</li>
     * </ul>
     * <p>
     * Target Requirements:
     * <ul>
     *   <li>Must have CAN_ATTACK ability (hostile actor)</li>
     *   <li>Must be within 1-3 tiles range (excludes adjacent melee range)</li>
     *   <li>Must not have been already added to action list (prevents duplicates)</li>
     * </ul>
     * <p>
     * Lunar Phase Restriction:
     * <ul>
     *   <li><b>New Moon:</b> Returns empty action list (bow attacks disabled due to darkness)</li>
     *   <li><b>Other Phases:</b> Normal ranged attack functionality</li>
     * </ul>
     *
     * @param attacker the actor holding the bow
     * @param map the game map containing all actors and locations
     * @return ActionList containing RangedAttackAction for each valid target,
     *         or empty list if New Moon phase or no valid targets found
     */
    public ActionList getRangedAttackActions(Actor attacker, GameMap map) {
        ActionList actions = new ActionList();

        LunarPhaseManager lunarManager = LunarPhaseManager.getInstance();
        if (lunarManager != null && lunarManager.getCurrentPhase() == MoonPhase.NEW_MOON) {
            return actions;
        }

        Location myLocation = map.locationOf(attacker);
        Set<Actor> addedTargets = new HashSet<>();

        int minX = map.getXRange().min();
        int maxX = map.getXRange().max() - 1;
        int minY = map.getYRange().min();
        int maxY = map.getYRange().max() - 1;

        for (int x = Math.max(minX, myLocation.x() - 3);
             x <= Math.min(maxX, myLocation.x() + 3);
             x++) {
            for (int y = Math.max(minY, myLocation.y() - 3);
                 y <= Math.min(maxY, myLocation.y() + 3);
                 y++) {

                if (x == myLocation.x() && y == myLocation.y()) continue;

                Location targetLocation = map.at(x, y);
                if (targetLocation != null && targetLocation.containsAnActor()) {
                    Actor target = targetLocation.getActor();

                    if (!addedTargets.contains(target) &&
                            target.hasAbility(Abilities.CAN_ATTACK) &&
                            isWithinRange(attacker, target, map)) {

                        String direction = getDirectionTo(myLocation, targetLocation);
                        actions.add(new RangedAttackAction(target, direction, this));
                        addedTargets.add(target);
                    }
                }
            }
        }
        return actions;
    }

    /**
     * Helper method to determine the direction from one location to another.
     *
     * @param from the starting location
     * @param to the target location
     * @return direction string (e.g., "North", "SouthEast", "West")
     */
    private String getDirectionTo(Location from, Location to) {
        int dx = to.x() - from.x();
        int dy = to.y() - from.y();

        String direction = "";
        if (dy < 0) direction += "North";
        else if (dy > 0) direction += "South";

        if (dx < 0) direction += "West";
        else if (dx > 0) direction += "East";

        return direction.isEmpty() ? "Here" : direction;
    }

    /**
     * Checks if a target is within bow range (1-3 tiles).
     * Uses Chebyshev distance (maximum of absolute differences in x and y coordinates).
     *
     * @param attacker the actor holding the bow
     * @param target the potential target
     * @param map the game map
     * @return true if target is within range (1-3 tiles), false otherwise
     */
    public static boolean isWithinRange(Actor attacker, Actor target, GameMap map) {
        if (map.locationOf(attacker) == null || map.locationOf(target) == null) {
            return false;
        }
        int attackerX = map.locationOf(attacker).x();
        int attackerY = map.locationOf(attacker).y();
        int targetX = map.locationOf(target).x();
        int targetY = map.locationOf(target).y();

        int distance = Math.max(Math.abs(attackerX - targetX), Math.abs(attackerY - targetY));

        return distance <= 3 && distance >= 1;
    }

    /**
     * Gets the name of the weapon.
     *
     * @return "Bow"
     */
    @Override
    public String getName() {
        return "Bow";
    }

    /**
     * Checks if the bow currently has a coating applied.
     *
     * @return true if the bow has a coating, false otherwise
     */
    public boolean hasCoating() {
        return coating != null && !coating.isEmpty();
    }
}
