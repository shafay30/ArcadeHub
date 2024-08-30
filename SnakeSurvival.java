// Author: Shafay Chughtai
// Project: ArcadeHub


import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeSurvival extends JPanel implements ActionListener {
    private static final int GRID_SIZE = 20;
    private static final int TILE_SIZE = 30;
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final int PANEL_SIZE = GRID_SIZE * TILE_SIZE;

    private final ArrayList<Point> snake = new ArrayList<>();
    private Point food;
    private int direction = KeyEvent.VK_RIGHT;
    private boolean gameOver = false;
    private boolean gameStarted = false;

    private final Timer gameTimer = new Timer(100, this);
    private final Timer countdownTimer = new Timer(1000, null);
    private int countdown = 3;

    private final JButton startButton = new JButton("Start");
    private final JButton restartButton = new JButton("Restart");
    private final JButton backButton = new JButton("Back to Arcade");

    public SnakeSurvival() {
        setLayout(null); // Use null layout to manually position components
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();

        // Initialize buttons and add action listeners
        startButton.setBounds(50, 500, 100, 30);
        startButton.addActionListener(e -> startGame());
        add(startButton);

        restartButton.setBounds(200, 500, 100, 30);
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> restartGame());
        add(restartButton);

        backButton.setBounds(350, 500, 150, 30);
        backButton.addActionListener(e -> backToArcade());
        add(backButton);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int newDirection = e.getKeyCode();
                if (gameStarted && !gameOver &&
                    (newDirection == KeyEvent.VK_LEFT && direction != KeyEvent.VK_RIGHT) ||
                    (newDirection == KeyEvent.VK_RIGHT && direction != KeyEvent.VK_LEFT) ||
                    (newDirection == KeyEvent.VK_UP && direction != KeyEvent.VK_DOWN) ||
                    (newDirection == KeyEvent.VK_DOWN && direction != KeyEvent.VK_UP)) {
                    direction = newDirection;
                }
            }
        });

        countdownTimer.addActionListener(e -> {
            if (countdown > 0) {
                startButton.setText("Starting in " + countdown);
                countdown--;
            } else {
                countdownTimer.stop();
                startButton.setEnabled(false);
                gameStarted = true;
                gameTimer.start();
                requestFocusInWindow();
            }
        });
    }

    private void startGame() {
        if (!gameStarted && !gameOver) {
            initGame();
            countdown = 3;
            countdownTimer.start();
        }
    }

    private void restartGame() {
        gameStarted = false;
        gameOver = false;
        startButton.setEnabled(true);
        startButton.setText("Start");
        gameTimer.stop();
        countdownTimer.stop();
        repaint();
    }

    private void initGame() {
        snake.clear();
        snake.add(new Point(GRID_SIZE / 2, GRID_SIZE / 2));
        spawnFood();
        gameStarted = false;
        gameOver = false;
    }

    private void spawnFood() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(GRID_SIZE);
            y = rand.nextInt(GRID_SIZE);
        } while (snake.contains(new Point(x, y)));
        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gameStarted) {
            g.setColor(Color.WHITE);
            g.drawString("Press 'Start' to Begin", PANEL_WIDTH / 2 - 70, PANEL_HEIGHT / 2);
        } else if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over", PANEL_WIDTH / 2 - 30, PANEL_HEIGHT / 2);
            restartButton.setEnabled(true);
        } else {
            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }

            g.setColor(Color.RED);
            g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameStarted || gameOver) return;

        Point head = new Point(snake.get(0));
        switch (direction) {
            case KeyEvent.VK_LEFT -> head.x--;
            case KeyEvent.VK_RIGHT -> head.x++;
            case KeyEvent.VK_UP -> head.y--;
            case KeyEvent.VK_DOWN -> head.y++;
        }

        if (head.equals(food)) {
            snake.add(0, food);
            spawnFood();
        } else {
            snake.add(0, head);
            snake.remove(snake.size() - 1);
        }

        if (head.x < 0 || head.x >= GRID_SIZE || head.y < 0 || head.y >= GRID_SIZE || snake.subList(1, snake.size()).contains(head)) {
            gameOver = true;
        }

        repaint();
    }

    private void backToArcade() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            CardLayout layout = (CardLayout) topFrame.getContentPane().getLayout();
            layout.show(topFrame.getContentPane(), "MainMenu");
        }
    }
}
