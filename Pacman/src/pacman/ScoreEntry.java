import java.time.LocalDateTime;

public class ScoreEntry {
    public final String name;
    public final int score;
    public final LocalDateTime playedAt;

    public ScoreEntry(String name, int score, LocalDateTime playedAt) {
        this.name = name;
        this.score = score;
        this.playedAt = playedAt;
    }

    @Override
    public String toString() {
        return String.format("%-12s  %6d  %s", name, score, playedAt.toString().replace('T', ' '));
    }
}
