package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

/**
 * Represents a frostbitten status effect that reduces warmth over time.
 * Frostbitten lasts for 3 turns and reduces warmth by 1 per turn.
 * This status is typically inflicted by ice-based attacks or cold environments.
 * The status tracks remaining turns while actors apply warmth reduction to themselves.
 *
 * @author Taabish Farooq Bhat
 * @version 2.0
 */
public class FrostBitten implements Status {

    private int turnsRemaining;
    private int tickCallCounter = 0; // Add counter

    private final int warmthReductionPerTurn;

    public FrostBitten() {
        this.turnsRemaining = 4;
        this.warmthReductionPerTurn = 1;
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        tickCallCounter++;
        // Only decrement every SECOND tick
        if (tickCallCounter % 2 == 0 && turnsRemaining > 0) {
            turnsRemaining--;
        }
    }

    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public int getWarmthReductionPerTurn() {
        return warmthReductionPerTurn;
    }
}
