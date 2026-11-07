package game.status;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import game.environment.Fire;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A centralised system that manages burning effects applied by {@link Fire}.
 * <p>
 * The {@code BurningByFlame} class handles:
 * <ul>
 *     <li>Stacking of multiple burn instances on an {@link Actor}</li>
 *     <li>Damage calculation and display output per turn</li>
 *     <li>Automatic stack expiry and removal after their duration ends</li>
 * </ul>
 * </p>
 * <p>
 * Each stack lasts for a fixed duration (default 5 turns) and deals
 * {@value #DAMAGE_PER_STACK} damage per stack per turn. Stacks are
 * tracked independently for each actor and automatically decay over time.
 * </p>
 *
 * @author Taiyo
 * @version 1.0
 */
public final class BurningByPortal {

    /**
     * The default number of turns each stack lasts before expiring.
     */
    private static final int DEFAULT_STACK_TURNS = 3;

    /**
     * The amount of damage dealt per active stack each turn.
     */
    private static final int DAMAGE_PER_STACK = 5;

    /**
     * Stores all active burning stacks for each actor.
     * <p>
     * Each {@link Actor} maps to a queue of integers, where each integer represents
     * the remaining turns for an individual burning stack.
     * </p>
     */
    private static final Map<Actor, ArrayDeque<Integer>> stacks = new WeakHashMap<>();

    /**
     * Adds a new burning stack with the default duration to the specified actor.
     * <p>
     * This should be called whenever an actor steps on a {@link Fire}
     * ground tile.
     * </p>
     *
     * @param actor the actor receiving the burning stack
     */
    public static void addStackTo(Actor actor) { addStackTo(actor, DEFAULT_STACK_TURNS); }

    /**
     * Adds a new burning stack to the specified actor with a custom duration.
     *
     * @param actor the actor receiving the burning stack
     * @param turns the duration (in turns) for the new stack
     */
    public static void addStackTo(Actor actor, int turns) {
        if (turns <= 0) return;
        stacks.computeIfAbsent(actor, a -> new ArrayDeque<>()).addLast(turns);
    }

    /**
     * Processes all active burning stacks for the specified actor at the end of their turn.
     * <p>
     * This method:
     * <ul>
     *     <li>Calculates total damage based on the number of active stacks</li>
     *     <li>Applies damage to the actor</li>
     *     <li>Prints feedback messages to the {@link Display}</li>
     *     <li>Reduces remaining durations and removes expired stacks</li>
     * </ul>
     * </p>
     *
     * @param actor the actor currently affected by burning
     * @param disp  the display used to print burning feedback messages
     */
    public static void onTurn(Actor actor, Display disp) {
        ArrayDeque<Integer> damageStack = stacks.get(actor);
        if (damageStack == null || damageStack.isEmpty()) return;

        int totalStacks = damageStack.size();
        int damage = totalStacks * DAMAGE_PER_STACK;
        actor.hurt(damage);

        int longest = 0;
        for (int t : damageStack)
            if (t > longest) longest = t;

        disp.println(actor + " is burning: " + totalStacks + " stack"
                + (totalStacks == 1 ? "" : "s")
                + ", takes " + damage + " dmg, " + longest + " turn(s) left");

        ArrayDeque<Integer> next = new ArrayDeque<>(damageStack.size());
        for (int t : damageStack) {
            if (--t > 0) next.addLast(t);
        }

        if (next.isEmpty()) {
            stacks.remove(actor);
            disp.println(actor + " is no longer burning.");
        } else {
            stacks.put(actor, next);
        }
    }
}
