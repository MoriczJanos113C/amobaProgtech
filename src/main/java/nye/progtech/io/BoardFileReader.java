package nye.progtech.io;

import nye.progtech.model.Board;
import nye.progtech.model.CellState;
import nye.progtech.model.Position;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class BoardFileReader {

    private BoardFileReader() {
        // utility class
    }

    public static Board load(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path)
                .stream()
                .filter(line -> !line.isBlank())
                .toList();

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Board file is empty: " + path);
        }

        int rows = lines.size();
        int cols = lines.get(0).length();

        for (String line : lines) {
            if (line.length() != cols) {
                throw new IllegalArgumentException("Inconsistent row length in board file: " + path);
            }
        }

        Board board = new Board(rows, cols);

        for (int r = 0; r < rows; r++) {
            String line = lines.get(r);
            for (int c = 0; c < cols; c++) {
                char ch = line.charAt(c);
                CellState state = switch (ch) {
                    case 'X' -> CellState.X;
                    case 'O' -> CellState.O;
                    case '.' -> CellState.EMPTY;
                    default -> throw new IllegalArgumentException(
                            "Invalid character '%c' at row %d, col %d in %s"
                                    .formatted(ch, r, c, path)
                    );
                };
                board.setCell(new Position(r, c), state);
            }
        }

        return board;
    }
}
