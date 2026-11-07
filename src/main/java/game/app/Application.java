package game.app;

import edu.monash.fit2099.engine.displays.Display;
import game.environment.Earth;
import game.ui.FancyMessage;

/**
 * The main application class that serves as the entry point for the game.
 *
 * This class handles the game initialization process, including displaying
 * the game title with animated text effects, constructing the game world,
 * and starting the main game loop. It provides error handling to assist
 * with debugging during development and runtime.
 */
public class Application {

    /**
     * The main method that starts the game application.
     * Initializes the display system, creates the game world (Earth), displays
     * an animated title screen, constructs the world with all its components,
     * and starts the main game loop. Includes comprehensive error handling
     * to catch and display any exceptions that occur during execution.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        Display terminalDisplay = new Display();
        Earth earth = new Earth(terminalDisplay);
        try{
            for (String line : FancyMessage.GAME_TITLE.split("\n")) {
                new Display().println(line);
                try {
                    Thread.sleep(200);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            earth.constructWorld();
            earth.run();

        }
        catch (Exception e) {
            e.printStackTrace();

        }
    }
}
