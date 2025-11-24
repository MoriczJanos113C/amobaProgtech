package nye.progtech.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and retrieving player statistics using an H2 database.
 */
public final class PlayerStatsRepository {

    /** JDBC database URL. */
    private static final String URL = "jdbc:h2:./amoba_db";

    /** JDBC username. */
    private static final String USER = "sa";

    /** JDBC password. */
    private static final String PASS = "";

    /**
     * Creates a new repository instance and initializes the database schema.
     */
    public PlayerStatsRepository() {
        initSchema();
    }

    /**
     * Initializes the player statistics table if it does not exist.
     */
    private void initSchema() {
        String sql = """
                CREATE TABLE IF NOT EXISTS player_stats (
                    name VARCHAR(100) PRIMARY KEY,
                    wins INT NOT NULL
                )
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to init schema", e);
        }
    }

    /**
     * Creates a new database connection.
     *
     * @return a new JDBC {@link Connection}
     * @throws SQLException if connecting fails
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Increases a player's number of wins.
     * If the player does not exist, it creates an entry with one win.
     *
     * @param playerName the player's name
     */
    public void incrementWin(final String playerName) {
        String sql = """
                MERGE INTO player_stats (name, wins)
                KEY (name)
                VALUES (
                    ?, COALESCE(
                        (SELECT wins FROM player_stats WHERE name = ?),
                        0
                    ) + 1
                )
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, playerName);
            ps.setString(2, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to increment wins", e);
        }
    }

    /**
     * Returns all player statistics ordered by number of wins (descending)
     * and then by name (ascending).
     *
     * @return list of {@link PlayerStat} entries
     */
    public List<PlayerStat> findAllOrderByWinsDesc() {
        String sql =
                "SELECT name, wins FROM player_stats "
                        + "ORDER BY wins DESC, name ASC";

        List<PlayerStat> result = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                int wins = rs.getInt("wins");
                result.add(new PlayerStat(name, wins));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query stats", e);
        }

        return result;
    }
}
