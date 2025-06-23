package Snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GameMain extends JPanel implements StateTransition {
    private static final long serialVersionUID = 1L;

    public static final int ROWS = 40;
    public static final int COLS = 40;
    public static final int CELL_SIZE = 15;

    public static final String TITLE = "Snake";
    public static final int PIT_WIDTH = COLS * CELL_SIZE;
    public static final int PIT_HEIGHT = ROWS * CELL_SIZE;

    public static final Color COLOR_PIT = Color.DARK_GRAY;
    public static final Color COLOR_GAMEOVER = Color.RED;
    public static final Font FONT_GAMEOVER = new Font("Verdana", Font.BOLD, 30);
    public static final Color COLOR_INSTRUCTION = Color.RED;
    public static final Font FONT_INSTRUCTION = new Font("Dialog", Font.PLAIN, 26);
    public static final Font FONT_DATA = new Font("Arial", Font.BOLD, 16);

    private Color scoreTextColor = new Color(0, 0, 0); // Default to black

    private Snake snake;
    private Food food;
    private Bonus bonus;
    private PowerUp powerUp;
    private GamePanel pit;

    private State currentState;
    private Timer stepTimer;
    public static final int STEPS_PER_SEC = 6;
    public static final int STEP_IN_MSEC = 1000 / STEPS_PER_SEC;

    private int score;
    private int highscore = 0;

    // PowerUp state
    private PowerUp.Type activePowerUpType = null;
    private long powerUpEffectEndTime = 0;
    private int normalStepInMsec = STEP_IN_MSEC;
    private int currentStepInMsec = STEP_IN_MSEC;
    private boolean doubleScoreActive = false;

    public GameMain() {
        initGUI();
        initGame();
        newGame();
    }

    public void initGUI() {
        pit = new GamePanel();
        pit.setPreferredSize(new Dimension(PIT_WIDTH, PIT_HEIGHT));
        pit.setFocusable(true);
        pit.requestFocus();
        super.add(pit);
    }

    @Override
    public void initGame() {
        snake = new Snake();
        food = new Food();
        bonus = new Bonus();
        powerUp = new PowerUp();
        stepTimer = new Timer(STEP_IN_MSEC, e -> stepGame());

        pit.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                int key = evt.getKeyCode();
                if (currentState == State.READY) {
                    startGame();
                } else if (currentState == State.PLAYING) {
                    switch (key) {
                        case KeyEvent.VK_UP:
                            snake.changeDirection(Move.UP); break;
                        case KeyEvent.VK_DOWN:
                            snake.changeDirection(Move.DOWN); break;
                        case KeyEvent.VK_LEFT:
                            snake.changeDirection(Move.LEFT); break;
                        case KeyEvent.VK_RIGHT:
                            snake.changeDirection(Move.RIGHT); break;
                    }
                } else if (currentState == State.GAMEOVER) {
                    newGame();
                    startGame();
                }
            }
        });

        currentState = State.INITIALIZED;
    }

    @Override
    public void newGame() {
        if (currentState != State.GAMEOVER && currentState != State.INITIALIZED)
            throw new IllegalStateException("Cannot run newGame() in state " + currentState);

        snake.newGame();
        do {
            food.newFood();
        } while (snake.contains(food.x, food.y));
        food.foodEaten = 0;
        score = 0;
        bonus.deactivate();
        powerUp.deactivate();
        activePowerUpType = null;
        powerUpEffectEndTime = 0;
        currentStepInMsec = normalStepInMsec;
        doubleScoreActive = false;
        stepTimer.setDelay(normalStepInMsec);

        currentState = State.READY;
        repaint();
    }

    @Override
    public void startGame() {
        if (currentState != State.READY)
            throw new IllegalStateException("Cannot run startGame() in state " + currentState);
        stepTimer.start();
        currentState = State.PLAYING;
        repaint();
    }

    @Override
    public void stopGame() {
        if (currentState != State.PLAYING)
            throw new IllegalStateException("Cannot run stopGame() in state " + currentState);
        stepTimer.stop();
        currentState = State.GAMEOVER;

        // Update highscore jika skor sekarang lebih tinggi
        if (score > highscore) {
            highscore = score;
        }

        repaint();
    }

    public void stepGame() {
        if (currentState != State.PLAYING)
            throw new IllegalStateException("Cannot run stepGame() in state " + currentState);

        int headX = snake.getHeadX();
        int headY = snake.getHeadY();

        boolean ateSomething = false;

        // PowerUp logic: ambil powerup jika diambil ular
        if (powerUp.isActive && headX == powerUp.x && headY == powerUp.y) {
            activePowerUpType = powerUp.type;
            powerUpEffectEndTime = System.currentTimeMillis() + PowerUp.DURATION;
            powerUp.deactivate();

            if (activePowerUpType == PowerUp.Type.SPEED_UP) {
                currentStepInMsec = Math.max(40, normalStepInMsec / 2);
                stepTimer.setDelay(currentStepInMsec);
                doubleScoreActive = false;
            } else if (activePowerUpType == PowerUp.Type.DOUBLE_SCORE) {
                doubleScoreActive = true;
                currentStepInMsec = normalStepInMsec;
                stepTimer.setDelay(currentStepInMsec);
            } else if (activePowerUpType == PowerUp.Type.SLOW_DOWN) {
                currentStepInMsec = normalStepInMsec * 2;
                stepTimer.setDelay(currentStepInMsec);
                doubleScoreActive = false;
            }
        }

        // Cek apakah efek powerup sudah habis
        if (activePowerUpType != null && System.currentTimeMillis() > powerUpEffectEndTime) {
            activePowerUpType = null;
            currentStepInMsec = normalStepInMsec;
            stepTimer.setDelay(currentStepInMsec);
            doubleScoreActive = false;
        }

        // Spawn powerup secara acak (misal setiap kelipatan 15 skor, tidak stack)
        if (!powerUp.isActive && score > 0 && score % 15 == 0 && activePowerUpType == null) {
            powerUp.newPowerUp();
            // Pastikan tidak spawn di badan ular/food/bonus
            while (snake.contains(powerUp.x, powerUp.y) || (food.x == powerUp.x && food.y == powerUp.y) || (bonus.isActive && bonus.x == powerUp.x && bonus.y == powerUp.y)) {
                powerUp.newPowerUp();
            }
        }

        // Makan food
        if (headX == food.x && headY == food.y) {
            do {
                food.newFood();
            } while (snake.contains(food.x, food.y));
            int point = doubleScoreActive ? 12 : 6;
            score += point;
            ateSomething = true;

            if (score % 10 == 0 && !bonus.isActive) {
                bonus.newBonus();
            }
        } else if (bonus.isActive && headX == bonus.x && headY == bonus.y) {
            int point = doubleScoreActive ? 40 : 20;
            score += point;
            bonus.deactivate();
            ateSomething = true;
        }

        // Panggil move dengan grow=true jika makan, grow=false jika tidak
        snake.move(ateSomething);

        if (bonus.isActive && bonus.isExpired()) {
            bonus.deactivate();
        }

        if (snake.eatItself()) {
            stopGame();
            return;
        }

        repaint();
    }

    // --- MODIFIED INNER CLASS ---
    private class GamePanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private Image grassBackground;

        public GamePanel() {
            try {
                grassBackground = ImageIO.read(getClass().getResource("/Snake/stone.jpg"));
            } catch (IOException | IllegalArgumentException e) {
                grassBackground = null; // fallback jika gagal load
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Gambar background rumput jika tersedia
            if (grassBackground != null) {
                int imgW = grassBackground.getWidth(this);
                int imgH = grassBackground.getHeight(this);
                for (int x = 0; x < getWidth(); x += imgW) {
                    for (int y = 0; y < getHeight(); y += imgH) {
                        g.drawImage(grassBackground, x, y, this);
                    }
                }
            } else {
                setBackground(COLOR_PIT);
            }

            snake.paint(g);
            food.paint(g);
            bonus.paint(g);
            powerUp.paint(g);

            int scoreBoxWidth = 100;
            int scoreBoxHeight = 30;
            int scoreBoxX = PIT_WIDTH - scoreBoxWidth - 10;
            int scoreBoxY = 10;

            g.setColor(Color.WHITE); // Background color for the score box
            g.fillRect(scoreBoxX, scoreBoxY, scoreBoxWidth, scoreBoxHeight);

            g.setColor(scoreTextColor); // Use the RGB color for the score text
            g.setFont(FONT_DATA);
            g.drawString("Score: " + score, scoreBoxX + 10, scoreBoxY + 20);

            // Highscore box
            int highscoreBoxWidth = 140;
            int highscoreBoxHeight = 30;
            int highscoreBoxX = PIT_WIDTH - highscoreBoxWidth - 10;
            int highscoreBoxY = scoreBoxY + scoreBoxHeight + 10;

            g.setColor(Color.WHITE);
            g.fillRect(highscoreBoxX, highscoreBoxY, highscoreBoxWidth, highscoreBoxHeight);

            g.setColor(new Color(0, 102, 0)); // Hijau tua untuk highscore
            g.setFont(FONT_DATA);
            g.drawString("Highscore: " + highscore, highscoreBoxX + 10, highscoreBoxY + 20);

            // PowerUp indicator
            if (activePowerUpType != null) {
                g.setColor(Color.BLACK);
                g.setFont(FONT_DATA);
                String msg = "";
                if (activePowerUpType == PowerUp.Type.SPEED_UP) msg = "Speed Up!";
                else if (activePowerUpType == PowerUp.Type.DOUBLE_SCORE) msg = "Double Score!";
                else if (activePowerUpType == PowerUp.Type.SLOW_DOWN) msg = "Slow Down!";
                g.drawString("PowerUp: " + msg, 20, 30);
            }

            if (currentState == State.READY) {
                g.setFont(FONT_INSTRUCTION);
                g.setColor(COLOR_INSTRUCTION);
                g.drawString("Pencet tombol apapun untuk memulai", 100, PIT_HEIGHT / 4);
            }

            if (currentState == State.GAMEOVER) {
                g.setFont(FONT_GAMEOVER);
                g.setColor(COLOR_GAMEOVER);
                g.drawString("Yah kamu cupu :)", 200, PIT_HEIGHT / 2);
                g.setFont(FONT_INSTRUCTION);
                g.drawString("Pencet tombol apapun untuk memulai", 120, PIT_HEIGHT / 2 + 40);
            }
        }

        public boolean contains(int x, int y) {
            if ((x < 0) || (x >= ROWS)) {
                return false;
            }
            if ((y < 0) || (y >= COLS)) {
                return false;
            }
            return true;
        }
    }
    // --- END MODIFIED INNER CLASS ---

    public void setScoreTextColor(int r, int g, int b) {
        scoreTextColor = new Color(r, g, b);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameMain main = new GameMain();
            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(main);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Example: Set score text color to black
            main.setScoreTextColor(0, 0, 0);
        });
    }
}