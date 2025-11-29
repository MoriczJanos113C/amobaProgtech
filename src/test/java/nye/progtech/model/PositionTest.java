package nye.progtech.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void constructor_acceptsValidCoordinates() {
        Position pos = new Position(3, 4);

        assertEquals(3, pos.row());
        assertEquals(4, pos.col());
    }

    @Test
    void constructor_rejectsNegativeRow() {
        assertThrows(IllegalArgumentException.class,
                () -> new Position(-1, 0));
    }

    @Test
    void constructor_rejectsNegativeCol() {
        assertThrows(IllegalArgumentException.class,
                () -> new Position(0, -1));
    }

    @Test
    void equals_returnsTrueForSameRowAndCol() {
        Position p1 = new Position(2, 5);
        Position p2 = new Position(2, 5);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void equals_returnsFalseForDifferentRow() {
        Position p1 = new Position(2, 5);
        Position p2 = new Position(3, 5);

        assertNotEquals(p1, p2);
    }

    @Test
    void equals_returnsFalseForDifferentCol() {
        Position p1 = new Position(2, 5);
        Position p2 = new Position(2, 6);

        assertNotEquals(p1, p2);
    }

    @Test
    void equals_returnsFalseForDifferentType() {
        Position p1 = new Position(1, 1);

        assertNotEquals(p1, "not a position");
    }

    @Test
    void toString_hasCorrectFormat() {
        Position pos = new Position(3, 7);

        assertEquals("(3, 7)", pos.toString());
    }
}
