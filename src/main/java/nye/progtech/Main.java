package nye.progtech;


import nye.progtech.game.GameEngine;
import nye.progtech.io.BoardFileReader;
import nye.progtech.io.BoardFileWriter;
import nye.progtech.model.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;


public class Main {

    private static final Path DEFAULT_BOARD_PATH = Path.of("board.txt");
    private static final Path SAVE_PATH = Path.of("saved_board.txt");

    public static void main(String[] args) {
        Board board = loadBoard();
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = sc.nextLine().trim();
        if (name.isBlank()) name = "Player";

        Player human = new Player(name, PlayerType.HUMAN, CellState.X);
        Player computer = new Player("Computer", PlayerType.COMPUTER, CellState.O);

        GameEngine game = new GameEngine(board, human, computer);
        game.play();

        // Save final state
        saveBoard(board);
    }

    private static Board loadBoard() {
        if (Files.exists(DEFAULT_BOARD_PATH)) {
            try {
                return BoardFileReader.load(DEFAULT_BOARD_PATH);
            } catch (Exception e) {
                System.err.println("Could not load board: " + e.getMessage());
            }
        }
        return new Board(10, 10);
    }

    private static void saveBoard(Board board) {
        try {
            BoardFileWriter.save(board, SAVE_PATH);
            System.out.println("Board saved to " + SAVE_PATH.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save board: " + e.getMessage());
        }
    }
}

