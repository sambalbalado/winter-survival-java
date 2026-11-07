package game.status;

import edu.monash.fit2099.engine.actors.Actor;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Minimal, engine-agnostic tracker for a timed "speed boost" state.
 * Call tick(actor) once per turn (e.g., at the top of Player.playTurn).
 */
public final class SpeedBoostStatus {
    private static final Map<Actor, Integer> timeLeft = new WeakHashMap<>();

    private SpeedBoostStatus() {}

    /** Start or refresh the buff for `duration` turns. */
    public static void activate(Actor actor, int duration) {
        timeLeft.put(actor, Math.max(duration, 1));
    }

    /** Decrement by 1. No-op if not active. */
    public static void tick(Actor actor) {
        Integer left = timeLeft.get(actor);
        if (left == null) return;
        left -= 1;
        if (left <= 0) timeLeft.remove(actor);
        else timeLeft.put(actor, left);
    }

    /** True if the actor currently has any remaining duration. */
    public static boolean isActive(Actor actor) {
        Integer left = timeLeft.get(actor);
        return left != null && left > 0;
    }

    /** For UI/logs, if desired. */
    public static int remaining(Actor actor) {
        Integer left = timeLeft.get(actor);
        return left == null ? 0 : left;
    }
}
