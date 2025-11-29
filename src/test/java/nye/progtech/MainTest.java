package nye.progtech;

import nye.progtech.db.PlayerStat;
import nye.progtech.db.PlayerStatsRepository;
import nye.progtech.io.BoardFileReader;
import nye.progtech.io.BoardFileWriter;
import nye.progtech.model.Board;
import nye.progtech.model.CellState;
import nye.progtech.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private static final Path BOARD_PATH = Path.of("board.txt");
    private static final String DB_URL = "jdbc:h2:./amoba_db";

    // ---------- reflection helpers for private static methods -----------

    private Board invokeLoadBoard() throws Exception {
        Method m = Main.class.getDeclaredMethod("loadBoard");
        m.setAccessible(true);
        return (Board) m.invoke(null);
    }

    private void invokeSaveBoard(Board board) throws Exception {
        Method m = Main.class.getDeclaredMethod("saveBoard", Board.class);
        m.setAccessible(true);
        m.invoke(null, board);
    }

    private boolean invokeBoardAlreadyHasWinner(Board board) throws Exception {
        Method m = Main.class.getDeclaredMethod("boardAlreadyHasWinner", Board.class);
        m.setAccessible(true);
        return (Boolean) m.invoke(null, board);
    }

    private void invokeShowHighScores(PlayerStatsRepository repo) throws Exception {
        Method m = Main.class.getDeclaredMethod("showHighScores", PlayerStatsRepository.class);
        m.setAccessible(true);
        m.invoke(null, repo);
    }

    // ----------------- setup: clean board file + DB ---------------------

    @BeforeEach
    void setUp() throws Exception {
        // Clean board file
        Files.deleteIfExists(BOARD_PATH);

        // Clean H2 table if it exists
        try (Connection conn = DriverManager.getConnection(DB_URL, "sa", "");
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DROP TABLE IF EXISTS player_stats");
        }
    }

    // ---------------------- loadBoard tests -----------------------------

    @Test
    void loadBoard_createsNewBoardWhenNoFileExists() throws Exception {
        // ensure file missing
        Files.deleteIfExists(BOARD_PATH);

        Board board = invokeLoadBoard();

        assertNotNull(board);
        assertEquals(10, board.getRows());
        assertEquals(10, board.getCols());
    }

    @Test
    void loadBoard_returnsExistingBoardWhenNotFinished() throws Exception {
        // create a non-winning board and save it
        Board original = new Board(10, 10);
        original.setCell(new Position(0, 0), CellState.X); // just a single stone
        BoardFileWriter.save(original, BOARD_PATH);

        Board loaded = invokeLoadBoard();

        assertEquals(10, loaded.getRows());
        assertEquals(10, loaded.getCols());
        assertEquals(CellState.X, loaded.getCell(new Position(0, 0)));
    }

    @Test
    void loadBoard_createsNewBoardWhenSavedBoardAlreadyHasWinner() throws Exception {
        // board with a winning horizontal 5-in-a-row
        Board finished = new Board(10, 10);
        for (int c = 0; c < 5; c++) {
            finished.setCell(new Position(0, c), CellState.X);
        }
        BoardFileWriter.save(finished, BOARD_PATH);

        // capture System.out to see the message
        ByteArrayOutputStream fakeOut = new ByteArrayOutputStream();
        PrintStream fakePrint = new PrintStream(fakeOut);
        PrintStream originalOut = System.out;

        Board loaded;
        try {
            System.setOut(fakePrint);
            loaded = invokeLoadBoard();
        } finally {
            System.setOut(originalOut);
        }

        // Should create a fresh board (all empty)
        assertEquals(10, loaded.getRows());
        assertEquals(10, loaded.getCols());
        assertEquals(CellState.EMPTY, loaded.getCell(new Position(0, 0)));

        String out = fakeOut.toString();
        assertTrue(out.contains("Last saved board was finished"),
                "Expected a message about finished board");
    }

    // ---------------------- saveBoard tests -----------------------------

    @Test
    void saveBoard_writesFile() throws Exception {
        Board board = new Board(10, 10);
        board.setCell(new Position(1, 1), CellState.X);

        ByteArrayOutputStream fakeOut = new ByteArrayOutputStream();
        PrintStream fakePrint = new PrintStream(fakeOut);
        PrintStream originalOut = System.out;

        try {
            System.setOut(fakePrint);
            invokeSaveBoard(board);
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(Files.exists(BOARD_PATH), "Board file should be created");

        // verify contents by reading back
        Board loaded = BoardFileReader.load(BOARD_PATH);
        assertEquals(CellState.X, loaded.getCell(new Position(1, 1)));

        String out = fakeOut.toString();
        assertTrue(out.contains("Board saved to"), "Should print save confirmation");
    }

    // ----------------- boardAlreadyHasWinner tests ----------------------

    @Test
    void boardAlreadyHasWinner_returnsTrueWhenFiveInRow() throws Exception {
        Board board = new Board(10, 10);

        // horizontal five X in row 2
        for (int c = 0; c < 5; c++) {
            board.setCell(new Position(2, c), CellState.X);
        }

        boolean hasWinner = invokeBoardAlreadyHasWinner(board);

        assertTrue(hasWinner);
    }

    @Test
    void boardAlreadyHasWinner_returnsFalseWhenNoWinner() throws Exception {
        Board board = new Board(10, 10);
        board.setCell(new Position(0, 0), CellState.X);
        board.setCell(new Position(1, 1), CellState.O);

        boolean hasWinner = invokeBoardAlreadyHasWinner(board);

        assertFalse(hasWinner);
    }

    // ------------------ showHighScores tests ----------------------------

    @Test
    void showHighScores_printsMessageWhenNoStats() throws Exception {
        PlayerStatsRepository repo = new PlayerStatsRepository(); // will recreate table

        ByteArrayOutputStream fakeOut = new ByteArrayOutputStream();
        PrintStream fakePrint = new PrintStream(fakeOut);
        PrintStream originalOut = System.out;

        try {
            System.setOut(fakePrint);
            invokeShowHighScores(repo);
        } finally {
            System.setOut(originalOut);
        }

        String out = fakeOut.toString();
        assertTrue(out.contains("There hasn’t been a single played or won match yet."),
                "Should print 'no games' message");
    }

    @Test
    void showHighScores_printsTableWithData() throws Exception {
        PlayerStatsRepository repo = new PlayerStatsRepository();

        // Insert some stats
        repo.incrementWin("Alice");
        repo.incrementWin("Bob");
        repo.incrementWin("Bob");  // Bob has 2
        repo.incrementWin("Charlie");
        repo.incrementWin("Charlie");
        repo.incrementWin("Charlie"); // Charlie has 3

        List<PlayerStat> stats = repo.findAllOrderByWinsDesc();
        assertEquals(3, stats.size());

        ByteArrayOutputStream fakeOut = new ByteArrayOutputStream();
        PrintStream fakePrint = new PrintStream(fakeOut);
        PrintStream originalOut = System.out;

        try {
            System.setOut(fakePrint);
            invokeShowHighScores(repo);
        } finally {
            System.setOut(originalOut);
        }

        String out = fakeOut.toString();

        assertTrue(out.contains("=== HIGH SCORE ==="), "Should print title");
        assertTrue(out.contains("Charlie"), "Should list Charlie");
        assertTrue(out.contains("Bob"), "Should list Bob");
        assertTrue(out.contains("Alice"), "Should list Alice");

        // Charlie should appear before Bob, Bob before Alice
        int idxCharlie = out.indexOf("Charlie");
        int idxBob = out.indexOf("Bob");
        int idxAlice = out.indexOf("Alice");

        assertTrue(idxCharlie >= 0 && idxBob >= 0 && idxAlice >= 0);
        assertTrue(idxCharlie < idxBob, "Charlie (3 wins) should be before Bob (2)");
        assertTrue(idxBob < idxAlice, "Bob (2 wins) should be before Alice (1)");
    }

    // ---------------------- main() test --------------------------------

    @Test
    void main_quitImmediately() {
        // Simulate user pressing "Q" to quit
        String input = "Q" + System.lineSeparator();
        ByteArrayInputStream fakeIn =
                new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream fakeOut = new ByteArrayOutputStream();
        PrintStream fakePrint = new PrintStream(fakeOut);

        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        try {
            System.setIn(fakeIn);
            System.setOut(fakePrint);

            Main.main(new String[0]);

        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String out = fakeOut.toString();
        assertTrue(out.contains("==== MENU ===="), "Menu should be shown");
        assertTrue(out.contains("Good bye!"), "Quit message should be shown");
    }
}
