package nye.progtech.model;

import java.util.Objects;

/**
 * Immutable representation of a board position (row, column).
 */
public final class Position {

    /** Row index of the position. */
    private final int row;

    /** Column index of the position. */
    private final int col;

    /**
     * Creates a new immutable position on the board.
     *
     * @param rowIndex the row index of the position (must be non-negative)
     * @param colIndex the column index of the position (must be non-negative)
     */
    public Position(final int rowIndex, final int colIndex) {
        if (rowIndex < 0 || colIndex < 0) {
            throw new IllegalArgumentException(
                    "Row and column must be non-negative."
            );
        }
        this.row = rowIndex;
        this.col = colIndex;
    }

    /**
     * Returns the row index of this position.
     *
     * @return the row index
     */
    public int row() {
        return row;
    }

    /**
     * Returns the column index of this position.
     *
     * @return the column index
     */
    public int col() {
        return col;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position that)) {
            return false;
        }
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "(%d, %d)".formatted(row, col);
    }
}
