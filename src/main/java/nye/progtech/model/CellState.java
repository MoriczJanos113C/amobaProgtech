package nye.progtech.model;

public enum CellState {
    EMPTY('.'),
    X('X'),
    O('O');

    private final char symbol;

    CellState(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public String toString() {
        return String.valueOf(symbol);
    }
}
