package Snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class PowerUp {
    public enum Type {
        SPEED_UP, DOUBLE_SCORE, SLOW_DOWN
    }

    public static final int DURATION = 5000; // ms
    private static final Color COLOR_SPEED = new Color(0, 255, 0); // Green
    private static final Color COLOR_DOUBLE = new Color(255, 215, 0); // Gold
    private static final Color COLOR_SLOW = new Color(138, 43, 226); // Purple

    private static Random rand = new Random();

    public int x, y;
    public boolean isActive = false;
    public Type type;
    private long activationTime;

    // Static Bungee font
    private static Font bungeeFont = null;
    static {
        try {
            // Pastikan file Bungee-Regular.ttf ada di folder resources/Snake/
            InputStream is = PowerUp.class.getResourceAsStream("/Snake/Bungee-Regular.ttf");
            if (is != null) {
                bungeeFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, 14f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(bungeeFont);
            }
        } catch (FontFormatException | IOException e) {
            bungeeFont = new Font("SansSerif", Font.BOLD, 14); // fallback
        }
    }

    public void newPowerUp() {
        x = rand.nextInt(GameMain.COLS - 4) + 2;
        y = rand.nextInt(GameMain.ROWS - 4) + 2;
        isActive = true;
        activationTime = System.currentTimeMillis();
        // Randomize type
        int t = rand.nextInt(3);
        if (t == 0) type = Type.SPEED_UP;
        else if (t == 1) type = Type.DOUBLE_SCORE;
        else type = Type.SLOW_DOWN;
    }

    public void deactivate() {
        isActive = false;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - activationTime > DURATION;
    }

    public void paint(Graphics g) {
        if (!isActive) return;
        switch (type) {
            case SPEED_UP: g.setColor(COLOR_SPEED); break;
            case DOUBLE_SCORE: g.setColor(COLOR_DOUBLE); break;
            case SLOW_DOWN: g.setColor(COLOR_SLOW); break;
        }
        int size = GameMain.CELL_SIZE + 4;
        // Draw circle (bulat)
        g.fillOval(x * GameMain.CELL_SIZE - 2, y * GameMain.CELL_SIZE - 2, size, size);
        // Draw type letter with Bungee font
        g.setColor(Color.BLACK);
        String letter = type == Type.SPEED_UP ? "F" : (type == Type.DOUBLE_SCORE ? "2x" : "S");
        if (bungeeFont != null) {
            g.setFont(bungeeFont.deriveFont(Font.BOLD, 14f));
        }
        // Center the text in the circle
        int textX = x * GameMain.CELL_SIZE + size / 4;
        int textY = y * GameMain.CELL_SIZE + (int)(size * 0.7);
        g.drawString(letter, textX, textY);
    }
}