package nye.progtech.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

import nye.progtech.db.PlayerStatsRepository;
import nye.progtech.model.Board;
import nye.progtech.model.Player;
import nye.progtech.model.Position;

/**
 * Handles the logic of a human vs. computer game.
 */
public final class GameEngine {

    /** The game board. */
    private final Board board;

    /** The human player. */
    private final Player human;

    /** The computer player. */
    private final Player computer;

    /** Random generator for AI moves. */
    private final Random random = new Random();

    /** Repository for storing player statistics. */
    private final PlayerStatsRepository statsRepo;

    /**
     * Creates a complete game engine instance.
     *
     * @param boardParam    the game board
     * @param humanParam    the human player
     * @param computerParam the computer player
     * @param statsParam    statistics repository
     */
    public GameEngine(
            final Board boardParam,
            final Player humanParam,
            final Player computerParam,
            final PlayerStatsRepository statsParam) {

        this.board = Objects.requireNonNull(boardParam);
        this.human = Objects.requireNonNull(humanParam);
        this.computer = Objects.requireNonNull(computerParam);
        this.statsRepo = Objects.requireNonNull(statsParam);
    }

    /**
     * Starts the gameplay loop for human vs. computer.
     */
    public void play() {
        Scanner scanner = new Scanner(System.in);

        System.out.println(
                "Welcome, " + human.getName()
                        + "! You are '" + human.getStone() + "'."
        );

        System.out.println(
                "Computer is '" + computer.getStone() + "'."
        );

        System.out.println(
                "Type e.g. A5 or B3 to make a move, or 'q' to quit."
        );

        System.out.println();
        System.out.println(board);

        while (true) {

            System.out.print("Your move: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) {
                System.out.println("Game exited.");
                break;
            }

            Position move = parseMove(input);

            if (move == null) {
                System.out.println("Invalid format. Use e.g. A5.");
                continue;
            }

            if (!board.isInside(move)) {
                System.out.println("Out of bounds. Try again.");
                continue;
            }

            if (!board.getCell(move).isEmpty()) {
                System.out.println("Cell already occupied. Try again.");
                continue;
            }

            board.setCell(move, human.getStone());
            System.out.println("You placed at " + input.toUpperCase() + ".");
            System.out.println(board);

            if (human.hasWon(board, move)) {
                System.out.println("You won!");
                statsRepo.incrementWin(human.getName());
                break;
            }

            Position aiMove = generateRandomMove();

            if (aiMove == null) {
                System.out.println("No more free spaces — game over.");
                break;
            }

            board.setCell(aiMove, computer.getStone());
            System.out.println(
                    "Computer moved to " + toNotation(aiMove) + "."
            );
            System.out.println(board);

            if (computer.hasWon(board, aiMove)) {
                System.out.println("Computer wins!");
                statsRepo.incrementWin(computer.getName());
                break;
            }
        }
    }

    /**
     * Converts user input (e.g. "A5") into a board position.
     *
     * @param input the raw user input
     * @return a valid {@link Position}, or {@code null} if parsing failed
     */
    private Position parseMove(final String input) {

        if (input.length() < 2) {
            return null;
        }

        char letter = Character.toUpperCase(input.charAt(0));

        if (letter < 'A' || letter > 'Z') {
            return null;
        }

        String numStr = input.substring(1);

        try {
            int row = Integer.parseInt(numStr) - 1;
            int col = letter - 'A';
            return new Position(row, col);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Generates a random legal AI move.
     *
     * @return a free board position or {@code null} if board is full
     */
    private Position generateRandomMove() {

        List<Position> empty = new ArrayList<>();

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {

                Position pos = new Position(r, c);

                if (board.getCell(pos).isEmpty()) {
                    empty.add(pos);
                }
            }
        }

        if (empty.isEmpty()) {
            return null;
        }

        int index = random.nextInt(empty.size());
        return empty.get(index);
    }

    /**
     * Converts a {@link Position} into board notation (e.g. A3, D10).
     *
     * @param pos the position to convert
     * @return position in user-friendly notation
     */
    private String toNotation(final Position pos) {
        char letter = (char) ('A' + pos.col());
        int row = pos.row() + 1;
        return "" + letter + row;
    }
}
