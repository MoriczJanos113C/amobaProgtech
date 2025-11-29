package nye.progtech.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void constructor_acceptsValidSize() {
        Board board = new Board(5, 5);

        assertEquals(5, board.getRows());
        assertEquals(5, board.getCols());
    }

    @Test
    void constructor_rejectsTooFewColumns() {
        assertThrows(IllegalArgumentException.class,
                () -> new Board(5, 4));
    }

    @Test
    void constructor_rejectsRowsGreaterThanMax() {
        assertThrows(IllegalArgumentException.class,
                () -> new Board(26, 5));
    }

    @Test
    void constructor_rejectsColsGreaterThanMax() {
        assertThrows(IllegalArgumentException.class,
                () -> new Board(25, 26));
    }

    @Test
    void constructor_rejectsColsGreaterThanRows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Board(5, 6));
    }

    @Test
    void allCellsAreEmptyAfterConstruction() {
        Board board = new Board(5, 5);

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Position pos = new Position(r, c);
                Assertions.assertEquals(CellState.EMPTY, board.getCell(pos));
            }
        }
    }

    @Test
    void setCellAndGetCell_workForValidPosition() {
        Board board = new Board(5, 5);
        Position pos = new Position(2, 3);

        board.setCell(pos, CellState.X);

        assertEquals(CellState.X, board.getCell(pos));
    }

    @Test
    void setCell_throwsWhenStateIsNull() {
        Board board = new Board(5, 5);
        Position pos = new Position(0, 0);

        assertThrows(IllegalArgumentException.class,
                () -> board.setCell(pos, null));
    }

    @Test
    void getCell_throwsWhenPositionIsNull() {
        Board board = new Board(5, 5);

        assertThrows(IllegalArgumentException.class,
                () -> board.getCell(null));
    }

    @Test
    void setCell_throwsWhenPositionIsOutOfBounds() {
        Board board = new Board(5, 5);
        // row == getRows() is out of bounds
        Position pos = new Position(board.getRows(), 0);

        assertThrows(IllegalArgumentException.class,
                () -> board.setCell(pos, CellState.X));
    }

    @Test
    void isInside_returnsTrueForValidPositions() {
        Board board = new Board(5, 5);

        assertTrue(board.isInside(new Position(0, 0)));
        assertTrue(board.isInside(new Position(4, 4)));
    }

    @Test
    void isInside_returnsFalseForInvalidPositions() {
        Board board = new Board(5, 5);

        assertFalse(board.isInside(new Position(5, 0)));
        assertFalse(board.isInside(new Position(0, 5)));
    }

    @Test
    void toString_containsHeadersAndRowNumbers() {
        Board board = new Board(5, 5);
        String s = board.toString();

        // column headers A B C ...
        assertTrue(s.contains("A B C D E"));

        // row numbers 1..5
        assertTrue(s.contains(" 1  "));
        assertTrue(s.contains(" 5  "));
    }
}
