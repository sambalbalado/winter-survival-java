package game.behaviours;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.Behaviour;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import game.capabilities.Tamable;
import game.items.Status;

/**
 * A behaviour that allows a tamable actor (like a Deer) to collect food.
 * <p>
 * The actor will pick up food items from the current location if inventory is not full,
 * move toward nearby food if available, or give collected food to its owner if adjacent.
 * </p>
 *
 * @author Esaam Matudin
 * @version 4.8
 */
public class CollectFoodBehaviour implements Behaviour {

    /** The tamable actor this behaviour belongs to. */
    private final Tamable animal;

    /** Maximum number of items the actor can carry. */
    private final int carryCapacity;

    /**
     * Constructor for CollectFoodBehaviour.
     *
     * @param animal the tamable actor that will perform this behaviour
     */
    public CollectFoodBehaviour(Tamable animal) {
        this.animal = animal;
        this.carryCapacity = 1; // deer can hold up to 1 item
    }

    /**
     * Generates an action for the actor to collect or deliver food.
     *
     * @param actor the actor performing the behaviour
     * @param map   the game map containing the actor
     * @return an action to pick up, move, or give food, or null if no action possible
     */
    @Override
    public Action generateAction(Actor actor, GameMap map) {
        Actor owner = animal.getOwner();
        if (owner == null || !map.contains(owner) || !map.contains(actor)) {
            return null;
        }

        Location here = map.locationOf(actor);
        Location ownerLocation = map.locationOf(owner);

        boolean inventoryFull = actor.getItemInventory().size() >= carryCapacity;

        // Pick up food on the same tile
        if (!inventoryFull) {
            for (Item item : here.getItems()) {
                if (item.hasAbility(Status.FOOD)) {
                    return new Action() {
                        @Override
                        public String execute(Actor actor, GameMap map) {
                            here.removeItem(item);
                            actor.addItemToInventory(item);
                            return actor + " picked up " + item;
                        }

                        @Override
                        public String menuDescription(Actor actor) {
                            return "Pick up " + item;
                        }
                    };
                }
            }

            // Move toward any food in nearby locations (radius 10)
            for (Location loc : here.getNearbyLocations(10)) {
                for (Item item : loc.getItems()) {
                    if (item.hasAbility(Status.FOOD)) {
                        // Move one step toward this food if possible
                        int currentDistance = distance(here, loc);
                        for (Exit exit : here.getExits()) {
                            Location nextStep = exit.getDestination();
                            if (nextStep.canActorEnter(actor) && distance(nextStep, loc) < currentDistance) {
                                return new MoveActorAction(nextStep, exit.getName());
                            }
                        }
                        return null; // can't move closer
                    }
                }
            }
        }

        // Deliver food to owner if adjacent
        if (!actor.getItemInventory().isEmpty()) {
            for (Exit exit : here.getExits()) {
                Location adjacent = exit.getDestination();
                if (adjacent.equals(ownerLocation)) {
                    return new Action() {
                        @Override
                        public String execute(Actor actor, GameMap map) {
                            StringBuilder result = new StringBuilder();
                            for (Item i : actor.getItemInventory()) {
                                owner.addItemToInventory(i);
                                result.append(actor).append(" gave ").append(i).append(" to ").append(owner).append("\n");
                            }
                            for (Item i : actor.getItemInventory().toArray(new Item[0])) {
                                actor.removeItemFromInventory(i);
                            }
                            return result.toString();
                        }

                        @Override
                        public String menuDescription(Actor actor) {
                            return "Give items to " + owner;
                        }
                    };
                }
            }

            // Move toward owner if carrying items
            int currentDistance = distance(here, ownerLocation);
            for (Exit exit : here.getExits()) {
                Location dest = exit.getDestination();
                if (dest.canActorEnter(actor)) {
                    int newDistance = distance(dest, ownerLocation);
                    if (newDistance < currentDistance) {
                        return new MoveActorAction(dest, exit.getName());
                    }
                }
            }
        }

        return null;
    }

    /**
     * Calculates Manhattan distance between two locations.
     *
     * @param a first location
     * @param b second location
     * @return distance between the two locations
     */
    private int distance(Location a, Location b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }
}
