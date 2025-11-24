package nye.progtech.model;


/** Represents the state of a cell on the board. */
public enum CellState {

    /** Empty cell. */
    EMPTY('.'),

    /** Cell occupied by player X. */
    X('X'),

    /** Cell occupied by player O. */
    O('O');

    /** Symbol representing this cell state. */
    private final char cellSymbol;

    /**
     * Constructs a CellState with the given symbol.
     *
     * @param symbol the character representing this state
     */
    CellState(final char symbol) {
        this.cellSymbol = symbol;
    }

    /** @return the symbol representing this state */
    public char getSymbol() {
        return cellSymbol;
    }

    /** @return true if this state is EMPTY */
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public String toString() {
        return String.valueOf(cellSymbol);
    }
}
