package game.environment;


import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.app.Abilities;
import game.status.Frozen;


/** Temporary icy ground that chills actors, then melts to Dirt. */
public class Ice extends Ground {
    private int ticksLeft;


    public Ice(int duration) {
        super('~', "ice");
        this.ticksLeft = Math.max(0, duration);
    }


    @Override
    public void tick(Location location) {
        super.tick(location);
        if (location.containsAnActor()) {
            Actor a = location.getActor();
            if (a != null && !a.hasAbility(Abilities.CREATIVE_IMMUNE)) {
// Light touch: ensure not spamming more than once per two ticks (Frozen already decrements every 2 ticks)
                a.addStatus(new Frozen(1));
            }
        }
        if (--ticksLeft <= 0) {
            location.setGround(new Dirt());
        }
    }
}
