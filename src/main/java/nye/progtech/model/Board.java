package nye.progtech.model;

import java.util.Arrays;

public final class Board {
    private final int rows;
    private final int cols;
    private final CellState[][] grid;

    public Board(int rows, int cols) {
        // 5 <= M <= N <= 25
        if (cols < 5 || rows > 25 || cols > 25 || cols > rows) {
            throw new IllegalArgumentException(
                    "Invalid board size: 5 <= M <= N <= 25 must hold (rows=N, cols=M)."
            );
        }
        this.rows = rows;
        this.cols = cols;
        this.grid = new CellState[rows][cols];
        for (CellState[] row : grid) {
            Arrays.fill(row, CellState.EMPTY);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public CellState getCell(Position pos) {
        validatePosition(pos);
        return grid[pos.row()][pos.col()];
    }

    public void setCell(Position pos, CellState state) {
        validatePosition(pos);
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null.");
        }
        grid[pos.row()][pos.col()] = state;
    }

    public boolean isInside(Position pos) {
        return pos.row() >= 0 && pos.row() < rows
                && pos.col() >= 0 && pos.col() < cols;
    }

    private void validatePosition(Position pos) {
        if (pos == null || !isInside(pos)) {
            throw new IllegalArgumentException("Position out of bounds: " + pos);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // column headers (A, B, C, …)
        sb.append("    "); // padding for row numbers
        for (int c = 0; c < cols; c++) {
            char header = (char) ('A' + c);
            sb.append(header).append(' ');
        }
        sb.append(System.lineSeparator());

        // rows
        for (int r = 0; r < rows; r++) {
            sb.append(String.format("%2d  ", r + 1)); // row number (1-indexed)
            for (int c = 0; c < cols; c++) {
                sb.append(symbolAt(r, c)).append(' ');
            }
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    private char symbolAt(int r, int c) {
        return grid[r][c].isEmpty() ? '.' : grid[r][c].getSymbol();
    }
}