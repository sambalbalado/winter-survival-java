package game.status;

import edu.monash.fit2099.engine.GameEntity;
import edu.monash.fit2099.engine.capabilities.Status;
import edu.monash.fit2099.engine.positions.Location;

public final class CreativeMode implements Status {
    public static final CreativeMode INSTANCE = new CreativeMode();
    private CreativeMode() {}

    @Override
    public void tickStatus(GameEntity currEntity, Location location) {
        Status.super.tickStatus(currEntity, location);
    }

    @Override
    public boolean isStatusActive() {
        return false;
    }
}
