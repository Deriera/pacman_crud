import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Pac-Man ASCII (Java) + MySQL Leaderboard");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new GamePanel());
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
