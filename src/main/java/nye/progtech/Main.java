package nye.progtech;

import nye.progtech.db.PlayerStat;
import nye.progtech.db.PlayerStatsRepository;
import nye.progtech.game.GameEngine;
import nye.progtech.io.BoardFileReader;
import nye.progtech.io.BoardFileWriter;
import nye.progtech.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Path BOARD_PATH = Path.of("board.txt");

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        PlayerStatsRepository statsRepo = new PlayerStatsRepository();

        while (true) {
            System.out.println("==== MENU ====");
            System.out.println("1 - New Game / Continue");
            System.out.println("2 - High score table");
            System.out.println("Q - Quit");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> startGame(sc, statsRepo);
                case "2" -> showHighScores(statsRepo);
                case "Q" -> {
                    System.out.println("Good bye!");
                    return;
                }
                default -> System.out.println("Unknown choice, please try again.");
            }
        }
    }

    private static void startGame(Scanner sc, PlayerStatsRepository statsRepo) {
        Board board = loadBoard();

        System.out.print("Enter your name: ");
        String name = sc.nextLine().trim();
        if (name.isBlank()) name = "Player";

        Player human = new Player(name, PlayerType.HUMAN, CellState.X);
        Player computer = new Player("Computer", PlayerType.COMPUTER, CellState.O);

        GameEngine game = new GameEngine(board, human, computer, statsRepo);
        game.play();

        saveBoard(board);
    }

    private static void showHighScores(PlayerStatsRepository statsRepo) {
        List<PlayerStat> stats = statsRepo.findAllOrderByWinsDesc();

        if (stats.isEmpty()) {
            System.out.println("There hasn’t been a single played or won match yet.");
            return;
        }

        System.out.println("=== HIGH SCORE ===");
        System.out.printf("%-20s %s%n", "Player", "Wins");
        System.out.println("-------------------------------");
        for (PlayerStat ps : stats) {
            System.out.printf("%-20s %d%n", ps.name(), ps.wins());
        }
        System.out.println();
    }

    private static Board loadBoard() {
        if (Files.exists(BOARD_PATH)) {
            try {
                Board board = BoardFileReader.load(BOARD_PATH);

                // Check if last game was already won
                if (boardAlreadyHasWinner(board)) {
                    System.out.println("Last saved board was from a finished game -> starting new board.");
                    return new Board(10, 10);
                }

                return board;

            } catch (Exception e) {
                System.err.println("Could not load board: " + e.getMessage());
            }
        }

        return new Board(10, 10);
    }


    private static void saveBoard(Board board) {
        try {
            BoardFileWriter.save(board, BOARD_PATH);
            System.out.println("Board saved to " + BOARD_PATH.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save board: " + e.getMessage());
        }
    }

    private static boolean boardAlreadyHasWinner(Board board) {
        Player dummyX = new Player("X", PlayerType.HUMAN, CellState.X);
        Player dummyO = new Player("O", PlayerType.COMPUTER, CellState.O);

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Position pos = new Position(r, c);
                CellState cell = board.getCell(pos);

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