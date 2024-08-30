import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class BrickBreaker extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 80;
    private static final int PADDLE_HEIGHT = 10;
    private static final int BALL_SIZE = 10;
    private static final int BRICK_ROWS = 3;
    private static final int BRICK_COLS = 7;
    private static final int BRICK_WIDTH = 60;
    private static final int BRICK_HEIGHT = 20;

    private Timer timer;
    private int paddleX;
    private int ballX;
    private int ballY;
    private int ballXDir;
    private int ballYDir;
    private boolean[][] bricks;
    private JButton restartButton;
    private JButton backButton;
    private boolean gameOver = false;
    private Set<Integer> pressedKeys = new HashSet<>();

    public BrickBreaker() {
        // Initialize game state
        paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballXDir = -2;
        ballYDir = -3;

        timer = new Timer(10, this);
        bricks = new boolean[BRICK_ROWS][BRICK_COLS];
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        initBricks();
        setupUI();
        timer.start();
    }

    private void setupUI() {
        setLayout(null);  // Use absolute positioning

        restartButton = new JButton("Restart");
        restartButton.setBounds(WIDTH / 2 - 50, HEIGHT / 2 - 15, 100, 30);
        restartButton.addActionListener(e -> restartGame());
        restartButton.setVisible(false);  // Hidden initially

        add(restartButton);

        backButton = new JButton("Back to Arcade");
        backButton.setBounds(WIDTH - 150, HEIGHT - 50, 140, 30); // Position in the bottom-right corner
        backButton.addActionListener(e -> backToArcade());
        add(backButton);
    }

    private void initBricks() {
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICK_COLS; j++) {
                bricks[i][j] = true; // Brick is present
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw paddle
        g.setColor(Color.GREEN);
        g.fillRect(paddleX, HEIGHT - 50, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw ball
        g.setColor(Color.YELLOW);
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);

        // Draw bricks
        g.setColor(Color.RED);
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICK_COLS; j++) {
                if (bricks[i][j]) {
                    int brickX = j * BRICK_WIDTH + 30;
                    int brickY = i * BRICK_HEIGHT + 30;
                    g.fillRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
                }
            }
        }

        // Game over or win messages
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over", WIDTH / 2 - 100, HEIGHT / 2);
            restartButton.setVisible(true);  // Show restart button
            backButton.setVisible(true);     // Show back button
            timer.stop();
            return;
        }

        if (checkWin()) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("You Win!", WIDTH / 2 - 80, HEIGHT / 2);
            restartButton.setVisible(true);  // Show restart button
            backButton.setVisible(true);     // Show back button
            timer.stop();
        }
    }

    private boolean checkWin() {
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICK_COLS; j++) {
                if (bricks[i][j]) {
                    return false; // Some bricks are still present
                }
            }
        }
        return true; // No bricks left, player wins
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;  // Skip updates if the game is over

        // Ball movement
        ballX += ballXDir;
        ballY += ballYDir;

        // Ball collision with walls
        if (ballX < 0 || ballX > WIDTH - BALL_SIZE) ballXDir *= -1;
        if (ballY < 0) ballYDir *= -1;

        // Ball collision with paddle
        if (ballY >= HEIGHT - 50 && ballX >= paddleX && ballX <= paddleX + PADDLE_WIDTH) {
            ballYDir *= -1;
        }

        // Ball collision with bricks
        for (int i = 0; i < BRICK_ROWS; i++) {
            for (int j = 0; j < BRICK_COLS; j++) {
                if (bricks[i][j]) {
                    int brickX = j * BRICK_WIDTH + 30;
                    int brickY = i * BRICK_HEIGHT + 30;

                    if (ballX >= brickX && ballX <= brickX + BRICK_WIDTH && ballY >= brickY && ballY <= brickY + BRICK_HEIGHT) {
                        bricks[i][j] = false;
                        ballYDir *= -1; // Reverse ball direction on hit
                    }
                }
            }
        }

        // Check for game over condition
        if (ballY > HEIGHT) {
            gameOver = true;  // End the game
        }

        // Handle continuous paddle movement
        if (pressedKeys.contains(KeyEvent.VK_LEFT) && paddleX > 0) {
            paddleX -= 5;
        }
        if (pressedKeys.contains(KeyEvent.VK_RIGHT) && paddleX < WIDTH - PADDLE_WIDTH) {
            paddleX += 5;
        }

        repaint();
    }

    private void restartGame() {
        gameOver = false;
        paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
        ballX = WIDTH / 2 - BALL_SIZE / 2;
        ballY = HEIGHT / 2 - BALL_SIZE / 2;
        ballXDir = -2;
        ballYDir = -3;
        initBricks();  // Reset bricks
        restartButton.setVisible(false);  // Hide restart button
        backButton.setVisible(false);     // Hide back button
        timer.start();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Add key to the set of pressed keys
        pressedKeys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Remove key from the set of pressed keys
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    private void backToArcade() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            JPanel cardPanel = (JPanel) topFrame.getContentPane();
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, "ArcadeMenu"); // Ensure "ArcadeMenu" matches your actual CardLayout panel name
        }
    }
}
