import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel {

    // Peta ASCII: #=wall, .=pellet, ' '=kosong
    private final String[] LEVEL = new String[]{
            "####################",
            "#........##........#",
            "#.####...##...####.#",
            "#................. #",
            "#.####.######.####.#",
            "#......#....#......#",
            "######.#.##.#.######",
            "#......#....#......#",
            "#.####.######.####.#",
            "#........##........#",
            "####################"
    };

    private char[][] map;
    private int rows, cols;

    private int pacR = 1, pacC = 1;
    private int ghostR = 1, ghostC = 18;

    private int dirR = 0, dirC = 0; // pacman direction
    private int score = 0;
    private int lives = 3;
    private boolean running = true;

    private final Timer timer;
    private final Random rnd = new Random();

    private final Font mono = new Font(Font.MONOSPACED, Font.BOLD, 22);

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);

        loadLevel();

        // Key bindings (WASD + Arrow)
        bindKey("LEFT", 0, -1);
        bindKey("RIGHT", 0, 1);
        bindKey("UP", -1, 0);
        bindKey("DOWN", 1, 0);

        bindKey("A", 0, -1);
        bindKey("D", 0, 1);
        bindKey("W", -1, 0);
        bindKey("S", 1, 0);

        // Game loop
        timer = new Timer(140, e -> tick());
        timer.start();
    }

    private void loadLevel() {
        rows = LEVEL.length;
        cols = LEVEL[0].length();
        map = new char[rows][cols];
        for (int r = 0; r < rows; r++) {
            String line = LEVEL[r];
            // jaga kalau ada spasi di akhir baris (biar panjang tetap cols)
            if (line.length() < cols) {
                line = String.format("%-" + cols + "s", line);
            }
            for (int c = 0; c < cols; c++) {
                char ch = line.charAt(c);
                if (ch != '#' && ch != '.') ch = ' ';
                map[r][c] = ch;
            }
        }
        // posisi awal
        pacR = 1; pacC = 1;
        ghostR = 1; ghostC = cols - 2;
        score = 0;
        lives = 3;
        running = true;
        dirR = 0; dirC = 0;
    }

    private void bindKey(String key, int dr, int dc) {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(key), key);
        am.put(key, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                dirR = dr; dirC = dc;
            }
        });
    }

    private void tick() {
        if (!running) return;

        movePacman();
        moveGhost();

        // check collision
        if (pacR == ghostR && pacC == ghostC) {
            lives--;
            if (lives <= 0) {
                gameOver();
                return;
            }
            // reset positions after hit
            pacR = 1; pacC = 1;
            ghostR = 1; ghostC = cols - 2;
            dirR = 0; dirC = 0;
        }

        // win condition: no pellets
        if (pelletsLeft() == 0) {
            running = false;
            timer.stop();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "YOU WIN! Score: " + score);
                endAndSave();
            });
            return;
        }

        repaint();
    }

    private void movePacman() {
        if (dirR == 0 && dirC == 0) return;
        int nr = pacR + dirR;
        int nc = pacC + dirC;

        // wrap horizontal (optional)
        if (nc < 0) nc = cols - 1;
        if (nc >= cols) nc = 0;

        if (isWall(nr, nc)) return;

        pacR = nr;
        pacC = nc;

        if (map[pacR][pacC] == '.') {
            map[pacR][pacC] = ' ';
            score += 10;
        }
    }

    private void moveGhost() {
        // Ghost random tapi lebih suka mendekat
        int bestDr = 0, bestDc = 0;
        int bestDist = Integer.MAX_VALUE;

        int[] drs = {0, 0, -1, 1};
        int[] dcs = {-1, 1, 0, 0};

        // sedikit randomness biar tidak terlalu pintar
        if (rnd.nextDouble() < 0.25) {
            // random valid move
            for (int tries = 0; tries < 10; tries++) {
                int i = rnd.nextInt(4);
                int nr = ghostR + drs[i];
                int nc = ghostC + dcs[i];
                if (!isWall(nr, nc)) {
                    ghostR = nr;
                    ghostC = nc;
                    return;
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            int nr = ghostR + drs[i];
            int nc = ghostC + dcs[i];
            if (isWall(nr, nc)) continue;

            int dist = Math.abs(pacR - nr) + Math.abs(pacC - nc);
            if (dist < bestDist) {
                bestDist = dist;
                bestDr = drs[i];
                bestDc = dcs[i];
            }
        }

        ghostR += bestDr;
        ghostC += bestDc;
    }

    private boolean isWall(int r, int c) {
        if (r < 0 || r >= rows) return true;
        if (c < 0 || c >= cols) return true;
        return map[r][c] == '#';
    }

    private int pelletsLeft() {
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (map[r][c] == '.') count++;
            }
        }
        return count;
    }

    private void gameOver() {
        running = false;
        timer.stop();
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "GAME OVER! Score: " + score);
            endAndSave();
        });
    }

    private void endAndSave() {
        String name = JOptionPane.showInputDialog(this, "Nama pemain untuk leaderboard:", "Player");
        if (name == null || name.trim().isEmpty()) name = "Player";
        name = name.trim();
        try {
            // pastikan driver kebaca
            Class.forName("com.mysql.cj.jdbc.Driver");
            DB.saveScore(name, score);
            showLeaderboard();
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "MySQL Driver tidak ditemukan.\nTambahkan mysql-connector-j ke classpath.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal simpan leaderboard:\n" + ex.getMessage());
        } finally {
            int opt = JOptionPane.showConfirmDialog(this, "Main lagi?", "Restart", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                loadLevel();
                timer.start();
            } else {
                System.exit(0);
            }
        }
    }

    private void showLeaderboard() {
        try {
            List<ScoreEntry> top = DB.topScores(10);
            StringBuilder sb = new StringBuilder();
            sb.append("LEADERBOARD (Top 10)\n");
            sb.append("--------------------------------------\n");
            int rank = 1;
            for (ScoreEntry e : top) {
                sb.append(String.format("%2d) %s\n", rank++, e.toString()));
            }
            JTextArea area = new JTextArea(sb.toString(), 14, 38);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal ambil leaderboard:\n" + ex.getMessage());
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // ukuran cukup untuk grid
        return new Dimension(700, 420);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(mono);

        int cellW = 28;
        int cellH = 28;
        int startX = 20;
        int startY = 50;

        // HUD
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score + "   Lives: " + lives + "   Pellets: " + pelletsLeft(), 20, 28);

        // Draw map as characters
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char ch = map[r][c];
                int x = startX + c * cellW;
                int y = startY + r * cellH;

                if (ch == '#') {
                    g.setColor(new Color(0, 140, 255));
                    g.drawString("#", x, y);
                } else if (ch == '.') {
                    g.setColor(Color.YELLOW);
                    g.drawString(".", x, y);
                } else {
                    // kosong -> tidak digambar
                }
            }
        }

        // Pacman
        g.setColor(Color.YELLOW);
        g.drawString("C", startX + pacC * cellW, startY + pacR * cellH);

        // Ghost
        g.setColor(Color.PINK);
        g.drawString("G", startX + ghostC * cellW, startY + ghostR * cellH);

        // border hint
        g.setColor(Color.GRAY);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.drawString("Kontrol: Arrow / WASD", 20, getHeight() - 15);
    }
}
