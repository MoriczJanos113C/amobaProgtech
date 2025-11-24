package nye.progtech.io;

import nye.progtech.model.Board;
import nye.progtech.model.CellState;
import nye.progtech.model.Position;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Utility class for loading a {@link Board} from a text file.
 */
public final class BoardFileReader {

    /** Private constructor to prevent instantiation. */
    private BoardFileReader() {
        // utility class
    }

    /**
     * Loads a board from the specified file path.
     *
     * @param path the file path to load from
     * @return the loaded {@link Board}
     * @throws IOException              if file reading fails
     * @throws IllegalArgumentException if file content is invalid
     */
    public static Board load(final Path path) throws IOException {
        final List<String> lines = Files.readAllLines(path)
                .stream()
                .filter(line -> !line.isBlank())
                .toList();

        if (lines.isEmpty()) {
            throw new IllegalArgumentException(
                    "Board file is empty: " + path
            );
        }

        final int rows = lines.size();
        final int cols = lines.get(0).length();

        for (String line : lines) {
            if (line.length() != cols) {
                throw new IllegalArgumentException(
                        "Inconsistent row length in board file: "
                                + path
                );
            }
        }

        final Board board = new Board(rows, cols);

        for (int r = 0; r < rows; r++) {
            final String line = lines.get(r);
            for (int c = 0; c < cols; c++) {
                final char ch = line.charAt(c);
                final CellState state = switch (ch) {
                    case 'X' -> CellState.X;
                    case 'O' -> CellState.O;
                    case '.' -> CellState.EMPTY;
                    default -> throw new IllegalArgumentException(
                            "Invalid character '" + ch
                                    + "' at row " + r
                                    + ", col " + c
                                    + " in " + path
                    );
                };
                board.setCell(new Position(r, c), state);
            }
        }

        return board;
    }
}
