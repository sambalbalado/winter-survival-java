package game.actions.potionactions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import game.items.SpeedBoostPotion;
import game.status.SpeedBoostStatus;   // uses the status you tick in Player.playTurn
import game.app.Abilities;

/**
 * Drinks a {@link SpeedBoostPotion} to grant the performer a temporary speed increase.
 * <p>
 * On execution, this action activates or refreshes {@link SpeedBoostStatus} for a fixed duration,
 * then consumes the specific potion item instance from the performer's inventory.
 * </p>
 *
 * <ul>
 *   <li><b>Eligibility:</b> Requires {@link Abilities#CAN_CONSUME} or {@link Abilities#CAN_DRINK}.</li>
 *   <li><b>Effect:</b> Calls {@link SpeedBoostStatus#activate(Actor, int)} with the configured duration.</li>
 *   <li><b>Consumption:</b> Removes the exact {@link SpeedBoostPotion} instance from inventory.</li>
 *   <li><b>Duration clamp:</b> Non-positive inputs are clamped to at least 1 turn.</li>
 * </ul>
 *
 * @author Taiyo Arima
 * @version 1.0
 * @see SpeedBoostPotion
 * @see SpeedBoostStatus
 * @see Abilities
 */
public class DrinkSpeedBoostAction extends Action {

    /**
     * The exact potion item being consumed.
     */
    private final SpeedBoostPotion source;

    /**
     * Number of turns to apply the speed boost (at least 1).
     */
    private final int duration;

    /**
     * Creates an action that consumes the given potion and applies a timed speed boost.
     *
     * @param source         the concrete {@link SpeedBoostPotion} to remove from inventory
     * @param durationTurns  requested duration in turns; clamped to {@code >= 1}
     */
    public DrinkSpeedBoostAction(SpeedBoostPotion source, int durationTurns) {
        this.source = source;
        this.duration = Math.max(1, durationTurns);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Presents this as a straightforward drink action with speed-boost feedback.
     * </p>
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " drinks Speed Boost Potion";
    }

    /**
     * Executes the drink effect if the actor is allowed to consume potions.
     * <p>
     * Validates abilities, activates or refreshes the {@link SpeedBoostStatus}, removes the
     * consumed item, and returns a short status message. If the actor lacks the required ability,
     * it returns an explanatory failure string.
     * </p>
     *
     * @param actor the performer consuming the potion
     * @param map   the current map (unused aside from standard signature)
     * @return a result string indicating success or the reason for failure
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        // Optional safety: enforce ability to consume
        if (!(actor.hasAbility(Abilities.CAN_CONSUME) || actor.hasAbility(Abilities.CAN_DRINK))) {
            return actor + " cannot consume potions.";
        }

        // Activate/refresh the timed speed effect you already tick in Player.playTurn
        SpeedBoostStatus.activate(actor, duration);

        // Consume this exact item instance
        actor.removeItemFromInventory(source);

        return actor + " feels faster! (Speed Boost for " + duration + " turns)";
    }
}
