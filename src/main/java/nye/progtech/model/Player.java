package nye.progtech.model;

import java.util.Objects;

public final class Player {
    private final String name;
    private final PlayerType type;
    private final CellState stone;

    private int score = 0;

    public Player(String name, PlayerType type, CellState stone) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Player name must not be blank.");
        if (type == null) throw new IllegalArgumentException("Player type must not be null.");
        if (stone == null || stone == CellState.EMPTY) throw new IllegalArgumentException("Player stone must be X or O.");

        this.name = name;
        this.type = type;
        this.stone = stone;
    }

    public boolean hasWon(Board board, Position position) {
        CellState stone = getStone();

        return checkDirection(board, position, stone, 1, 0 )
                || checkDirection(board, position, stone, 0, 1 )
                || checkDirection(board, position, stone, 1, 1 )
                || checkDirection(board, position, stone, 1, -1 );
    }

    private boolean checkDirection(Board board, Position position, CellState stone, int dx, int dy) {
        int count = 1;

        count += countLine(board, position, stone, dx, dy);
        count += countLine(board, position, stone, -dx, -dy);

        return count >= 5;
    }

    private int countLine(Board board, Position position, CellState stone, int dx, int dy) {
        int row = position.row();
        int col = position.col();
        int count = 0;

        while (true) {
            row += dx;
            col += dy;

            if (row < 0 || col < 0 || row >= board.getRows() || col >= board.getCols()) {
                break;
            }

            Position pos = new Position(row, col);

            if (board.getCell(pos) != stone) break;

            count++;
        }

        return count;
    }

    public String getName() {
        return name;
    }

    public PlayerType getType() {
        return type;
    }

    public CellState getStone() {
        return stone;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        score++;
    }

    public void resetScore() {
        score = 0;
    }

    public boolean isHuman() {
        return type == PlayerType.HUMAN;
    }

    public boolean isComputer() {
        return type == PlayerType.COMPUTER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player other)) return false;
        return name.equals(other.name)
                && type == other.type
                && stone == other.stone;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, stone);
    }

    @Override
    public String toString() {
        return "Player{name='%s', type=%s, stone=%s}"
                .formatted(name, type, stone);
    }
}