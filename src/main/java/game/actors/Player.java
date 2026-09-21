package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseActorAttribute;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.displays.Menu;
import game.Abilities;
import game.weapons.BareFist;
import game.GameAttributes;
import game.items.Bedroll;
import game.items.Bottle;

/**
 * Class representing the Player.
 * <p>
 * The player has attributes for health, warmth, and hydration, and can perform actions
 * each turn via the menu. The player can also carry items like a Bedroll or Bottle.
 * </p>
 *
 * @author Adrian Kristanto (original supplied player scaffold)
 * @author Esaam Matudin (survival attributes, inventory, and turn handling)
 * @version 9.1
 */
public class Player extends Actor {

    /**
     * Constructor for the Player.
     *
     * @param name        the name of the player
     * @param displayChar the character used to display the player
     * @param hitPoints   the starting hit points of the player
     */
    public Player(String name, char displayChar, int hitPoints) {
        super(name, displayChar, hitPoints);

        this.enableAbility(Abilities.CAN_ATTACK);
        this.setIntrinsicWeapon(new BareFist());

        this.addNewStatistic(GameAttributes.HYDRATION, new BaseActorAttribute(20));
        this.addNewStatistic(GameAttributes.WARMTH, new BaseActorAttribute(30));

        this.addItemToInventory(new Bedroll("Bedroll", '=', true));
        this.addItemToInventory(new Bottle("Bottle", 'o', true));
    }

    /**
     * Handles the player's turn each round.
     *
     * @param actions    the list of possible actions
     * @param lastAction the last action performed
     * @param map        the current game map
     * @param display    the display to show messages
     * @return the action chosen by the player
     */
    @Override
    public Action playTurn(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (lastAction.getNextAction() != null)
            return lastAction.getNextAction();

        if (!this.isConscious()) {
            String result = this.unconscious(map);
            display.println(result);
            return new Action() {
                @Override
                public String execute(Actor actor, GameMap map) {
                    return "";
                }

                @Override
                public String menuDescription(Actor actor) {
                    return "";
                }
            };
        }

        displayStats(display);
        updateTurn();

        Menu menu = new Menu(actions);
        return menu.showMenu(this, display);
    }

    /**
     * Checks if the player is still conscious.
     *
     * @return true if the player is conscious, false otherwise
     */
    @Override
    public boolean isConscious() {
        return super.isConscious()
                && this.getAttribute(GameAttributes.HYDRATION) > 0
                && this.getAttribute(GameAttributes.WARMTH) > 0;
    }

    /**
     * Called when the player becomes unconscious.
     *
     * @param map the current game map
     * @return a string describing the unconscious event
     */
    @Override
    public String unconscious(GameMap map) {
        return super.unconscious(map);
    }

    /**
     * Displays the player's stats (HP, Warmth, Hydration).
     *
     * @param display the display to print the stats
     */
    private void displayStats(Display display) {
        display.println("Explorer Stats:");
        display.println("HP: " + getAttribute(BaseAttributes.HEALTH)
                + "/" + getMaximumAttribute(BaseAttributes.HEALTH));
        display.println("Warmth: " + getAttribute(GameAttributes.WARMTH)
                + "/" + getMaximumAttribute(GameAttributes.WARMTH));
        display.println("Hydration: " + getAttribute(GameAttributes.HYDRATION)
                + "/" + getMaximumAttribute(GameAttributes.HYDRATION));
    }

    /**
     * Updates player attributes each turn by decreasing hydration and warmth.
     */
    private void updateTurn() {
        this.modifyAttribute(GameAttributes.HYDRATION,
                ActorAttributeOperation.DECREASE, 1);
        this.modifyAttribute(GameAttributes.WARMTH,
                ActorAttributeOperation.DECREASE, 1);
    }
}
