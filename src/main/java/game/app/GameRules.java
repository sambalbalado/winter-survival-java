package game.app;

import edu.monash.fit2099.engine.actors.Actor;
import game.app.Abilities;

public final class GameRules {
    private GameRules() {}

    private static boolean creativeOnly = true;    // <- force creative as the only mode

    public static boolean isCreativeOnly() {
        return creativeOnly;
    }

    /** Call this once (e.g., on player spawn) to grant creative perks. */
    public static void enableCreativeFor(Actor actor) {
        actor.enableAbility(Abilities.CREATIVE_MODE);
        actor.enableAbility(Abilities.MORPH_PRIVILEGED);
        actor.enableAbility(Abilities.POTION_PRIVILEGED);
        // Optional: any other creative boosts (carry cap, infinite resources, etc.)
    }
}
