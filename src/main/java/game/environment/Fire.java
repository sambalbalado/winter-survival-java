package game.environment;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.app.Abilities;
import game.status.BurnSource;
import game.status.Ignite;

/**
 * A {@link Ground} type representing burning fire terrain on the map.
 * <p>
 * The {@code Fire} ground:
 * <ul>
 *   <li>Applies a stacking {@link Ignite} to any {@link Actor} standing on it (one stack per tick)</li>
 *   <li>Persists for a limited number of turns before extinguishing</li>
 *   <li>Reverts to its {@code base} ground (defaults to {@link Dirt}) when expired</li>
 *   <li>Allows actors to walk through it</li>
 * </ul>
 * </p>
 *
 * Typical creators: dragon breath, hazards, spells.
 *
 * @author Taiyo
 * @version 2.1
 */
public final class Fire extends Ground {
    private final BurnSource src;
    private int ticksLeft;

    public Fire(BurnSource src, int duration) {
        super('^', "fire");
        this.src = src;
        this.ticksLeft = Math.max(0, duration);
    }

    @Override
    public void tick(Location location) {
        super.tick(location);

        if (location.containsAnActor()) {
            Actor a = location.getActor();
            if (a != null && !a.hasAbility(Abilities.CREATIVE_IMMUNE)) {
                Ignite existing = a.statusesOf(Ignite.class).stream()
                        .filter(ig -> ig.source() == src)
                        .findFirst().orElse(null);

                if (existing != null) {
                    existing.addStacks(1, null);           // +1, guarded in addStacks
                    // (optional once-per-turn message)
                } else {
                    Ignite.applyOrStackOn(a, src, 1, null); // creates 5×5; guarded if called twice
                }
            }
        }

        if (--ticksLeft <= 0) {
            location.setGround(new Dirt());
        }
    }
}
