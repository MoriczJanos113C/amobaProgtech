package nye.progtech.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void constructor_acceptsValidArguments() {
        Player player = new Player("Alice", PlayerType.HUMAN, CellState.X);

        assertEquals("Alice", player.getName());
        assertEquals(PlayerType.HUMAN, player.getType());
        assertEquals(CellState.X, player.getStone());
        assertEquals(0, player.getScore());
    }

    @Test
    void constructor_rejectsNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player(null, PlayerType.HUMAN, CellState.X));
    }

    @Test
    void constructor_rejectsBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("   ", PlayerType.HUMAN, CellState.X));
    }

    @Test
    void constructor_rejectsNullType() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("Alice", null, CellState.X));
    }

    @Test
    void constructor_rejectsNullStone() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("Alice", PlayerType.HUMAN, null));
    }

    @Test
    void constructor_rejectsEmptyStone() {
        assertThrows(IllegalArgumentException.class,
                () -> new Player("Alice", PlayerType.HUMAN, CellState.EMPTY));
    }

    @Test
    void score_isZeroByDefault_andCanBeIncrementedAndReset() {
        Player player = new Player("Alice", PlayerType.HUMAN, CellState.X);

        assertEquals(0, player.getScore());

        player.incrementScore();
        player.incrementScore();
        assertEquals(2, player.getScore());

        player.resetScore();
        assertEquals(0, player.getScore());
    }

    @Test
    void isHumanAndIsComputerReflectType() {
        Player human = new Player("Human", PlayerType.HUMAN, CellState.X);
        Player computer = new Player("Bot", PlayerType.COMPUTER, CellState.O);

        assertTrue(human.isHuman());
        assertFalse(human.isComputer());

        assertFalse(computer.isHuman());
        assertTrue(computer.isComputer());
    }

    @Test
    void equalsAndHashCode_useNameTypeAndStone() {
        Player p1 = new Player("Alice", PlayerType.HUMAN, CellState.X);
        Player p2 = new Player("Alice", PlayerType.HUMAN, CellState.X);
        Player p3 = new Player("Bob", PlayerType.HUMAN, CellState.X);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());

        assertNotEquals(p1, p3);
    }

    @Test
    void hasWon_horizontalFiveInARow_returnsTrue() {
        Board board = new Board(10, 5);
        Player player = new Player("Alice", PlayerType.HUMAN, CellState.X);


        for (int c = 0; c < 5; c++) {
            board.setCell(new Position(0, c), CellState.X);
        }

        boolean won = player.hasWon(board, new Position(0, 4));
        assertTrue(won);
    }

    @Test
    void hasWon_verticalFiveInARow_returnsTrue() {
        Board board = new Board(10, 5);
        Player player = new Player("Alice", PlayerType.HUMAN, CellState.X);


        for (int r = 0; r < 5; r++) {
            board.setCell(new Position(r, 0), CellState.X);
        }

        boolean won = player.hasWon(board, new Position(4, 0));
        assertTrue(won);
    }

    @Test
    void hasWon_diagonalDownRightFiveInARow_returnsTrue() {
        Board board = new Board(10, 5);
        Player player = new Player("Alice", PlayerType.HUMAN, CellState.X);

        // (0,0) (1,1) (2,2) (3,3) (4,4)
        for (int i = 0; i < 5; i++) {
            board.setCell(new Position(i, i), CellState.X);
        }

        boolean won = player.hasWon(board, new Position(4, 4));
        assertTrue(won);
    }

    @Test
    void hasWon_diagonalDownLeftFiveInARow_returnsTrue() {
        Board board = new Board(10, 5);
        Player player = new Player("Alice", PlayerType.HUMAN, CellState.X);

        // (0,4) (1,3) (2,2) (3,1) (4,0)
        for (int i = 0; i < 5; i++) {
            board.setCell(new Position(i, 4 - i), CellState.X);
        }

        boolean won = player.hasWon(board, new Position(4, 0));
        assertTrue(won);
    }

    @Test
    void hasWon_returnsFalseWhenOnlyFourInARow() {
        Board board = new Board(10, 5);
        Player player = new Player("Alice", PlayerType.HUMAN, CellState.X);


        for (int c = 0; c < 4; c++) {
            board.setCell(new Position(0, c), CellState.X);
        }

        boolean won = player.hasWon(board, new Position(0, 3));
        assertFalse(won);
    }

    @Test
    void hasWon_returnsFalseWhenLineIsBrokenByOtherStone() {
        Board board = new Board(10, 5);
        Player player = new Player("Alice", PlayerType.HUMAN, CellState.X);

        // X X O X X (still only 2+2 consecutive)
        board.setCell(new Position(0, 0), CellState.X);
        board.setCell(new Position(0, 1), CellState.X);
        board.setCell(new Position(0, 2), CellState.O);
        board.setCell(new Position(0, 3), CellState.X);
        board.setCell(new Position(0, 4), CellState.X);

        boolean won = player.hasWon(board, new Position(0, 4));
        assertFalse(won);
    }
}
