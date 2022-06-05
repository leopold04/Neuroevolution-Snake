import java.awt.event.KeyEvent;
import javax.swing.JFrame;

/**
 Author: Leopold Dorilas
 Version: 05/20/2022
 */
public class SnakeGame {
    // controls whether game is displayed in a new window or in the console. disable if you want to see evolution progress in console
    public static boolean VISUALS_ENABLED = true;

    // controls whether you can control the snake with your keyboard or allow the neural network to choose the snake's direction
    public static boolean PLAYER_CONTROL = true;

    // controls whether the first 50 apples are placed in set positions
    public static boolean SET_APPLES = true;

    // controls whether all snakes will inherit the best performing chromosome or start with 'empty' brains. other chromosomes can be found in file 'Chromosomes'
    public static boolean TESTING_MODE = true;

    public static void main(String[] args)
    {
        new GameFrame();
    }
}
