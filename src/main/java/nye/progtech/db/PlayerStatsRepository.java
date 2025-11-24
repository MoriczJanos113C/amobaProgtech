package nye.progtech.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class PlayerStatsRepository {

    private static final String URL  = "jdbc:h2:./amoba_db";
    private static final String USER = "sa";
    private static final String PASS = "";

    public PlayerStatsRepository() {
        initSchema();
    }

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

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /** Increases a player's number of wins (creates it with 1 if it doesn't exist).*/
    public void incrementWin(String playerName) {
        String sql = """
                MERGE INTO player_stats (name, wins)
                KEY (name)
                VALUES (?, COALESCE((SELECT wins FROM player_stats WHERE name = ?), 0) + 1)
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

    /** High score list sorted by name and number of wins in descending order. */
    public List<PlayerStat> findAllOrderByWinsDesc() {
        String sql = "SELECT name, wins FROM player_stats ORDER BY wins DESC, name ASC";

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