package game.creatures;

import game.app.Abilities;
import game.behaviours.*;

/**
 * A deer actor that wanders the map and can be tamed.
 * <p>
 * Deer do not initiate attacks. They can be attacked by others, and can be tamed
 * by offering an edible item. After taming, they follow their owner. Deer track
 * a warmth attribute that decays each turn; at zero, they are removed unless
 * cold-resistant.
 *
 * @author Sai Sann Lone
 * @version 9.0
 */
public class Deer extends Animal {

    /**
     * Creates a deer with default stats, abilities, and behaviours.
     * Warmth starts at 10 (displays as 9 after initial decrement).
     */
    public Deer() {
        super("Deer", 'd', 50, 10);

        // Deer-specific abilities
        this.enableAbility(Abilities.CAN_CONSUME);
        this.enableAbility(Abilities.CONSUMES_GROUND_ITEMS);

        // Consumption behaviour
        addNewBehaviour(2, new ConsumeGroundItemsBehaviour());

        // Food collection behaviour
        addNewBehaviour(3, new CollectFoodBehaviour(this));
    }
}
