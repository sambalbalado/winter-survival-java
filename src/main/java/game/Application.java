package game;

import edu.monash.fit2099.engine.displays.Display;

/**
 * Entry point for the Explorer game application.
 * <p>
 * This class initializes the display and the Earth world, prints the game title with a small delay,
 * constructs the game world, and starts the game loop.
 * </p>
 *
 * @author Esaam Matudin
 * @version 1.0
 */
public class Application {
    private static final long TITLE_LINE_DELAY_MILLIS = 200;

    public static void main(String[] args) {
        Display terminalDisplay = new Display();
        Earth earth = new Earth(terminalDisplay);
        try {
            for (String line : FancyMessage.GAME_TITLE.split("\n")) {
                terminalDisplay.println(line);
                try {
                    Thread.sleep(TITLE_LINE_DELAY_MILLIS);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            earth.constructWorld();
            earth.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
