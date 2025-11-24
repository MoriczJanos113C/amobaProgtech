package nye.progtech.model;


import java.util.Objects;

/** Represents a player in the game. */
public final class Player {

    /** The player's name. */
    private final String playerName;

    /** The player's type (human or computer). */
    private final PlayerType playerType;

    /** The player's stone (X or O). */
    private final CellState playerStone;

    /** The player's score. */
    private int score = 0;

    /** The number of consecutive stones needed to win. */
    private static final int WINNING_COUNT = 5;

    /**
     * Creates a new player.
     *
     * @param name the name of the player
     * @param type the type of the player (human or computer)
     * @param stone the stone used by the player (X or O)
     */
    public Player(
            final String name,
            final PlayerType type,
            final CellState stone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Player name mustn't be blank."
            );
        }
        if (type == null) {
            throw new IllegalArgumentException(
                    "Player type mustn't be null."
            );
        }
        if (stone == null || stone == CellState.EMPTY) {
            throw new IllegalArgumentException(
                    "Player stone mustn't be X or O."
            );
        }
        this.playerName = name;
        this.playerType = type;
        this.playerStone = stone;
    }

    /**
     * Checks if the player has won at the given position on the board.
     *
     * @param board the game board
     * @param position the last move position
     * @return true if the player has won
     */
    public boolean hasWon(final Board board, final Position position) {
        CellState stone = getStone();

        return checkDirection(board, position, stone, 1, 0)
                || checkDirection(board, position, stone, 0, 1)
                || checkDirection(board, position, stone, 1, 1)
                || checkDirection(board, position, stone, 1, -1);
    }

    /**
     * Checks a direction for consecutive stones.
     *
     * @param board the board
     * @param position the starting position
     * @param stone the player's stone
     * @param dx row direction
     * @param dy column direction
     * @return true if a winning line is found
     */
    private boolean checkDirection(
            final Board board,
            final Position position,
            final CellState stone,
            final int dx,
            final int dy) {
        int count = 1;

        count += countLine(board, position, stone, dx, dy);
        count += countLine(board, position, stone, -dx, -dy);

        return count >= WINNING_COUNT;
    }

    /**
     * Counts consecutive stones in one direction.
     *
     * @param board the board
     * @param position starting position
     * @param stone player's stone
     * @param dx row increment
     * @param dy column increment
     * @return the number of consecutive stones
     */
    private int countLine(
            final Board board,
            final Position position,
            final CellState stone,
            final int dx,
            final int dy) {
        int row = position.row();
        int col = position.col();
        int count = 0;

        while (true) {
            row += dx;
            col += dy;

            if (row < 0
                    || col < 0
                    || row >= board.getRows()
                    || col >= board.getCols()) {
                break;
            }

            Position pos = new Position(row, col);

            if (board.getCell(pos) != stone) {
                break;
            }

            count++;
        }

        return count;
    }

    /** @return the player's name */
    public String getName() {
        return playerName;
    }

    /** @return the player's type */
    public PlayerType getType() {
        return playerType;
    }

    /** @return the player's stone */
    public CellState getStone() {
        return playerStone;
    }

    /** @return the player's score */
    public int getScore() {
        return score;
    }

    /** Increments the player's score by 1. */
    public void incrementScore() {
        score++;
    }

    /** Resets the player's score to 0. */
    public void resetScore() {
        score = 0;
    }

    /** @return true if the player is human */
    public boolean isHuman() {
        return playerType == PlayerType.HUMAN;
    }

    /** @return true if the player is a computer */
    public boolean isComputer() {
        return playerType == PlayerType.COMPUTER;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Player other)) {
            return false;
        }
        return playerName.equals(other.playerName)
                && playerType == other.playerType
                && playerStone == other.playerStone;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerName, playerType, playerStone);
    }

    @Override
    public String toString() {
        return "Player{name='%s', type=%s, stone=%s}"
                .formatted(playerName, playerType, playerStone);
    }
}
