import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DB {
    // Ubah sesuai setting kamu (XAMPP biasanya root tanpa password)
    private static final String URL = "jdbc:mysql://localhost:3306/pacman_db?useSSL=false&serverTimezone=Asia/Jakarta";
    private static final String USER = "root";
    private static final String PASS = "";

    public static void saveScore(String playerName, int score) throws SQLException {
        String sql = "INSERT INTO leaderboard(player_name, score) VALUES(?, ?)";
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, playerName);
            ps.setInt(2, score);
            ps.executeUpdate();
        }
    }

    public static List<ScoreEntry> topScores(int limit) throws SQLException {
        String sql = "SELECT player_name, score, played_at FROM leaderboard ORDER BY score DESC, played_at ASC LIMIT ?";
        List<ScoreEntry> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString(1);
                    int score = rs.getInt(2);
                    Timestamp ts = rs.getTimestamp(3);
                    LocalDateTime dt = ts.toLocalDateTime();
                    out.add(new ScoreEntry(name, score, dt));
                }
            }
        }
        return out;
    }
}
