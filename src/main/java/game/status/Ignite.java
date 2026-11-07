package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.displays.Display;

/**
 * Unified burning DoT with multiplicative stacks and per-source isolation.
 * Damage = baseDamagePerTurn * stacks. Collections are never mutated inside tick().
 */
public final class Ignite implements Status {

    private static final int DEFAULT_MAX_CHARGES = 5;

    private final BurnSource source;
    private final java.util.List<BurnCharge> charges = new java.util.ArrayList<>();
    private final int maxCharges;
    private boolean expired;
    private boolean stackedThisTurn = false;

    // Each burn application (a “stack”) is an independent charge.
    private static final class BurnCharge {
        int remaining;   // turns left
        final int dpt;   // damage per turn for this charge
        BurnCharge(int remaining, int dpt) {
            this.remaining = Math.max(0, remaining);
            this.dpt = Math.max(0, dpt);
        }
    }

    public Ignite(int duration, int baseDamagePerTurn, int stacks, int maxStacks, BurnSource source) {
        this.source = source;
        // Initialize with `stacks` identical charges of (duration, dpt)
        for (int i = 0; i < Math.max(1, stacks); i++) {
            charges.add(new BurnCharge(Math.max(0, duration), Math.max(0, baseDamagePerTurn)));
        }
        this.maxCharges = Math.max(charges.size(), maxStacks);
        this.expired = charges.isEmpty() || charges.stream().allMatch(c -> c.remaining <= 0 || c.dpt == 0);
        compact(); // remove any zero-length charges just in case
    }

    // --- Readouts (adapted) ---
    /** How many turns until the NEXT drop in total damage. */
    public int remainingTurns() {
        return charges.stream().mapToInt(c -> c.remaining).min().orElse(0); // “next expiry”
    }
    /** Total damage per turn = sum of all active charges’ dpt. */
    public int damagePerTurn() {
        return charges.stream().filter(c -> c.remaining > 0).mapToInt(c -> c.dpt).sum();
    }
    public int stacks() { return (int) charges.stream().filter(c -> c.remaining > 0).count(); }
    public BurnSource source() { return source; }
    public boolean isExpired() { return expired; }
    @Override public boolean isStatusActive() { return !expired; }

    private void compact() {
        charges.removeIf(c -> c.remaining <= 0 || c.dpt == 0);
        expired = charges.isEmpty();
    }

    // --- Turn tick (single source of DoT) ---
    public void onTurn(Actor self, Display display) {
        if (expired) return;
        int dmg = damagePerTurn();
        if (dmg > 0) self.hurt(dmg);

        if (display != null && dmg > 0) {
            display.println(self + " is burning " + Ignite.summary(this) + " [" + source + "].");
        }

        // Decrement every charge; oldest charges will hit 0 first → FIFO fade
        for (BurnCharge c : charges) {
            if (c.remaining > 0) c.remaining--;
        }
        compact();
        if (expired) self.removeStatus(this);
    }

    // --- Stacking semantics ---
    /**
     * For backward compatibility: add “stacks” means append that many charges.
     * If refreshDuration != null, use it for the new charge(s); else use source defaults.
     * FireTile calls should always pass refreshDuration == null and are per-turn guarded.
     */
    public void addStacks(int add, Integer refreshDuration) {
        if (expired) expired = false;

        // Tile semantics: never refresh and at most +1 per turn.
        if (this.source == BurnSource.FireTile) {
            refreshDuration = null;
            if (!tryMarkStackedThisTurn()) return;
        } else if (refreshDuration == null) {
            // Any non-tile “no-refresh” call also obeys one-per-turn guard (defensive)
            if (!tryMarkStackedThisTurn()) return;
        }

        int toAdd = Math.max(1, add);
        int dur = (refreshDuration != null) ? refreshDuration : source.defaultDuration();
        int dpt = source.defaultDamagePerTurn();
        while (toAdd-- > 0 && stacks() < maxCharges) {
            charges.add(new BurnCharge(dur, dpt));
        }
        compact();
    }

    // --- Helpers/summary ---
    public static String summary(Ignite ig) {
        int next = ig.remainingTurns();
        return "(stacks: " + ig.stacks()
                + ", Damage per turn: " + ig.damagePerTurn()
                + ", next expiry in: " + next + ")";
    }

    public boolean tryMarkStackedThisTurn() {
        if (stackedThisTurn) return false;
        stackedThisTurn = true;
        return true;
    }
    public void resetTurnFlags() { stackedThisTurn = false; }

    // ---------------- Ergonomic helpers (updated to append charges) ----------------

    private static Ignite find(Actor a, BurnSource src) {
        return a.statusesOf(Ignite.class).stream().filter(i -> i.source == src).findFirst().orElse(null);
    }

    /** Non-tile “apply or refresh” now means: if exists, append a NEW charge; else create with 1 charge. */
    public static Ignite applyOrRefresh(Actor a, BurnSource src) {
        Ignite ex = find(a, src);
        if (ex == null) {
            Ignite ig = new Ignite(src.defaultDuration(), src.defaultDamagePerTurn(), 1, DEFAULT_MAX_CHARGES, src);
            a.addStatus(ig);
            if (src == BurnSource.FireTile) ig.tryMarkStackedThisTurn(); // avoid same-tick double stack
            return ig;
        } else {
            ex.addStacks(1, src == BurnSource.FireTile ? null : src.defaultDuration());
            return ex;
        }
    }


    /** Tiles should use this with refreshDuration == null so they append exactly one 5×5 charge per turn. */
    public static void applyOrStackOn(Actor a, BurnSource src, int stacksToAdd, Integer refreshDuration) {
        Ignite ex = find(a, src);
        if (ex == null) {
            a.addStatus(new Ignite(
                    (refreshDuration != null ? refreshDuration : src.defaultDuration()),
                    src.defaultDamagePerTurn(),
                    Math.max(1, stacksToAdd),
                    DEFAULT_MAX_CHARGES,
                    src
            ));
        } else {
            ex.addStacks(Math.max(1, stacksToAdd), refreshDuration);
        }
    }

    /** Keep, but understand this now appends a new charge (not “merge”). */
    public static void addStackTo(Actor a, BurnSource src) {
        Ignite ex = find(a, src);
        if (ex == null) {
            a.addStatus(new Ignite(src.defaultDuration(), src.defaultDamagePerTurn(), 1, DEFAULT_MAX_CHARGES, src));
        } else {
            ex.addStacks(1, src.defaultDuration());
        }
    }

    // Make Ignite.tickStatus(...) a NO-OP if your engine auto-calls it, to avoid double ticks.
    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        // NO-OP: Ignite ticks once per actor turn via onTurn(...)
    }
}
