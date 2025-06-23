package Snake;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * A Snake is made up of one or more SnakeSegments.
 * This class implements GameState interface but only overrides the
 * newGame(). See game state diagram.
 *
 * When the snake moves forward:
 * - The head segment grows by one cell.
 * - If no food is eaten, the tail segment shrink by one cell; otherwise
 *   no change.
 * - No change for the intermediate segments.
 */
public class Snake implements StateTransition {
    private static final int INIT_LENGTH = 12;
    public static final int CELL_SIZE = GameMain.CELL_SIZE;
    public static final Color COLOR_HEAD = Color.RED;
    public static final Color COLOR_BODY = Color.PINK;

    List<SnakeSegment> segments = new ArrayList<>();
    Move direction;

    // Untuk menyimpan posisi seluruh tubuh ular
    private List<int[]> bodyPositions = new ArrayList<>();

    public Snake() { }

    @Override
    public void initGame() {}

    @Override
    public void newGame() {
        segments.clear();
        bodyPositions.clear();
        int headX = GameMain.COLS / 2;
        int headY = GameMain.ROWS / 2;
        direction = Move.UP;
        // Inisialisasi posisi tubuh ular
        for (int i = 0; i < INIT_LENGTH; i++) {
            bodyPositions.add(new int[]{headX, headY + i});
        }
    }

    @Override
    public void startGame() {}

    @Override
    public void stopGame() {}

    public void changeDirection(Move newDir) {
        if ((newDir != direction) &&
                ((newDir == Move.UP && direction != Move.DOWN) ||
                        (newDir == Move.DOWN && direction != Move.UP) ||
                        (newDir == Move.LEFT && direction != Move.RIGHT) ||
                        (newDir == Move.RIGHT && direction != Move.LEFT))) {
            direction = newDir;
        }
    }

    /**
     * Memindahkan ular satu langkah ke depan.
     * Jika grow = true, ular bertambah panjang (tidak menghapus ekor).
     * Jika grow = false, ular bergerak normal (ekor dihapus).
     */
    public void move(boolean grow) {
        int[] head = bodyPositions.get(0);
        int newX = head[0];
        int newY = head[1];
        switch (direction) {
            case UP:    newY--; break;
            case DOWN:  newY++; break;
            case LEFT:  newX--; break;
            case RIGHT: newX++; break;
        }
        // Wrap-around
        if (newX < 0) newX = GameMain.COLS - 1;
        if (newX >= GameMain.COLS) newX = 0;
        if (newY < 0) newY = GameMain.ROWS - 1;
        if (newY >= GameMain.ROWS) newY = 0;

        bodyPositions.add(0, new int[]{newX, newY});
        if (!grow) {
            bodyPositions.remove(bodyPositions.size() - 1);
        }
    }

    public int getHeadX() {
        return bodyPositions.get(0)[0];
    }

    public int getHeadY() {
        return bodyPositions.get(0)[1];
    }

    public int getLength() {
        return bodyPositions.size();
    }

    public boolean contains(int x, int y) {
        for (int[] pos : bodyPositions) {
            if (pos[0] == x && pos[1] == y) return true;
        }
        return false;
    }

    public boolean eatItself() {
        int headX = getHeadX();
        int headY = getHeadY();
        for (int i = 1; i < bodyPositions.size(); i++) {
            int[] pos = bodyPositions.get(i);
            if (pos[0] == headX && pos[1] == headY) return true;
        }
        return false;
    }

    public void setHeadPosition(int x, int y) {
        if (!bodyPositions.isEmpty()) {
            bodyPositions.get(0)[0] = x;
            bodyPositions.get(0)[1] = y;
        }
    }

    public void paint(Graphics g) {
        // Body
        g.setColor(COLOR_BODY);
        for (int i = 1; i < bodyPositions.size(); i++) {
            int[] pos = bodyPositions.get(i);
            g.fill3DRect(pos[0] * CELL_SIZE, pos[1] * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1, true);
        }
        // Head
        if (!bodyPositions.isEmpty()) {
            g.setColor(COLOR_HEAD);
            int[] head = bodyPositions.get(0);
            int offset = 2;
            g.fill3DRect(head[0] * CELL_SIZE - offset / 2, head[1] * CELL_SIZE - offset / 2,
                    CELL_SIZE - 1 + offset, CELL_SIZE - 1 + offset, true);
        }
    }
}