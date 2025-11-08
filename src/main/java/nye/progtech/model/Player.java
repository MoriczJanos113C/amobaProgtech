package nye.progtech.model;

import java.util.Objects;

public final class Player {
    private final String name;
    private final PlayerType type;
    private final CellState stone;

    public Player(String name, PlayerType type, CellState stone) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Player name must not be blank.");
        if (type == null) throw new IllegalArgumentException("Player type must not be null.");
        if (stone == null || stone == CellState.EMPTY) throw new IllegalArgumentException("Player stone must be X or O.");

        this.name = name;
        this.type = type;
        this.stone = stone;
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