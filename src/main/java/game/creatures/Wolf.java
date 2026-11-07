package game.creatures;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.app.Abilities;
import game.behaviours.*;
import game.weapons.Bite;

/**
 * A wolf actor that can wander, attack with a {@link Bite}, and be tamed.
 * <p>
 * Wolves are predatory creatures affected by lunar phases:
 * <ul>
 *   <li><b>New Moon:</b> Wolves become docile and won't attack unless provoked</li>
 *   <li><b>Normal Phase:</b> Standard aggressive behavior with base 15 damage</li>
 *   <li><b>Full Moon:</b> Wolves gain MOON_RAGE ability, dealing +5 bonus damage (20 total)</li>
 * </ul>
 * <p>
 * Wolves attack actors that can fight back ({@code CAN_ATTACK}). When tamed,
 * they follow and protect their owner and lose the {@code PREDATOR} ability.
 * Wolves track a warmth attribute that decays each turn; at zero warmth,
 * they are removed from the map unless cold-resistant.
 * <p>
 * Default Stats:
 * <ul>
 *   <li>Hit Points: 100</li>
 *   <li>Initial Warmth: 25 (displays as 24 after first turn decrement)</li>
 *   <li>Base Bite Damage: 15</li>
 *   <li>Bite Hit Rate: 50%</li>
 *   <li>Display Character: 'e'</li>
 * </ul>
 * <p>
 * Behaviors (by priority):
 * <ol start="0">
 *   <li>ImmobilizedBehaviour - Handles dragon ice breath stun</li>
 *   <li>FleeBehaviour - Handles dragon fear effects</li>
 *   <li>AttackBehaviour - Engages hostile actors</li>
 *   <li>ProtectOwnerBehaviour - Defends owner when tamed</li>
 *   <li>FollowBehaviour - Follows owner when tamed</li>
 *   <li>WanderBehaviour - Random movement when idle</li>
 * </ol>
 *
 * @author Sai Sann Lone & Taabish Farooq Bhat
 * @version 7.0
 * @see Animal
 * @see Bite
 * @see game.api.MoonPhase
 */
public class Wolf extends Animal {

    private int lunarDamageBonus = 0;

    /**
     * Creates a wolf with default stats, abilities, and behaviors.
     * <p>
     * Initial Configuration:
     * <ul>
     *   <li>Name: "Wolf"</li>
     *   <li>Display Character: 'e'</li>
     *   <li>Hit Points: 100</li>
     *   <li>Warmth: 25 (shows as 24 after first turn due to Animal class decrement)</li>
     * </ul>
     * <p>
     * Enabled Abilities:
     * <ul>
     *   <li>{@code PREDATOR} - Allows hunting prey (disabled when tamed)</li>
     *   <li>{@code DOCILE} - Marker for New Moon phase behavior</li>
     *   <li>{@code CAN_ATTACK} - Inherited from Animal class</li>
     * </ul>
     * <p>
     * Behaviors Added:
     * <ul>
     *   <li>Priority 2: AttackBehaviour - Engages hostile actors</li>
     *   <li>Priority 3: ProtectOwnerBehaviour - Active only when tamed</li>
     *   <li>Priority 10: FollowBehaviour - Inherited from Animal</li>
     *   <li>Priority 999: WanderBehaviour - Inherited from Animal</li>
     * </ul>
     */
    public Wolf() {
        super("Wolf", 'e', 100, 25);

        // Wolf-specific abilities and weapon
        this.enableAbility(Abilities.PREDATOR);
        this.enableAbility(Abilities.DOCILE);

        // Attack behaviour
        addNewBehaviour(2, new AttackBehaviour(this));

        // Tamed behaviours (only active when tamed)
        addNewBehaviour(3, new ProtectOwnerBehaviour(this));
    }

    /**
     * Returns the wolf's intrinsic bite weapon with lunar damage bonus applied.
     * <p>
     * Damage Calculation:
     * <ul>
     *   <li><b>Full Moon (MOON_RAGE active):</b> 15 base + 5 lunar bonus = 20 damage</li>
     *   <li><b>Other Phases:</b> 15 base + 0 lunar bonus = 15 damage</li>
     * </ul>
     * <p>
     * The lunar bonus is dynamically calculated each time this method is called,
     * checking for the MOON_RAGE ability that is enabled during Full Moon phases
     * by the lunar phase system.
     *
     * @return IntrinsicWeapon representing the wolf's bite attack with current
     *         lunar phase damage modifiers applied
     * @see Bite
     * @see game.api.LunarPhaseManager
     */
    @Override
    public IntrinsicWeapon getIntrinsicWeapon() {
        int lunarBonus = this.hasAbility(Abilities.MOON_RAGE) ? 5 : 0;
        return new Bite(lunarBonus, 15);
    }

    /**
     * Tames the wolf and assigns an owner.
     * <p>
     * When tamed:
     * <ul>
     *   <li>Wolf stops hunting prey (PREDATOR ability disabled)</li>
     *   <li>Wolf follows owner via FollowBehaviour</li>
     *   <li>Wolf protects owner via ProtectOwnerBehaviour</li>
     *   <li>Wolf won't attack owner even if owner has CAN_ATTACK</li>
     * </ul>
     * <p>
     * Taming is permanent and cannot be undone. The wolf will remain loyal
     * to the owner for the rest of the game session.
     *
     * @param owner the actor who tames the wolf (typically the player)
     */
    @Override
    public void tame(edu.monash.fit2099.engine.actors.Actor owner) {
        super.tame(owner);
        this.disableAbility(Abilities.PREDATOR);
    }
}
