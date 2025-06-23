package Snake;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Bonus {
    private static final Color COLOR_BONUS = new Color(255, 0, 0); // Red color for bonus
    private static final int BONUS_DURATION = 7000; // 7 seconds in milliseconds
    private static Random rand = new Random();

    int x, y;
    boolean isActive = false;
    private int size = GameMain.CELL_SIZE;
    private int sizeDirection = 1; // 1 for growing, -1 for shrinking
    private long activationTime;

    public void newBonus() {
        x = rand.nextInt(GameMain.COLS - 4) + 2;
        y = rand.nextInt(GameMain.ROWS - 4) + 2;
        isActive = true;
        size = GameMain.CELL_SIZE; // Reset size when a new bonus is created
        activationTime = System.currentTimeMillis();
    }

    public void deactivate() {
        isActive = false;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - activationTime > BONUS_DURATION;
    }

    public void paint(Graphics g) {
        if (isActive) {
            g.setColor(COLOR_BONUS);

            // Update size for animation
            size += sizeDirection;
            if (size >= GameMain.CELL_SIZE + 10 || size <= GameMain.CELL_SIZE) {
                sizeDirection *= -1; // Reverse direction when reaching limits
            }

            int offset = size / 2;
            g.fillOval(x * GameMain.CELL_SIZE - offset,
                    y * GameMain.CELL_SIZE - offset,
                    size, size);

            // Draw the time indicator
            int elapsed = (int) (System.currentTimeMillis() - activationTime);
            int remainingTime = BONUS_DURATION - elapsed;
            int barWidth = (int) ((double) remainingTime / BONUS_DURATION * GameMain.PIT_WIDTH);
            g.fillRect(0, GameMain.PIT_HEIGHT - 10, barWidth, 5);
        }
    }
}