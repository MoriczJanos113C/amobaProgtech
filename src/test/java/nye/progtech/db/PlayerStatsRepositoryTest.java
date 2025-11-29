package nye.progtech.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerStatsRepositoryTest {

    private PlayerStatsRepository repo;

    @BeforeEach
    void setUp() throws Exception {

        repo = new PlayerStatsRepository();


        try (Connection conn = DriverManager.getConnection("jdbc:h2:./amoba_db", "sa", "");
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM player_stats");
        }
    }

    @Test
    void incrementWin_insertsNewPlayerWithOneWin() {
        repo.incrementWin("Alice");

        List<PlayerStat> stats = repo.findAllOrderByWinsDesc();

        assertEquals(1, stats.size());
        PlayerStat alice = stats.get(0);
        assertEquals("Alice", alice.name());
        assertEquals(1, alice.wins());
    }

    @Test
    void incrementWin_incrementsExistingPlayerWins() {
        repo.incrementWin("Bob");
        repo.incrementWin("Bob");
        repo.incrementWin("Bob");

        List<PlayerStat> stats = repo.findAllOrderByWinsDesc();

        assertEquals(1, stats.size());
        PlayerStat bob = stats.get(0);
        assertEquals("Bob", bob.name());
        assertEquals(3, bob.wins());
    }

    @Test
    void findAllOrderByWinsDesc_ordersByWinsThenName() {
        // Wins:
        //   Charlie: 3
        //   Bob:     2
        //   Alice:   1
        //   Anna:    1
        repo.incrementWin("Alice");
        repo.incrementWin("Anna");

        repo.incrementWin("Bob");
        repo.incrementWin("Bob");

        repo.incrementWin("Charlie");
        repo.incrementWin("Charlie");
        repo.incrementWin("Charlie");

        List<PlayerStat> stats = repo.findAllOrderByWinsDesc();

        assertEquals(4, stats.size());

        // Order should be:
        // 1: Charlie (3)
        // 2: Bob     (2)
        // 3: Alice   (1)
        // 4: Anna    (1)
        assertEquals("Charlie", stats.get(0).name());
        assertEquals(3, stats.get(0).wins());

        assertEquals("Bob", stats.get(1).name());
        assertEquals(2, stats.get(1).wins());

        assertEquals(1, stats.get(2).wins());
        assertEquals(1, stats.get(3).wins());

        // alphabetical between same wins
        assertEquals("Alice", stats.get(2).name());
        assertEquals("Anna", stats.get(3).name());
    }
}
