// SpawnModification.java
package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;

import game.app.Abilities;
import game.status.Poisoned;

/**
 * Defines attribute or ability changes applied to animals when they spawn.
 * <p>
 * Each {@link SpawnModification} can give traits such as cold resistance,
 * ground-item consumption, poisoned, or bonus health. These modifications are applied
 * immediately after the animal is created.
 *
 * @author Sai Sann Lone
 * @version 1.1
 */
public class SpawnModification {
    private final boolean coldResistant;
    private final boolean consumesGroundItems;
    private final int healthBonus;
    private final String displayName;

    private SpawnModification(boolean coldResistant, boolean consumesGroundItems, int healthBonus, String displayName) {
        this.coldResistant = coldResistant;
        this.consumesGroundItems = consumesGroundItems;
        this.healthBonus = healthBonus;
        this.displayName = displayName;
    }

    /**
     * Creates a modification with no effects.
     *
     * @return a neutral {@code SpawnModification}
     */
    public static SpawnModification none() {
        return new SpawnModification(false, false, 0, "");
    }

    /**
     * Creates a modification that grants cold resistance and a health bonus.
     *
     * @param healthBonus the amount of health to add
     * @return a cold-resistant {@code SpawnModification}
     */
    public static SpawnModification withColdResistance(int healthBonus) {
        return new SpawnModification(true, false, healthBonus,
                "[+" + healthBonus + "HP, COLD_RESISTANT]");
    }

    /**
     * Creates a modification that allows animals to consume items from the ground.
     *
     * @return a {@code SpawnModification} that enables ground-item consumption
     */
    public static SpawnModification withGroundConsumption() {
        return new SpawnModification(false, true, 0, "[CONSUMES_GROUND_ITEMS]");
    }

    /**
     * Creates a modification that applies {@link Poisoned} on spawn.
     *
     * @param turns          number of turns the poison lasts
     * @param damagePerTurn  damage dealt each poisoned turn
     * @return a modification that poisons the spawned actor
     */
    public static SpawnModification poisoned(int turns, int damagePerTurn) {
        return new SpawnModification(false, false, 0,
                "[POISONED " + turns + "@" + damagePerTurn + "]"   // CHANGED
        ) {
            @Override
            public void applyTo(Actor actor) {
                super.applyTo(actor);
                actor.addStatus(new Poisoned(turns, damagePerTurn)); // same behaviour
            }
        };
    }

    /**
     * Convenience helper to apply poison to any actor (not just the spawnee).
     *
     * @param actor          target actor
     * @param turns          number of turns the poison lasts
     * @param damagePerTurn  damage per poisoned turn
     */
    public static void applyPoisonToActor(Actor actor, int turns, int damagePerTurn) {  // NEW
        actor.addStatus(new Poisoned(turns, damagePerTurn));                            // NEW
    }


    /**
     * Applies this modification to the given actor.
     * <p>
     * - Cold resistance adds max and current health and grants {@code COLD_RESISTANT}.
     * - Ground consumption grants {@code CONSUMES_GROUND_ITEMS}.
     *
     * @param actor the actor to modify
     */
    public void applyTo(Actor actor) {
        if (coldResistant) {
            actor.modifyStatsMaximum(BaseAttributes.HEALTH, ActorAttributeOperation.INCREASE, healthBonus);
            actor.modifyAttribute(BaseAttributes.HEALTH, ActorAttributeOperation.INCREASE, healthBonus);
            actor.enableAbility(Abilities.COLD_RESISTANT);
        }

        if (consumesGroundItems) {
            actor.enableAbility(Abilities.CONSUMES_GROUND_ITEMS);
            // The ConsumeGroundItemsBehaviour will automatically work based on the ability check
        }
    }
    /**
     * Returns a short label describing this modification.
     *
     * @return a display name for logging or debugging
     */
    public String getDisplayName() {
        return displayName;
    }
}
