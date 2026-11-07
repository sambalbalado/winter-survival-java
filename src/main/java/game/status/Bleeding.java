package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

public class Bleeding implements Status {
    private int turnsRemaining;
    private int tickCount = 0;
    private final int damagePerTurn;

    public Bleeding() {
        this.turnsRemaining = 2;
        this.damagePerTurn = 10;
    }

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        tickCount++;
        // Only decrement every second tick
        if (tickCount % 2 == 0 && turnsRemaining > 0) {
            turnsRemaining--;
        }
    }

    @Override
    public boolean isStatusActive() {
        return turnsRemaining > 0;
    }

    public int getDamagePerTurn() {
        return damagePerTurn;
    }
}
