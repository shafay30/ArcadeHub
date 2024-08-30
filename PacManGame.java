// Author: Shafay Chughtai
// Project: ArcadeHub

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class PacManGame extends JPanel implements ActionListener, KeyListener {
    private static final int TILE_SIZE = 30;
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 15;
    private static final int GAME_SPEED = 100;
    private static final int GHOST_SIZE = 30;
    
    private Timer timer;
    private int pacmanX, pacmanY;
    private int directionX, directionY;
    private int score;

    private ArrayList<Point> walls;
    private ArrayList<Point> dots;
    private ArrayList<Point> ghosts;
    private JButton restartButton;

    public PacManGame() {
        setPreferredSize(new Dimension(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setLayout(new BorderLayout()); // Use BorderLayout for button placement
        addKeyListener(this);
        setFocusable(true); // Ensure this component can gain focus

        // Initialize game state and UI components
        restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> restartGame());
        add(restartButton, BorderLayout.SOUTH);

        timer = new Timer(GAME_SPEED, this);
        walls = new ArrayList<>();
        dots = new ArrayList<>();
        ghosts = new ArrayList<>();
        restartGame(); // Initialize game state
        timer.start();
    }

    private void restartGame() {
        pacmanX = 1;
        pacmanY = 1;
        directionX = 0;
        directionY = 0;
        score = 0;

        walls.clear();
        dots.clear();
        ghosts.clear();
        initializeMaze();
        initializeGhosts();
        repaint();
        timer.start();
    }

    private void initializeMaze() {
        // Create walls
        for (int i = 0; i < GRID_WIDTH; i++) {
            walls.add(new Point(i, 0));         // Top wall
            walls.add(new Point(i, GRID_HEIGHT - 1)); // Bottom wall
        }
        for (int i = 0; i < GRID_HEIGHT; i++) {
            walls.add(new Point(0, i));         // Left wall
            walls.add(new Point(GRID_WIDTH - 1, i)); // Right wall
        }

        // Add some internal walls
        walls.add(new Point(5, 5));
        walls.add(new Point(6, 5));
        walls.add(new Point(7, 5));
        walls.add(new Point(8, 5));

        // Initialize dots
        for (int i = 1; i < GRID_WIDTH - 1; i++) {
            for (int j = 1; j < GRID_HEIGHT - 1; j++) {
                if (!walls.contains(new Point(i, j))) {
                    dots.add(new Point(i, j));
                }
            }
        }
    }

    private void initializeGhosts() {
        Random rand = new Random();
        for (int i = 0; i < 2; i++) {
            int x, y;
            do {
                x = rand.nextInt(GRID_WIDTH - 2) + 1;
                y = rand.nextInt(GRID_HEIGHT - 2) + 1;
            } while (walls.contains(new Point(x, y)) || (x == pacmanX && y == pacmanY));
            ghosts.add(new Point(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw walls
        g.setColor(Color.BLUE);
        for (Point wall : walls) {
            g.fillRect(wall.x * TILE_SIZE, wall.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // Draw dots
        g.setColor(Color.WHITE);
        for (Point dot : dots) {
            g.fillOval(dot.x * TILE_SIZE + TILE_SIZE / 3, dot.y * TILE_SIZE + TILE_SIZE / 3, TILE_SIZE / 3, TILE_SIZE / 3);
        }

        // Draw Pac-Man
        g.setColor(Color.YELLOW);
        int arcStart = 30;
        int arcAngle = 300;
        if (directionX == 0 && directionY == -1) { // Up
            arcStart = 90;
        } else if (directionX == 0 && directionY == 1) { // Down
            arcStart = 270;
        } else if (directionX == -1 && directionY == 0) { // Left
            arcStart = 180;
        } else if (directionX == 1 && directionY == 0) { // Right
            arcStart = 0;
        }
        g.fillArc(pacmanX * TILE_SIZE, pacmanY * TILE_SIZE, TILE_SIZE, TILE_SIZE, arcStart, arcAngle);

        // Draw ghosts
        g.setColor(Color.RED);
        for (Point ghost : ghosts) {
            g.fillOval(ghost.x * TILE_SIZE, ghost.y * TILE_SIZE, GHOST_SIZE, GHOST_SIZE);
        }

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 10, 20);

        // Game over message
        if (isGameOver()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over", GRID_WIDTH * TILE_SIZE / 2 - 100, GRID_HEIGHT * TILE_SIZE / 2);
            timer.stop();
        }
    }

    private void movePacMan() {
        int nextX = pacmanX + directionX;
        int nextY = pacmanY + directionY;

        // Check if the next move is valid (not a wall)
        if (!walls.contains(new Point(nextX, nextY))) {
            pacmanX = nextX;
            pacmanY = nextY;
        }
    }

    private void moveGhosts() {
        for (int i = 0; i < ghosts.size(); i++) {
            Point ghost = ghosts.get(i);
            int dx = 0, dy = 0;

            // Move ghosts towards Pac-Man, but only in cardinal directions
            if (pacmanX > ghost.x) {
                dx = 1; // Move right
            } else if (pacmanX < ghost.x) {
                dx = -1; // Move left
            } else if (pacmanY > ghost.y) {
                dy = 1; // Move down
            } else if (pacmanY < ghost.y) {
                dy = -1; // Move up
            }

            // Calculate the new position for the ghost
            Point newPos = new Point(ghost.x + dx, ghost.y + dy);
            if (!walls.contains(newPos)) {
                ghosts.set(i, newPos);
            }
        }
    }

    private void checkDotCollision() {
        Point pacmanPosition = new Point(pacmanX, pacmanY);

        // Check if Pac-Man is on a dot
        if (dots.contains(pacmanPosition)) {
            dots.remove(pacmanPosition);
            score++;
        }

        // Game over condition (all dots eaten)
        if (dots.isEmpty()) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "You Win! Final Score: " + score);
            System.exit(0);
        }
    }

    private boolean isGameOver() {
        Point pacmanPosition = new Point(pacmanX, pacmanY);
        return ghosts.contains(pacmanPosition);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        movePacMan();
        moveGhosts();
        checkDotCollision();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> { directionX = 0; directionY = -1; }
            case KeyEvent.VK_DOWN -> { directionX = 0; directionY = 1; }
            case KeyEvent.VK_LEFT -> { directionX = -1; directionY = 0; }
            case KeyEvent.VK_RIGHT -> { directionX = 1; directionY = 0; }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pac-Man Game");
        PacManGame game = new PacManGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
