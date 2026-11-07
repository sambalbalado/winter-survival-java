package game.behaviours;
import edu.monash.fit2099.engine.positions.Location;
import game.flora.Tree;

/**
 * Interface for tree behaviours.
 * Allows trees to have composable, reusable behaviors that can be added or removed.
 *
 * @author Esaam Matudin
 * @version 3.0
 */
public interface TreeBehaviour {

    /**
     * Performs the behaviour's action on the tree.
     *
     * @param tree the tree performing the behaviour
     * @param location the location of the tree
     */
    void perform(Tree tree, Location location);
}
