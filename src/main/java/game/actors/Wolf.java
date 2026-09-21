package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.actors.Behaviour;
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
import game.weapons.Bite;

import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a Wolf actor that can attack other actors using {@link Bite}.
 * <p>
 * Wolves can wander, attack actors with {@code CAN_ATTACK}, and can be tamed by players.
 * Once tamed, they will follow and protect their owner.
 * </p>
 *
 * @author Esaam Matudin
 * @version 5.8
 */
public class Wolf extends Actor implements Tamable {

    /** The map of behaviours with their priorities. */
    private final Map<Integer, Behaviour> behaviours = new TreeMap<>();

    /** Whether the wolf is tamed. */
    private boolean tamed = false;

    /** The owner of the wolf if tamed. */
    private Actor owner;

    /**
     * Constructor for Wolf.
     *
     * @param name        the name of the wolf
     * @param displayChar the character representing the wolf
     * @param hitPoints   starting hit points of the wolf
     */
    public Wolf(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints);

        this.enableAbility(Abilities.CAN_ATTACK);
        this.enableAbility(Abilities.PREDATOR);
        this.setIntrinsicWeapon(new Bite());

        addNewBehaviour(1, new AttackBehaviour(this));

        // Follow and protect after being tamed
        addNewBehaviour(2, new ProtectOwnerBehaviour(this));
        addNewBehaviour(3, new FollowBehaviour(this));

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
     * Executes the wolf's turn by checking behaviours in order of priority.
     *
     * @param actions    the list of possible actions
     * @param lastAction the last action performed
     * @param map        the current game map
     * @param display    the display to show messages
     * @return the chosen action for the wolf
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
     * Returns the list of allowable actions another actor can perform on this wolf.
     * <p>
     * Only allows attacks if the other actor has {@code CAN_ATTACK}. Allows taming
     * if the wolf is not already tamed.
     * </p>
     *
     * @param otherActor the actor interacting with the wolf
     * @param direction  the direction of the wolf
     * @param map        the current game map
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
     * Tames the wolf and assigns the owner.
     * Disables the {@code PREDATOR} ability when tamed.
     *
     * @param owner the actor who tames the wolf
     */
    @Override
    public void tame(Actor owner) {
        this.tamed = true;
        this.owner = owner;
        this.disableAbility(Abilities.PREDATOR);
    }

    /**
     * Checks whether the wolf is tamed.
     *
     * @return true if tamed, false otherwise
     */
    @Override
    public boolean isTamed() {
        return this.tamed;
    }

    /**
     * Returns the owner of the wolf.
     *
     * @return the actor who owns the wolf if tamed, null otherwise
     */
    @Override
    public Actor getOwner() {
        return owner;
    }
}
