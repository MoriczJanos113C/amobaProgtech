package nye.progtech.io;

import nye.progtech.model.Board;
import nye.progtech.model.CellState;
import nye.progtech.model.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class BoardFileReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void load_throwsWhenFileDoesNotExist() {
        Path file = tempDir.resolve("nonexistent.txt");

        assertThrows(IOException.class,
                () -> BoardFileReader.load(file));
    }

    @Test
    void load_throwsWhenFileIsEmpty() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "   \n   \n"); // only blanks

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> BoardFileReader.load(file)
        );
        assertTrue(ex.getMessage().contains("Board file is empty"));
    }

    @Test
    void load_throwsWhenRowLengthsAreInconsistent() throws IOException {
        Path file = tempDir.resolve("bad_rows.txt");

        String ls = System.lineSeparator();
        String content = String.join(ls,
                "XO...",
                ".....",
                "...",
                ".....",
                "....."
        ) + ls;

        Files.writeString(file, content);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> BoardFileReader.load(file)
        );
        assertTrue(ex.getMessage().contains("Inconsistent row length"));
    }

    @Test
    void load_throwsWhenInvalidCharacterPresent() throws IOException {
        Path file = tempDir.resolve("invalid_char.txt");

        String ls = System.lineSeparator();
        String content = String.join(ls,
                "XO...",
                ".....",
                "..Z..",   // Z is invalid
                ".....",
                "....."
        ) + ls;

        Files.writeString(file, content);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> BoardFileReader.load(file)
        );
        assertTrue(ex.getMessage().contains("Invalid character"));
    }

    @Test
    void load_readsBoardCorrectly() throws IOException {
        Path file = tempDir.resolve("board.txt");

        String ls = System.lineSeparator();
        String content = String.join(ls,
                "XO...",
                ".....",
                "..X..",
                ".....",
                "....O"
        ) + ls;

        Files.writeString(file, content);

        Board board = BoardFileReader.load(file);

        assertEquals(5, board.getRows());
        assertEquals(5, board.getCols());

        assertEquals(CellState.X, board.getCell(new Position(0, 0)));
        assertEquals(CellState.O, board.getCell(new Position(0, 1)));
        assertEquals(CellState.EMPTY, board.getCell(new Position(0, 2)));

        assertEquals(CellState.X, board.getCell(new Position(2, 2)));
        assertEquals(CellState.O, board.getCell(new Position(4, 4)));
        assertEquals(CellState.EMPTY, board.getCell(new Position(3, 3)));
    }

    @Test
    void load_and_save_areCompatibleRoundTrip() throws IOException {
        // create original board
        Board original = new Board(5, 5);
        original.setCell(new Position(0, 0), CellState.X);
        original.setCell(new Position(1, 1), CellState.O);
        original.setCell(new Position(2, 2), CellState.X);
        original.setCell(new Position(3, 3), CellState.O);
        original.setCell(new Position(4, 4), CellState.X);

        Path file = tempDir.resolve("roundtrip.txt");

        // save and then load
        BoardFileWriter.save(original, file);
        Board loaded = BoardFileReader.load(file);

        assertEquals(original.getRows(), loaded.getRows());
        assertEquals(original.getCols(), loaded.getCols());

        for (int r = 0; r < original.getRows(); r++) {
            for (int c = 0; c < original.getCols(); c++) {
                Position pos = new Position(r, c);
                assertEquals(
                        original.getCell(pos),
                        loaded.getCell(pos),
                        "Mismatch at (" + r + ", " + c + ")"
                );
            }
        }
    }
}
