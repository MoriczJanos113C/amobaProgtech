package nye.progtech.game;

import nye.progtech.db.PlayerStatsRepository;
import nye.progtech.model.Board;
import nye.progtech.model.CellState;
import nye.progtech.model.Player;
import nye.progtech.model.PlayerType;
import nye.progtech.model.Position;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {


    private GameEngine createEngine(int rows, int cols) {
        Board board = new Board(rows, cols);
        Player human = new Player("Human", PlayerType.HUMAN, CellState.X);
        Player computer = new Player("Computer", PlayerType.COMPUTER, CellState.O);
        PlayerStatsRepository repo = new PlayerStatsRepository();
        return new GameEngine(board, human, computer, repo);
    }

    private Position invokeParseMove(GameEngine engine, String input) throws Exception {
        Method m = GameEngine.class.getDeclaredMethod("parseMove", String.class);
        m.setAccessible(true);
        return (Position) m.invoke(engine, input);
    }

    private Position invokeGenerateRandomMove(GameEngine engine) throws Exception {
        Method m = GameEngine.class.getDeclaredMethod("generateRandomMove");
        m.setAccessible(true);
        return (Position) m.invoke(engine);
    }

    private String invokeToNotation(GameEngine engine, Position pos) throws Exception {
        Method m = GameEngine.class.getDeclaredMethod("toNotation", Position.class);
        m.setAccessible(true);
        return (String) m.invoke(engine, pos);
    }

    // -------------- constructor tests -----------------

    @Test
    void constructor_throwsWhenBoardIsNull() {
        Player human = new Player("Human", PlayerType.HUMAN, CellState.X);
        Player computer = new Player("Computer", PlayerType.COMPUTER, CellState.O);
        PlayerStatsRepository repo = new PlayerStatsRepository();

        assertThrows(NullPointerException.class,
                () -> new GameEngine(null, human, computer, repo));
    }

    @Test
    void constructor_throwsWhenHumanIsNull() {
        Board board = new Board(5, 5);
        Player computer = new Player("Computer", PlayerType.COMPUTER, CellState.O);
        PlayerStatsRepository repo = new PlayerStatsRepository();

        assertThrows(NullPointerException.class,
                () -> new GameEngine(board, null, computer, repo));
    }

    @Test
    void constructor_throwsWhenComputerIsNull() {
        Board board = new Board(5, 5);
        Player human = new Player("Human", PlayerType.HUMAN, CellState.X);
        PlayerStatsRepository repo = new PlayerStatsRepository();

        assertThrows(NullPointerException.class,
                () -> new GameEngine(board, human, null, repo));
    }

    @Test
    void constructor_throwsWhenStatsRepoIsNull() {
        Board board = new Board(5, 5);
        Player human = new Player("Human", PlayerType.HUMAN, CellState.X);
        Player computer = new Player("Computer", PlayerType.COMPUTER, CellState.O);

        assertThrows(NullPointerException.class,
                () -> new GameEngine(board, human, computer, null));
    }

    // -------------- parseMove tests -----------------

    @Test
    void parseMove_parsesValidSimpleInput() throws Exception {
        GameEngine engine = createEngine(10, 5);

        Position pos = invokeParseMove(engine, "A1");

        assertNotNull(pos);
        assertEquals(0, pos.row());
        assertEquals(0, pos.col());
    }

    @Test
    void parseMove_parsesLowercaseInput() throws Exception {
        GameEngine engine = createEngine(10, 5);

        Position pos = invokeParseMove(engine, "c10");

        assertNotNull(pos);
        assertEquals(9, pos.row());   // 10 -> index 9
        assertEquals(2, pos.col());   // 'C' -> 2
    }

    @Test
    void parseMove_returnsNullWhenTooShort() throws Exception {
        GameEngine engine = createEngine(10, 5);

        Position pos = invokeParseMove(engine, "A");

        assertNull(pos);
    }

    @Test
    void parseMove_returnsNullWhenFirstCharNotLetter() throws Exception {
        GameEngine engine = createEngine(10, 5);

        Position pos = invokeParseMove(engine, "1A");

        assertNull(pos);
    }

    @Test
    void parseMove_returnsNullWhenRowIsNotNumber() throws Exception {
        GameEngine engine = createEngine(10, 5);

        Position pos = invokeParseMove(engine, "AZZ");

        assertNull(pos);
    }

    // -------------- toNotation tests -----------------

    @Test
    void toNotation_convertsTopLeftCorrectly() throws Exception {
        GameEngine engine = createEngine(10, 5);

        String notation = invokeToNotation(engine, new Position(0, 0));

        assertEquals("A1", notation);
    }

    @Test
    void toNotation_convertsArbitraryPositionCorrectly() throws Exception {
        GameEngine engine = createEngine(10, 5);

        // row index 4 -> 5, col index 3 -> 'D'
        String notation = invokeToNotation(engine, new Position(4, 3));

        assertEquals("D5", notation);
    }

    // -------------- generateRandomMove tests -----------------

    @Test
    void generateRandomMove_returnsEmptyCellInsideBoard() throws Exception {
        int rows = 5;
        int cols = 5;
        Board board = new Board(rows, cols);
        Player human = new Player("Human", PlayerType.HUMAN, CellState.X);
        Player computer = new Player("Computer", PlayerType.COMPUTER, CellState.O);
        PlayerStatsRepository repo = new PlayerStatsRepository();
        GameEngine engine = new GameEngine(board, human, computer, repo);

        // mark one non-empty cell
        board.setCell(new Position(0, 0), CellState.X);

        // call multiple times to check behavior under randomness
        for (int i = 0; i < 10; i++) {
            Position move = invokeGenerateRandomMove(engine);
            assertNotNull(move);
            assertTrue(board.isInside(move), "Move must be inside board");
            assertTrue(board.getCell(move).isEmpty(), "Move must be on empty cell");
        }
    }

    @Test
    void generateRandomMove_returnsNullWhenBoardIsFull() throws Exception {
        int rows = 5;
        int cols = 5;
        Board board = new Board(rows, cols);
        Player human = new Player("Human", PlayerType.HUMAN, CellState.X);
        Player computer = new Player("Computer", PlayerType.COMPUTER, CellState.O);
        PlayerStatsRepository repo = new PlayerStatsRepository();
        GameEngine engine = new GameEngine(board, human, computer, repo);

        // fill entire board
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board.setCell(new Position(r, c), CellState.X);
            }
        }

        Position move = invokeGenerateRandomMove(engine);

        assertNull(move);
    }

    // -------------- play() test -----------------

    @Test
    void play_simulatedUserFlow_coversBranches() {
        // Simulated user input:
        //  "1A" -> invalid format (parseMove returns null, first char not a letter)
        //  "A1" -> valid move
        //  "q"  -> quit
        String input = String.join(System.lineSeparator(),
                "1A",
                "A1",
                "q"
        ) + System.lineSeparator();

        ByteArrayInputStream fakeIn =
                new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream fakeOut = new ByteArrayOutputStream();
        PrintStream fakePrint = new PrintStream(fakeOut);

        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        try {
            System.setIn(fakeIn);
            System.setOut(fakePrint);

            Board board = new Board(5, 5);
            Player human = new Player("Human", PlayerType.HUMAN, CellState.X);
            Player computer = new Player("Computer", PlayerType.COMPUTER, CellState.O);
            PlayerStatsRepository repo = new PlayerStatsRepository();

            GameEngine engine = new GameEngine(board, human, computer, repo);

            // Run the interactive loop with fake IO
            engine.play();

            String out = fakeOut.toString();

            // - invalid format warning
            // - a message about placing at A1
            // - a computer move
            // - exit message
            assertTrue(out.contains("Invalid format"), "Should warn about invalid input format");
            assertTrue(out.contains("You placed at A1"), "Should report human move at A1");
            assertTrue(out.contains("Computer moved to"), "Should report a computer move");
            assertTrue(out.contains("Game exited"), "Should show exit message after 'q'");
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
