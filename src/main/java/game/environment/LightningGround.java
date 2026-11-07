package game.environment;


import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.app.Abilities;
import game.status.Stunned;


/** Temporary lightning-charged ground that briefly stuns actors, then dissipates. */
public class LightningGround extends Ground {
    private int ticksLeft;


    public LightningGround(int duration) {
        super('!', "lightning"); // same caret as Fire visually? adjust if needed
        this.ticksLeft = Math.max(0, duration);
    }


    @Override
    public void tick(Location location) {
        super.tick(location);
        if (location.containsAnActor()) {
            Actor a = location.getActor();
            if (a != null && !a.hasAbility(Abilities.CREATIVE_IMMUNE)) {
                a.addStatus(new Stunned(1));
            }
        }
        if (--ticksLeft <= 0) {
            location.setGround(new Dirt());
        }
    }
}
