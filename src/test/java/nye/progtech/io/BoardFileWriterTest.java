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

class BoardFileWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void save_throwsWhenBoardIsNull() {
        Path path = tempDir.resolve("board.txt");

        assertThrows(IllegalArgumentException.class,
                () -> BoardFileWriter.save(null, path));
    }

    @Test
    void save_throwsWhenPathIsNull() {
        Board board = new Board(5, 5);

        assertThrows(IllegalArgumentException.class,
                () -> BoardFileWriter.save(board, null));
    }

    @Test
    void save_writesCorrectBoardFormat() throws IOException {
        Board board = new Board(5, 5);


        board.setCell(new Position(0, 0), CellState.X);
        board.setCell(new Position(0, 1), CellState.O);
        board.setCell(new Position(2, 2), CellState.X);
        board.setCell(new Position(4, 4), CellState.O);

        Path file = tempDir.resolve("board.txt");
        BoardFileWriter.save(board, file);

        String content = Files.readString(file);

        String ls = System.lineSeparator();
        String expected = String.join(ls,
                "XO...",
                ".....",
                "..X..",
                ".....",
                "....O"
        ) + ls;

        assertEquals(expected, content);
    }

    @Test
    void save_createsParentDirectoriesIfNeeded() throws IOException {
        Board board = new Board(5, 5);

        Path nestedPath = tempDir
                .resolve("subdir")
                .resolve("nested")
                .resolve("board.txt");

        BoardFileWriter.save(board, nestedPath);

        assertTrue(Files.exists(nestedPath));
    }
}
