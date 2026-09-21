package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.Abilities;
import game.actions.AttackAction;
import game.actions.TameAction;
import game.behaviours.AttackBehaviour;
import game.behaviours.FollowBehaviour;
import game.behaviours.ProtectOwnerBehaviour;
import game.behaviours.WanderBehaviour;
import game.capabilities.Tamable;
import game.items.Status;
import game.weapons.Claw;

import java.util.Map;
import java.util.TreeMap;

/**
 * A Bear actor that can wander, attack other actors, and be tamed by a player.
 * <p>
 * Bears use claws as their intrinsic weapon and attack actors that have the
 * {@code CAN_ATTACK} ability. Once tamed, they can follow and protect their owner.
 * </p>
 *
 * @author Esaam Matudin
 * @version 7.5
 */
public class Bear extends Actor implements Tamable {

    /** The map of behaviours with their priorities. */
    private final Map<Integer, Behaviour> behaviours = new TreeMap<>();

    /** Whether the bear is tamed. */
    private boolean tamed = false;

    /** The owner of the bear if tamed. */
    private Actor owner;

    /**
     * Constructor for Bear.
     *
     * @param name        the name of the bear
     * @param displayChar the character used to display the bear
     * @param hitPoints   starting hit points of the bear
     */
    public Bear(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints);

        this.enableAbility(Abilities.CAN_ATTACK);
        this.enableAbility(Abilities.PREDATOR);
        this.setIntrinsicWeapon(new Claw());

        addNewBehaviour(1, new AttackBehaviour(this));

        // Follow and protect after being tamed
        addNewBehaviour(2, new ProtectOwnerBehaviour(this));
        addNewBehaviour(3, new FollowBehaviour(this));

        addNewBehaviour(999, new WanderBehaviour());
    }

    /**
     * Adds a new behaviour with a specified priority.
     *
     * @param priority  the priority value (lower means higher priority)
     * @param behaviour the behaviour to add
     */
    public void addNewBehaviour(Integer priority, Behaviour behaviour) {
        this.behaviours.put(priority, behaviour);
    }

    /**
     * Executes the bear's turn by checking behaviours in order of priority.
     *
     * @param actions    the list of possible actions
     * @param lastAction the last action performed
     * @param map        the current game map
     * @param display    the display to show messages
     * @return the action chosen by the bear
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        for (Behaviour behaviour : behaviours.values()) {
            Action action = behaviour.generateAction(this, map);
            if (action != null)
                return action;
        }
        return new DoNothingAction();
    }

    /**
     * Returns the list of allowable actions another actor can perform on this bear.
     * <p>
     * Only allows attacks if the other actor has {@code CAN_ATTACK}. Allows taming
     * if the bear is not already tamed.
     * </p>
     *
     * @param otherActor the actor interacting with the bear
     * @param direction  the direction of the other actor
     * @param map        the current game map
     * @return a list of actions the other actor can perform
     */
    @Override
    public ActionList allowableActions(Actor otherActor, String direction, GameMap map) {
        ActionList actions = new ActionList();
        if (otherActor.hasAbility(Abilities.CAN_ATTACK) && !otherActor.equals(owner)) {
            actions.add(new AttackAction(this, direction));
        }
        // Allow taming if not already tamed
        if (!tamed) {
            for (Item item : otherActor.getItemInventory()) {
                if (item.hasAbility(Status.FOOD)) {
                    actions.add(new TameAction(this, item));
                    break;
                }
            }
        }
        return actions;
    }

    /**
     * Tames the bear and assigns the owner.
     * Disables the {@code PREDATOR} ability when tamed.
     *
     * @param owner the actor who tames the bear
     */
    @Override
    public void tame(Actor owner) {
        this.tamed = true;
        this.owner = owner;
        this.disableAbility(Abilities.PREDATOR);
    }

    /**
     * Checks whether the bear is tamed.
     *
     * @return true if tamed, false otherwise
     */
    @Override
    public boolean isTamed() {
        return this.tamed;
    }

    /**
     * Returns the owner of the bear.
     *
     * @return the actor who owns the bear if tamed, null otherwise
     */
    @Override
    public Actor getOwner() {
        return owner;
    }
}
