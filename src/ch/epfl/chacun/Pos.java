package ch.epfl.chacun;

/**
 * Represents a position on the board
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 *
 * @param x (int) Horizontal coordinate
 * @param y (int) Vertical coordinate
 */
public record Pos(int x, int y) {
    public static final Pos ORIGIN = new Pos(0, 0);

    /**
     * Translates the current position by the given coordinates
     *
     * @param dX (int) delta of the horizontal coordinate
     * @param dY (int) delta of the vertical coordinate
     *
     * @return (Pos) the translated position
     */
    public Pos translated(int dX,int dY) {
        return new Pos(x + dX, y + dY);
    }

    /**
     * Returns the neighbor of the current position in the given direction
     *
     * @param direction (Direction) the direction of the neighbor
     *
     * @return (Pos) the neighbor position
     */
    public Pos neighbor(Direction direction) {
        return switch (direction) {
            case E -> translated(1, 0);
            case N -> translated(0, -1);
            case S -> translated(0, 1);
            case W -> translated(-1, 0);
        };
    }
}
