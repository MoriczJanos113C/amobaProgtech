package nye.progtech;

import nye.progtech.io.BoardFileReader;
import nye.progtech.io.BoardFileWriter;
import nye.progtech.model.Board;
import nye.progtech.model.CellState;
import nye.progtech.game.GameEngine;
import nye.progtech.model.Player;
import nye.progtech.db.PlayerStat;
import nye.progtech.db.PlayerStatsRepository;
import nye.progtech.model.PlayerType;
import nye.progtech.model.Position;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/** Main class for running the game application. */
public final class Main {

    /** Path to the saved board file. */
    private static final Path BOARD_PATH = Path.of("board.txt");

    /** Board default size. */
    private static final int DEFAULT_BOARD_SIZE = 10;

    /** Private constructor to prevent instantiation of this utility class. */
    private Main() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated"
        );
    }

    /**
     * Entry point of the program.
     *
     * @param args command-line arguments
     */
    public static void main(final String[] args) {
        final Scanner sc = new Scanner(System.in);
        final PlayerStatsRepository statsRepo = new PlayerStatsRepository();

        while (true) {
            System.out.println("==== MENU ====");
            System.out.println("1 - New Game / Continue");
            System.out.println("2 - High score table");
            System.out.println("Q - Quit");
            System.out.print("Choose: ");

            final String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> startGame(sc, statsRepo);
                case "2" -> showHighScores(statsRepo);
                case "Q" -> {
                    System.out.println("Good bye!");
                    return;
                }
                default ->
                        System.out.println("Unknown choice, please try again.");
            }
        }
    }

    /**
     * Starts a new game session.
     *
     * @param sc Scanner for user input
     * @param statsRepo repository for player statistics
     */
    private static void startGame(
            final Scanner sc,
            final PlayerStatsRepository statsRepo) {
        final Board board = loadBoard();

        System.out.print("Enter your name: ");
        String name = sc.nextLine().trim();
        if (name.isBlank()) {
            name = "Player";
        }

        final Player human = new Player(name, PlayerType.HUMAN, CellState.X);
        final Player computer = new Player(
                "Computer", PlayerType.COMPUTER, CellState.O
        );

        final GameEngine game = new GameEngine(
                board, human, computer, statsRepo
        );
        game.play();

        saveBoard(board);
    }

    /**
     * Shows the high score table.
     *
     * @param statsRepo repository containing player statistics
     */
    private static void showHighScores(final PlayerStatsRepository statsRepo) {
        final List<PlayerStat> stats = statsRepo.findAllOrderByWinsDesc();

        if (stats.isEmpty()) {
            System.out.println(
                    "There hasn’t been a single played or won match yet."
            );
        } else {
            System.out.println("=== HIGH SCORE ===");
            System.out.printf("%-20s %s%n", "Player", "Wins");
            System.out.println("-------------------------------");
            for (final PlayerStat ps : stats) {
                System.out.printf("%-20s %d%n", ps.name(), ps.wins());
            }
            System.out.println();
        }
    }

    /**
     * Loads the saved board or creates a new one.
     *
     * @return the loaded or new board
     */
    private static Board loadBoard() {
        if (Files.exists(BOARD_PATH)) {
            try {
                final Board board = BoardFileReader.load(BOARD_PATH);

                // Check if last game was already won
                if (boardAlreadyHasWinner(board)) {
                    System.out.println(
                            "Last saved board was finished->starting new board."
                    );
                    return new Board(DEFAULT_BOARD_SIZE, DEFAULT_BOARD_SIZE);
                }

                return board;

            } catch (Exception e) {
                System.err.println("Could not load board: " + e.getMessage());
            }
        }

        return new Board(DEFAULT_BOARD_SIZE, DEFAULT_BOARD_SIZE);
    }

    /**
     * Saves the current board to file.
     *
     * @param board the board to save
     */
    private static void saveBoard(final Board board) {
        try {
            BoardFileWriter.save(board, BOARD_PATH);
            System.out.println("Board saved to " + BOARD_PATH.toAbsolutePath());
        } catch (final IOException e) {
            System.err.println("Failed to save board: " + e.getMessage());
        }
    }

    /**
     * Checks if the board already contains a winner.
     *
     * @param board the board to check
     * @return true if there is a winner
     */
    private static boolean boardAlreadyHasWinner(final Board board) {
        final Player dummyX = new Player("X", PlayerType.HUMAN, CellState.X);
        final Player dummyO = new Player("O", PlayerType.COMPUTER, CellState.O);

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                final Position pos = new Position(r, c);
                final CellState cell = board.getCell(pos);

                if (cell == CellState.X && dummyX.hasWon(board, pos)) {
                    return true;
                }
                if (cell == CellState.O && dummyO.hasWon(board, pos)) {
                    return true;
                }
            }
        }
        return false;
    }
}
