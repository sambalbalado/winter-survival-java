package game.flora;

import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.behaviours.TreeBehaviour;

import java.util.Map;
import java.util.TreeMap;

/**
 * Abstract base class for all trees.
 * Handles turn counting, growth stages, and periodic item drops.
 *
 * @author Taabish Farooq Bhat (A1) & Esaam Matudin (A3)
 * @version 2.0
 */
public abstract class Tree extends Ground {

    protected Stage stage;
    protected int turnsInStage = 0;

    // Map of behaviours with their priorities
    protected Map<Integer, TreeBehaviour> behaviours = new TreeMap<>();

    /**
     * Constructs a tree with specified display character, name, drop interval, and initial stage.
     *
     * @param displayChar the character displayed on the map
     * @param name the name of the tree
     * @param initialStage the starting stage of the tree
     */
    public Tree(char displayChar, String name, Stage initialStage) {
        super(displayChar, name);
        this.stage = initialStage;
    }

    /**
     * Called each turn by the engine.
     * Increments counters and handles growth and item drops.
     *
     * @param location the Location of the tree
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        turnsInStage++;

        // Execute all behaviours using priority orders
        for (TreeBehaviour behaviour : behaviours.values()) {
            behaviour.perform(this, location);
        }
    }

    /**
     * Adds a new behaviour with a specified priority.
     *
     * @param priority the priority value (lower means higher priority)
     * @param behaviour the behaviour to add
     */
    public void addBehaviour(Integer priority, TreeBehaviour behaviour) {
        this.behaviours.put(priority, behaviour);
    }


    /**
     * Gets the current stage of the tree.
     *
     * @return the current stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Sets the stage of the tree and resets the turns in stage counter.
     *
     * @param newStage the new stage
     */
    public void setStage(Stage newStage) {
        this.stage = newStage;
        this.turnsInStage = 0;
    }

    /**
     * Gets the number of turns spent in the current stage.
     *
     * @return turns in current stage
     */
    public int getTurnsInStage() {
        return turnsInStage;
    }

    /**
     * Helper method to get the map name from a location.
     *
     * @param location the location to check
     * @return the name of the map
     */
    protected String getMapName(Location location) {
        return location.map().toString();
    }
}
