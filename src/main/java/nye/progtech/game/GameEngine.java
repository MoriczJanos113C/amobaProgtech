package nye.progtech.game;

import nye.progtech.model.Board;
import nye.progtech.model.Player;
import nye.progtech.model.Position;

import java.util.*;

public final class GameEngine {

    private final Board board;
    private final Player human;
    private final Player computer;
    private final Random random = new Random();

    public GameEngine(Board board, Player human, Player computer) {
        this.board = Objects.requireNonNull(board);
        this.human = Objects.requireNonNull(human);
        this.computer = Objects.requireNonNull(computer);
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome, " + human.getName() + "! You are '" + human.getStone() + "'.");
        System.out.println("Computer is '" + computer.getStone() + "'.");
        System.out.println("Type e.g. A5 or B3 to make a move, or 'q' to quit.");
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

            Position aiMove = generateRandomMove();
            if (aiMove == null) {
                System.out.println("No more free spaces — game over.");
                break;
            }

            board.setCell(aiMove, computer.getStone());
            System.out.println("Computer moved to " + toNotation(aiMove) + ".");
            System.out.println(board);
        }
    }

    private Position parseMove(String input) {
        if (input.length() < 2) return null;
        char letter = Character.toUpperCase(input.charAt(0));
        if (letter < 'A' || letter > 'Z') return null;
        String numStr = input.substring(1);
        try {
            int row = Integer.parseInt(numStr) - 1;
            int col = letter - 'A';
            return new Position(row, col);
        } catch (NumberFormatException e) {
            return null;
        }
    }

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
        if (empty.isEmpty()) return null;
        return empty.get(random.nextInt(empty.size()));
    }

    private String toNotation(Position pos) {
        char letter = (char) ('A' + pos.col());
        int row = pos.row() + 1;
        return "" + letter + row;
    }
}