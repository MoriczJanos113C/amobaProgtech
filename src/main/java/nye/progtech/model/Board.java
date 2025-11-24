package nye.progtech.model;


import java.util.Arrays;

/** Represents the game board. */
public final class Board {

    /** Number of rows in the board. */
    private final int boardRows;

    /** Number of columns in the board. */
    private final int boardCols;

    /** Grid representing the cells of the board. */
    private final CellState[][] grid;

    /** Minimum allowed board size. */
    private static final int MIN_SIZE = 5;

    /** Maximum allowed board size. */
    private static final int MAX_SIZE = 25;

    /**
     * Constructs a Board with the given number of rows and columns.
     *
     * @param rows number of rows (N)
     * @param cols number of columns (M)
     * @throws IllegalArgumentException if size is invalid
     */
    public Board(final int rows, final int cols) {
        if (cols < MIN_SIZE
                || rows > MAX_SIZE
                || cols > MAX_SIZE
                || cols > rows) {
            throw new IllegalArgumentException(
                    //(rows=N, cols=M)
                    "Invalid board size: 5 <= M <= N <= 25 must hold."
            );
        }
        this.boardRows = rows;
        this.boardCols = cols;
        this.grid = new CellState[rows][cols];
        for (CellState[] row : grid) {
            Arrays.fill(row, CellState.EMPTY);
        }
    }

    /** @return number of rows */
    public int getRows() {
        return boardRows;
    }

    /** @return number of columns */
    public int getCols() {
        return boardCols;
    }

    /**
     * Returns the state of the cell at the given position.
     *
     * @param pos the position to query
     * @return the cell state
     */
    public CellState getCell(final Position pos) {
        validatePosition(pos);
        return grid[pos.row()][pos.col()];
    }

    /**
     * Sets the cell at the given position to the specified state.
     *
     * @param pos the position to set
     * @param state the state to set
     */
    public void setCell(final Position pos, final CellState state) {
        validatePosition(pos);
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null.");
        }
        grid[pos.row()][pos.col()] = state;
    }

    /**
     * Checks if the position is inside the board boundaries.
     *
     * @param pos the position to check
     * @return true if inside the board
     */
    public boolean isInside(final Position pos) {
        return pos.row() >= 0 && pos.row() < boardRows
                && pos.col() >= 0 && pos.col() < boardCols;
    }

    /**
     * Validates the position.
     *
     * @param pos the position to validate
     */
    private void validatePosition(final Position pos) {
        if (pos == null || !isInside(pos)) {
            throw new IllegalArgumentException(
                    "Position out of bounds: " + pos
            );
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // column headers (A, B, C, …)
        sb.append("    ");
        for (int c = 0; c < boardCols; c++) {
            char header = (char) ('A' + c);
            sb.append(header).append(' ');
        }
        sb.append(System.lineSeparator());

        // rows
        for (int r = 0; r < boardRows; r++) {
            sb.append(String.format("%2d  ", r + 1));
            for (int c = 0; c < boardCols; c++) {
                sb.append(symbolAt(r, c)).append(' ');
            }
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    /**
     * Returns the symbol of the cell at the given coordinates.
     *
     * @param r row index
     * @param c column index
     * @return character representing the cell
     */
    private char symbolAt(final int r, final int c) {
        return grid[r][c].isEmpty() ? '.' : grid[r][c].getSymbol();
    }
}
