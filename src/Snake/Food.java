package Snake;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
/**
 * The Food class models a food item that the snake can eat.
 * It is placed randomly in the pit.
 */
public class Food {
    // == Define named constants ==
    private static final Color COLOR_FOOD = new Color(76, 181, 245); // Blue #4CB5F5

    // == Define properties (package access) ==
    /** current food location (x, y) in the pit (in cells) */
    int x, y;
    /** Food items eaten count */
    int foodEaten = -1;

    private static Random rand = new Random();

    /**
     * Regenerate a food item. Randomly place inside the pit (slightly off the edge).
     */
    public void newFood() {
        x = rand.nextInt(GameMain.COLS - 4) + 2;
        y = rand.nextInt(GameMain.ROWS - 4) + 2;
        ++foodEaten;  // one more food eaten
    }

    /**
     * Paint itself via the Graphics context.
     * The repaint() in GameMain class callbacks paintComponent(Graphics)
     * This snake.paint(Graphics) shall be placed in paintComponent(Graphics).
     *
     * @param g - the drawing Graphics object
     */
    public void paint(Graphics g) {
        g.setColor(COLOR_FOOD);
        // Slightly bigger than the cell
        int offset = 4;
        g.fill3DRect(x * GameMain.CELL_SIZE - offset/2,
                y * GameMain.CELL_SIZE - offset/2,
                GameMain.CELL_SIZE + offset, GameMain.CELL_SIZE + offset, true);
    }
}