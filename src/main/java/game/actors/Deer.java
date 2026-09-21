package game.actors;

import edu.monash.fit2099.engine.items.Item;
import game.actions.AttackAction;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import game.Abilities;
import game.actions.TameAction;
import game.behaviours.CollectFoodBehaviour;
import game.behaviours.FollowBehaviour;
import game.behaviours.WanderBehaviour;
import game.capabilities.Tamable;
import game.items.Status;

import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a Deer actor that wanders around the map.
 * <p>
 * Deer can be attacked by other actors but does not attack. They can be tamed
 * by actors offering a food item, after which the deer will follow its owner.
 * </p>
 *
 * @author Esaam Matudin
 * @version 8.3
 */
public class Deer extends Actor implements Tamable {

    /** The map of behaviours with their priorities. */
    private final Map<Integer, Behaviour> behaviours = new TreeMap<>();

    /** Whether the deer is tamed. */
    private boolean tamed = false;

    /** The owner of the deer if tamed. */
    private Actor owner;

    /**
     * Constructor for Deer.
     *
     * @param name        the name of the Deer
     * @param displayChar the character that will represent the Deer in the display
     * @param hitPoints   the Deer's starting hit points
     */
    public Deer(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints);

        addNewBehaviour(1, new CollectFoodBehaviour(this));
        addNewBehaviour(2, new FollowBehaviour(this));
        addNewBehaviour(999, new WanderBehaviour());
    }

    /**
     * Adds a new behaviour with a given priority.
     *
     * @param priority   priority of the behaviour (lower means higher priority)
     * @param behaviour  the behaviour to add
     */
    public void addNewBehaviour(Integer priority, Behaviour behaviour) {
        this.behaviours.put(priority, behaviour);
    }

    /**
     * Determines what the Deer does on its turn by checking behaviours in order of priority.
     *
     * @param actions    the list of possible actions
     * @param lastAction the last action performed
     * @param map        the current game map
     * @param display    the display to show messages
     * @return an action for the Deer to perform
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
     * Returns actions that other actors can perform on the Deer.
     * <p>
     * Allows attacks if the other actor has {@code CAN_ATTACK}. Allows taming
     * if the deer is not already tamed.
     * </p>
     *
     * @param otherActor the actor interacting with the Deer
     * @param direction  the direction of the Deer
     * @param map        the current map
     * @return a list of allowable actions
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
     * Tames the deer and assigns the owner.
     *
     * @param owner the actor who tames the deer
     */
    @Override
    public void tame(Actor owner) {
        this.tamed = true;
        this.owner = owner;
    }

    /**
     * Checks whether the deer is tamed.
     *
     * @return true if tamed, false otherwise
     */
    @Override
    public boolean isTamed() {
        return this.tamed;
    }

    /**
     * Returns the owner of the deer.
     *
     * @return the actor who owns the deer if tamed, null otherwise
     */
    @Override
    public Actor getOwner() {
        return owner;
    }
}
