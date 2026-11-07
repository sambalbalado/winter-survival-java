package game.items;

import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Location;

/** Non-portable map marker that fades after a few turns. */
public class ScentMarker extends Item {
    private int ttl;

    public ScentMarker(int ttl) {
        super("Scent Marker", '•', false);
        this.ttl = Math.max(1, ttl);
    }

    @Override
    public void tick(Location location) {
        ttl -= 1;
        if (ttl <= 0) {
            location.removeItem(this);
        }
    }
}
