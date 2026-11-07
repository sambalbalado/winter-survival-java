package game.weapons;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.actions.CoatWeaponAction;
import game.app.Abilities;
import game.capabilities.CoatableWeapon;
import game.capabilities.WeaponWithActions;
import game.items.Coatable;
import game.status.Bleeding;
import game.status.Poisoned;

import java.util.Random;

/**
 * Represents an Axe weapon with special effects.
 *
 * The Axe deals 15 damage with a 75% hit rate and has a 50% chance to cause bleeding on hit.
 * It can be coated with special materials:
 * - Snow coating: Inflicts FrostBitten status
 * - Yewberry coating: Inflicts Poisoned status
 *
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public class Axe extends IntrinsicWeapon implements CoatableWeapon, WeaponWithActions, Coatable {

    /**
     * Random number generator for bleeding chance calculation
     */
    private static final Random random = new Random();

    /**
     * The current coating applied to the axe
     */
    private String coating;

    /**
     * Constructor for Axe.
     * Creates an axe with 15 damage, 75% hit rate, and no initial coating.
     */
    public Axe() {
        super(15, "chops", 75, "axe");
        this.coating = null;
    }

    /**
     * Determines if this weapon can attack at range.
     *
     * @return false, as the axe is a melee weapon
     */
    public boolean isRangedWeapon() {
        return false;
    }

    /**
     * Applies a coating to the axe.
     *
     * @param coatingItem the coating material to apply
     */
    @Override
    public void applyCoating(String coatingItem) {
        this.coating = coatingItem;
    }

    /**
     * Removes the current coating from the axe.
     */
    @Override
    public void removeCoating() {
        this.coating = null;
    }

    /**
     * Gets the current coating on the axe.
     *
     * @return the coating material, or null if no coating is applied
     */
    @Override
    public String getCoating() {
        return coating;
    }

    /**
     * Determines if this axe can be coated.
     *
     * @return true, as the axe supports coating
     */
    @Override
    public boolean canBeCoated() {
        return true;
    }

    /**
     * Returns allowable actions for coating the axe.
     * Checks the actor's inventory for coating materials and adds coating actions.
     *
     * @param actor the actor holding the axe
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
     * Performs an attack with the axe and applies special effects.
     *
     * On a successful hit:
     * - 50% chance to inflict Bleeding status (2 turns, 10 damage per turn)
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
                target.addStatus(new Poisoned());
                System.out.println("Poison applied to " + target);
                result += " " + target + " is poisoned by yewberry!";
            }

            if (random.nextInt(100) < 50) {
                target.addStatus(new Bleeding());
                result += " " + target + " starts bleeding!";
            }
        }

        return result;
    }

    /**
     * Gets the name of the weapon.
     *
     * @return "Axe"
     */
    @Override
    public String getName() {
        return "Axe";
    }

    /**
     * Checks if the axe currently has a coating applied.
     *
     * @return true if the axe has a coating, false otherwise
     */
    public boolean hasCoating() {
        return coating != null && !coating.isEmpty();
    }
}
