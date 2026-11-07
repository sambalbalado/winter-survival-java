package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.status.SpeedBoostStatus;

/**
 * While SpeedBoost is active, move up to 2 tiles in one straight direction
 * if the path is clear and tiles are enterable.
 */
public class SprintMoveAction extends Action {
    private final int stepCount;

    public SprintMoveAction(int stepCount) {
        this.stepCount = Math.max(1, stepCount);
    }

    @Override
    public String menuDescription(Actor actor) {
        return "Sprint (" + stepCount + " tiles)";
    }

    @Override
    public String execute(Actor actor, GameMap map) {
        if (!SpeedBoostStatus.isActive(actor)) {
            return actor + " tries to sprint, but the effect has worn off.";
        }

        Location here = map.locationOf(actor);

        // Simple UX: pick the first direction that permits a full sprint.
        // (You can replace this with a submenu of directions if your UI allows.)
        for (Exit e : here.getExits()) {
            int dx = e.getDestination().x() - here.x();
            int dy = e.getDestination().y() - here.y();

            int nx = here.x(), ny = here.y();
            Location cur = here;
            boolean ok = true;

            for (int i = 0; i < stepCount; i++) {
                nx += dx; ny += dy;
                if (!map.getXRange().contains(nx) || !map.getYRange().contains(ny)) { ok = false; break; }
                Location next = map.at(nx, ny);
                if (!next.canActorEnter(actor) || map.isAnActorAt(next)) { ok = false; break; }
                cur = next;
            }

            if (ok && cur != here) {
                map.moveActor(actor, cur);
                return actor + " sprints " + stepCount + " tiles " + e.getName() + ".";
            }
        }
        return "No clear path to sprint.";
    }
}
