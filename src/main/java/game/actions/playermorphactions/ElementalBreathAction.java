package game.actions.playermorphactions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.app.Abilities;
import game.status.Ignite;
import game.status.Frozen;
import game.status.Stunned;
import game.status.BurnSource;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Performs a rotating elemental breath while the performer is in {@link Abilities#FORM_DRAGON} form.
 * <p>
 * Each execution cycles the element in the sequence <b>FIRE → ICE → LIGHTNING</b> and applies
 * an area-of-effect (AoE) status to adjacent tiles (Chebyshev radius {@value #RADIUS}):
 * </p>
 * <ul>
 *   <li><b>FIRE:</b> Apply/stack one Ignite charge via
 *   {@link Ignite#applyOrStackOn(Actor, BurnSource, int, Integer)} with {@link BurnSource#DRAGON}.</li>
 *   <li><b>ICE:</b> Apply {@link Frozen} for 2 turns.</li>
 *   <li><b>LIGHTNING:</b> Apply {@link Stunned} for 2 turns.</li>
 * </ul>
 * <p>
 * The breath only affects tiles that currently contain an actor and never targets the caster.
 * If the performer lacks {@link Abilities#FORM_DRAGON}, the action reports that it cannot be used.
 * </p>
 *
 * @author Taiyo Arima
 * @version 1.0
 * @see Abilities#FORM_DRAGON
 * @see Ignite
 * @see Frozen
 * @see Stunned
 * @see BurnSource
 */
public class ElementalBreathAction extends Action {

    /**
     * Chebyshev radius around the caster to scan for targets (i.e., 8 neighbors when {@code RADIUS == 1}).
     */
    private static final int RADIUS = 1;

    /**
     * Tracks the next elemental mode per-actor to support per-caster rotation.
     * <p>
     * Uses a {@link WeakHashMap} so entries are eligible for GC when a key {@link Actor} becomes unreachable,
     * preventing memory retention across map/actor lifecycles.
     * </p>
     */
    private static final Map<Actor, String> modeMap = new WeakHashMap<>();

    /**
     * Human-readable description shown in menus.
     *
     * @param actor the actor who would perform this action
     * @return a succinct menu label describing the rotating elemental breath
     */
    @Override
    public String menuDescription(Actor actor) {
        return actor + " → Breathe elements (Fire → Ice → Lightning)";
    }

    /**
     * Executes the elemental breath if the performer has {@link Abilities#FORM_DRAGON}.
     * <p>
     * The element used is determined by a per-actor rotation. All adjacent tiles are scanned; if a tile
     * contains an actor, the appropriate status effect is applied. The caster is never affected.
     * </p>
     *
     * @param actor the performer; must have {@link Abilities#FORM_DRAGON} to succeed
     * @param map   the current {@link GameMap} used to locate neighbors
     * @return a descriptive summary of the element used and the number of actors affected, or a failure message
     */
    @Override
    public String execute(Actor actor, GameMap map) {
        if (!actor.hasAbility(Abilities.FORM_DRAGON)) {
            return actor + " cannot breathe elements right now.";
        }

        String mode = nextModeFor(actor);
        Location origin = map.locationOf(actor);
        int affected = 0;

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                if (dx == 0 && dy == 0) continue;
                int x = origin.x() + dx, y = origin.y() + dy;
                if (!map.getXRange().contains(x) || !map.getYRange().contains(y)) continue;

                Location tile = map.at(x, y);
                if (!map.isAnActorAt(tile)) continue;

                Actor target = tile.getActor();
                switch (mode) {
                    case "FIRE" -> {
                        // unified burn system: stack 1 charge, default duration
                        Ignite.applyOrStackOn(target, BurnSource.DRAGON, 1, null);
                        affected++;
                    }
                    case "ICE" -> {
                        target.addStatus(new Frozen(2));
                        affected++;
                    }
                    case "LIGHTNING" -> {
                        target.addStatus(new Stunned(2));
                        affected++;
                    }
                }
            }
        }
        return "Dragon breath (" + mode + ") bursts out. Affected actors: " + affected + ".";
    }

    /**
     * Advances and returns the next elemental mode for a given caster. Rotation is per-actor:
     * <pre>FIRE → ICE → LIGHTNING → FIRE …</pre>
     * <p>
     * Newly-seen actors start at {@code LIGHTNING} such that the first call returns {@code FIRE}.
     * </p>
     *
     * @param a the actor whose rotation state is queried/advanced
     * @return the element to apply for this execution (after advancing the internal rotation)
     */
    private String nextModeFor(Actor a) {
        String current = modeMap.get(a);
        if (current == null) current = "LIGHTNING";
        String next = "FIRE";
        if ("FIRE".equals(current)) next = "ICE";
        else if ("ICE".equals(current)) next = "LIGHTNING";
        else if ("LIGHTNING".equals(current)) next = "FIRE";
        modeMap.put(a, next);
        return next;
    }
}
