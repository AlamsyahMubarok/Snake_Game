package Snake;

import java.awt.Graphics;
/**
 * SnakeSegment represents one horizontal or vertical segment of a snake. The "head" of
 * this segment is at (headX, headY). The segment is drawn starting from the "head"
 * and proceeding "length" cells in "direction", until it reaches the "tail".
 */
public class SnakeSegment {
    // == Define named constants ==
    /** Size of each body cell in pixels */
    public static final int CELL_SIZE = GameMain.CELL_SIZE;

    // == Define properties (package access) ==
    /** (x, y) of this segment head */
    int headX, headY;
    /** length of this segment */
    int length;
    /** Movement direction of this segment */
    Move direction;

    /**
     * Construct a new snake segment at given (headX, headY), length and direction.
     */
    public SnakeSegment(int headX, int headY, int length, Move direction) {
        this.headX = headX;
        this.headY = headY;
        this.direction = direction;
        this.length = length;
    }

    /**
     * Grow by adding one cell in front of the head of this segment.
     */
    public void grow() {
        length++;
        // Need to adjust the headX or headY
        switch (direction) {
            case LEFT:  headX--; break;
            case RIGHT: headX++; break;
            case UP:    headY--; break;
            case DOWN:  headY++; break;
        }
    }

    /**
     * Shrink by removing one cell from the tail of this segment.
     */
    public void shrink() {
        length--;
        // no change in headX and headY needed
    }

    // Helper method to get the tail x.
    private int getTailX() {
        if (direction == Move.LEFT) {
            return headX + length - 1;
        } else if (direction == Move.RIGHT) {
            return headX - length + 1;
        } else {   // UP or DOWN
            return headX;
        }
    }

    // Helper method to get the tail y.
    private int getTailY() {
        if (direction == Move.DOWN) {
            return headY - length + 1;
        } else if (direction == Move.UP) {
            return headY + length - 1;
        } else {  // LEFT or RIGHT
            return headY;
        }
    }

    /**
     * Returns true if the snake segment contains the given cell.
     * Used for collision detection.
     */
    public boolean contains(int x, int y) {
        switch (direction) {
            case LEFT:  return ((y == headY) && ((x >= headX) && (x <= getTailX())));
            case RIGHT: return ((y == headY) && ((x <= headX) && (x >= getTailX())));
            case UP:    return ((x == headX) && ((y >= headY) && (y <= getTailY())));
            case DOWN:  return ((x == headX) && ((y <= headY) && (y >= getTailY())));
            default:    return false;  // prevent syntax error
        }
    }

    /**
     * Paint itself via the Graphics object.
     * Since SnakeSegments are encapsulated in Snake, snakeSegment.paint(Graphics)
     * shall be called in snake.paint(Graphics).
     */
    public void paint(Graphics g) {
        int x = headX, y = headY;

        switch (direction) {
            case LEFT:
                for (int i = 0; i < length; i++) {
                    g.fill3DRect(x*CELL_SIZE, y*CELL_SIZE, CELL_SIZE-1, CELL_SIZE-1, true);
                    // width/height minus-1 for better looking adjacent raised rectangles
                    x++;
                }
                break;
            case RIGHT:
                for (int i = 0; i < length; i++) {
                    g.fill3DRect(x*CELL_SIZE, y*CELL_SIZE, CELL_SIZE-1, CELL_SIZE-1, true);
                    x--;
                }
                break;
            case UP:
                for (int i = 0; i < length; i++) {
                    g.fill3DRect(x*CELL_SIZE, y*CELL_SIZE, CELL_SIZE-1, CELL_SIZE-1, true);
                    y++;
                }
                break;
            case DOWN:
                for (int i = 0; i < length; i++) {
                    g.fill3DRect(x*CELL_SIZE, y*CELL_SIZE, CELL_SIZE-1, CELL_SIZE-1, true);
                    y--;
                }
                break;
        }
    }
}