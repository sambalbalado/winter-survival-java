package game.weapons;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.capabilities.WeaponWithActions;
import game.environment.Fire;
import game.status.Ignite;
import game.status.BurnSource;



import java.util.Random;

import static game.status.BurnSource.FireTile;
import static game.status.BurnSource.TORCH;

/**
 * Represents a Torch weapon with fire-spreading capabilities.
 *
 * The Torch deals 10 damage with a 50% hit rate.
 * On a successful hit, it:
 * - Inflicts Burning status on the target (7 turns, 3 damage per turn)
 * - Spawns fire in all 8 surrounding tiles around the attacker
 *
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public class Torch extends IntrinsicWeapon implements WeaponWithActions {

    /**
     * Random number generator for chance calculations
     */
    private static final Random random = new Random();

    /**
     * Constructor for Torch.
     * Creates a torch with 10 damage and 50% hit rate.
     */
    public Torch() {
        super(10, "burns", 50, "torch");
    }

    /**
     * Performs an attack with the torch and applies fire effects.
     *
     * On a successful hit:
     * - Inflicts Burning status on the target (7 turns, 3 damage per turn)
     * - Spawns fire terrain in all 8 surrounding tiles around the attacker
     *
     * @param attacker the actor performing the attack
     * @param target the actor being attacked
     * @param map the game map
     * @return description of the attack result and fire effects
     */
    @Override
    public String attack(Actor attacker, Actor target, GameMap map) {
        String result = super.attack(attacker, target, map);

        if (result.contains("for") && result.contains("damage")) {
            Ignite.applyOrRefresh(target, TORCH);
            result += " " + target + " is set ablaze!";

            spawnFire(attacker, map);
            result += " Fire spreads around the area!";
        }

        return result;
    }

    /**
     * Spawns fire terrain in all 8 surrounding locations of the attacker.
     * Fire is only spawned on valid locations that aren't already fire terrain.
     *
     * @param attacker the actor attacking with the torch
     * @param map the game map
     */
    private void spawnFire(Actor attacker, GameMap map) {
        Location attackerLocation = map.locationOf(attacker);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                Location fireLocation = attackerLocation.map().at(
                        attackerLocation.x() + dx,
                        attackerLocation.y() + dy
                );

                if (fireLocation != null &&
                        !fireLocation.getGround().getClass().equals(Fire.class)) {
                    fireLocation.setGround(new Fire(FireTile, 5));
                }
            }
        }
    }

    /**
     * Gets the name of the weapon.
     *
     * @return "Torch"
     */
    @Override
    public String getName() {
        return "Torch";
    }

    /**
     * Returns allowable actions for the torch.
     * Torch has no special actions beyond basic attacks.
     *
     * @param actor the actor holding the torch
     * @param map the game map
     * @return an empty ActionList
     */
    @Override
    public ActionList allowableActions(Actor actor, GameMap map) {
        return new ActionList();
    }

    /**
     * Determines if this weapon can attack at range.
     *
     * @return false, as the torch is a melee weapon
     */
    @Override
    public boolean isRangedWeapon() {
        return false;
    }
}
