package game.creatures;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseActorAttribute;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.AttackAction;
import game.actions.TameAction;
import game.api.LunarPhaseManager;
import game.api.MoonPhase;
import game.app.Abilities;
import game.behaviours.*;
import game.status.Bleeding;
import game.status.FrostBitten;
import game.status.Poisoned;
import game.status.Ignite;
import game.status.BurnSource;
import game.status.CreativeMode;


import java.util.Map;
import java.util.TreeMap;

/**
 * Abstract base class for all animals in the game.
 * <p>
 * Provides common functionality including:
 * - Behaviour management with priority-based execution
 * - Warmth attribute tracking and decay
 * - Taming mechanics
 * - Status effect processing (Bleeding, Poison, Burning, Frostbite)
 * - Standardized turn processing
 *
 * @author Sai Sann Lone & Taabish Farooq Bhat
 * @version 2.0
 */
public abstract class Animal extends Actor implements Tamable {

    /** The map of behaviours with their priorities. */
    protected Map<Integer, Behaviour> behaviours = new TreeMap<>();

    /** Whether the animal is tamed. */
    protected boolean tamed = false;

    /** The owner of the animal if tamed. */
    protected Actor owner;

    /** Flag to skip warmth decay on spawn turn. */
    protected boolean justSpawned = true;

    /**
     * Constructor for creating an animal.
     *
     * @param name the name of the animal
     * @param displayChar the character to display on the map
     * @param hitPoints the starting hit points
     * @param initialWarmth the starting warmth value
     */
    public Animal(String name, char displayChar, int hitPoints, int initialWarmth) {
        super(name, displayChar, hitPoints);

        // Initialize warmth attribute
        this.addNewStatistic(AnimalAttributes.WARMTH, new BaseActorAttribute(initialWarmth));

        // Immediately decrement warmth so first display shows (initialWarmth - 1)
        this.modifyAttribute(AnimalAttributes.WARMTH, ActorAttributeOperation.DECREASE, 1);

        // Enable common abilities
        this.enableAbility(Abilities.CAN_ATTACK);

        // Common behaviours found in all animals

        // Dragon effects behaviours (highest priority)
        addNewBehaviour(0, new ImmobilizedBehaviour());
        addNewBehaviour(1, new FleeBehaviour());


        // Wander behaviour (lowest priority)
        addNewBehaviour(999, new WanderBehaviour());
        // Follow Behaviour
        addNewBehaviour(10, new FollowBehaviour(this));

    }

    /**
     * Adds a new behaviour with a specified priority.
     *
     * @param priority the priority value (lower means higher priority)
     * @param behaviour the behaviour to add
     */
    public void addNewBehaviour(Integer priority, Behaviour behaviour) {
        this.behaviours.put(priority, behaviour);
    }

    /**
     * Chooses and performs this animal's action for the turn.
     * <p>
     * Handles the following in order:
     * <ol>
     *   <li><b>Unconsciousness Check:</b> Removes actor from map if HP reaches 0</li>
     *   <li><b>Lunar Phase Effects:</b>
     *     <ul>
     *       <li>Full Moon: Enables MOON_RAGE ability for increased aggression</li>
     *       <li>New Moon: If animal has DOCILE ability, performs no action (wolves rest)</li>
     *     </ul>
     *   </li>
     *   <li><b>Warmth Decay:</b> Decreases warmth by 1 per turn unless:
     *     <ul>
     *       <li>Part of a multi-turn action (e.g., sleeping)</li>
     *       <li>Just spawned this turn</li>
     *       <li>Has COLD_RESISTANT ability</li>
     *     </ul>
     *   </li>
     *   <li><b>Warmth Depletion Check:</b> Removes actor if warmth reaches 0 (unless cold-resistant)</li>
     *   <li><b>Fire Interaction:</b> Applies BurningByTorch status if standing on fire ground ('^')</li>
     *   <li><b>Status Effect Damage:</b> Applies damage from:
     *     <ul>
     *       <li>BurningByTorch: Cumulative damage from all burning stacks</li>
     *       <li>Bleeding: Cumulative damage from all bleeding stacks</li>
     *       <li>Poisoned: Cumulative damage from all poison stacks</li>
     *       <li>FrostBitten: Ticks status and reduces warmth per stack</li>
     *     </ul>
     *   </li>
     *   <li><b>Behaviour Evaluation:</b> Executes first non-null action from priority-ordered behaviours</li>
     * </ol>
     *
     * @param actions available actions for this turn (not used in current implementation)
     * @param lastAction the previous action performed; if it has a chained next action,
     *                   warmth decay is skipped
     * @param map current game map containing this actor
     * @param display output display for status messages
     * @return the selected action for this turn, or DoNothingAction if no behaviour triggers
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {

        for (Ignite ig : new java.util.ArrayList<>(statusesOf(Ignite.class))) {
            ig.resetTurnFlags();
            ig.onTurn(this, display);
            if (!ig.isStatusActive()) removeStatus(ig);
        }

        // Universal KO check at 0 HP

        if (!isConscious()) {
            map.removeActor(this);
            return new DoNothingAction();
        }

        MoonPhase phase = LunarPhaseManager.getInstance().getCurrentPhase();
        if (phase == MoonPhase.FULL_MOON) {
            this.enableAbility(Abilities.MOON_RAGE);
        } else {
            this.disableAbility(Abilities.MOON_RAGE);
        }


        MoonPhase currentPhase = LunarPhaseManager.getInstance().getCurrentPhase();
        if (currentPhase == MoonPhase.NEW_MOON && hasAbility(Abilities.DOCILE)) {
            return new DoNothingAction();
        }

        // Per-turn warmth decay (skip if multi-turn action or just spawned)
        if ((lastAction == null || lastAction.getNextAction() == null) && !justSpawned) {
            if (!hasAbility(Abilities.COLD_RESISTANT)) {
                modifyAttribute(AnimalAttributes.WARMTH, ActorAttributeOperation.DECREASE, 1);
            }
        }
        justSpawned = false;

        // Check for warmth depletion
        if (getAttribute(AnimalAttributes.WARMTH) <= 0 && !hasAbility(Abilities.COLD_RESISTANT)) {
            display.println("The " + this.name + " becomes unconscious and is removed from the map.");
            map.removeActor(this);
            return new DoNothingAction();
        }

        // Fire interaction
        Location currentLocation = map.locationOf(this);
        if (currentLocation.getGround().getDisplayChar() == '^' && !hasStatus(CreativeMode.class)) {
            for (Ignite ig : new java.util.ArrayList<>(statusesOf(Ignite.class))) {
                ig.resetTurnFlags();
                ig.onTurn(this, display);
            }
            display.println(this.name + " walks through fire and gets burned!");
        }

        // Process burning damage
        if (hasStatus(Ignite.class)) {
            int totalBurningDamage = statusesOf(Ignite.class).stream()
                    .mapToInt(Ignite::damagePerTurn)
                    .sum();
            if (totalBurningDamage > 0) {
                this.hurt(totalBurningDamage);
            }
        }

        // Process bleeding damage
        if (hasStatus(Bleeding.class)) {
            int totalDamage = 0;
            for (Bleeding bleeding : statusesOf(Bleeding.class)) {
                totalDamage += bleeding.getDamagePerTurn();
            }
            if (totalDamage > 0) {
                this.hurt(totalDamage);
            }
        }

        // Process poison damage
        if (hasStatus(Poisoned.class)) {
            int totalPoisonDamage = 0;
            for (Poisoned poison : statusesOf(Poisoned.class)) {
                totalPoisonDamage += poison.getDamagePerTurn();
            }
            if (totalPoisonDamage > 0) {
                this.hurt(totalPoisonDamage);
            }
        }

        // Process frostbite
        if (hasStatus(FrostBitten.class)) {
            int frostbiteStacks = 0;
            for (FrostBitten frostbitten : statusesOf(FrostBitten.class)) {
                frostbiteStacks++;
                frostbitten.tickStatus(this, map.locationOf(this));
                if (!frostbitten.isStatusActive()) {
                    removeStatus(frostbitten);
                }
            }

            int totalWarmthReduction = frostbiteStacks;
            modifyAttribute(AnimalAttributes.WARMTH, ActorAttributeOperation.DECREASE, totalWarmthReduction);

            display.println(this + " loses 2" + " warmth due to frostbite and cold.");
        }

        // Evaluate behaviours in priority order
        for (Behaviour behaviour : behaviours.values()) {
            Action action = behaviour.generateAction(this, map);
            if (action != null) {
                return action;
            }
        }

        return new DoNothingAction();
    }

    /**
     * Returns the list of allowable actions another actor can perform on this animal.
     * <p>
     * Allows attacks if the other actor has {@code CAN_ATTACK} and is not the owner.
     * Allows taming if the animal is not already tamed and the other actor has fruit.
     *
     * @param otherActor the actor interacting with the animal
     * @param direction the direction of the other actor
     * @param map the current game map
     * @return a list of actions the other actor can perform
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList actions = new ActionList();

        // Allow attack if other actor can attack and is not the owner
        if (otherActor.hasAbility(Abilities.CAN_ATTACK) && !otherActor.equals(owner)) {
            actions.add(new AttackAction(this, direction));
        }

        // Allow taming if not already tamed and other actor has fruit
        if (!tamed) {
            for (Item item : otherActor.getItemInventory()) {
                if (item.hasAbility(Abilities.EAT_FRUIT)) {
                    actions.add(new TameAction(this, item));
                    break;
                }
            }
        }

        return actions;
    }

    /**
     * Tames the animal and assigns the owner.
     * Subclasses can override to add additional behavior (e.g., disabling PREDATOR ability).
     *
     * @param owner the actor who tames the animal
     */
    @Override
    public void tame(Actor owner) {
        this.tamed = true;
        this.owner = owner;
    }

    /**
     * Checks whether the animal is tamed.
     *
     * @return true if tamed, false otherwise
     */
    @Override
    public boolean isTamed() {
        return this.tamed;
    }

    /**
     * Returns the owner of the animal.
     *
     * @return the actor who owns the animal if tamed, null otherwise
     */
    @Override
    public Actor getOwner() {
        return owner;
    }

    /**
     * Returns a string representation of the animal including warmth status.
     *
     * @return string representation with warmth information
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(super.toString());
        String warmthStatus = hasAbility(Abilities.COLD_RESISTANT)
                ? "|Immune|"
                : String.valueOf(getAttribute(AnimalAttributes.WARMTH));
        result.append(" | (Warmth: ").append(warmthStatus).append(")");

        // Add poisoned status if active
        if (hasStatus(Poisoned.class)) {
            for (Poisoned poison : statusesOf(Poisoned.class)) {
                if (poison.isStatusActive()) {
                    result.append(" | ").append(poison.toString());
                }
            }
        }

        return result.toString();
    }
}
