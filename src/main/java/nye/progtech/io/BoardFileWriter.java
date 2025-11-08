package nye.progtech.io;

import nye.progtech.model.Board;
import nye.progtech.model.CellState;
import nye.progtech.model.Position;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BoardFileWriter {

    private BoardFileWriter() {}

    public static void save(Board board, Path path) throws IOException {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null.");
        }
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null.");
        }

        StringBuilder sb = new StringBuilder();

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                CellState state = board.getCell(new Position(r, c));
                char ch = switch (state) {
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