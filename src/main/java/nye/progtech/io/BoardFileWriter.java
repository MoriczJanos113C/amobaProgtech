package nye.progtech.io;

import nye.progtech.model.Board;
import nye.progtech.model.CellState;
import nye.progtech.model.Position;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for saving a Board object to a file.
 */
public final class BoardFileWriter {

    /** Private constructor to prevent instantiation. */
    private BoardFileWriter() {
        // Utility class
    }

    /**
     * Saves the given board to the specified path.
     *
     * @param board the board to save
     * @param path  the file path to save the board to
     * @throws IOException if writing fails
     */
    public static void save(
            final Board board,
            final Path path)
            throws IOException {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
        }

        final StringBuilder sb = new StringBuilder();

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                final CellState state = board.getCell(new Position(r, c));
                final char ch = switch (state) {
                    case X -> 'X';
                    case O -> 'O';
                    case EMPTY -> '.';
                };
                sb.append(ch);
            }
            sb.append(System.lineSeparator());
        }

        // Create directories if needed, then write the file
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        Files.writeString(path, sb.toString());
    }
}
