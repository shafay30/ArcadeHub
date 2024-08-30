import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PongGame extends JPanel implements ActionListener, KeyListener {
    private int paddleX = 150;  // Paddle position (X-coordinate)
    private final int paddleY = 270;  // Paddle position (Y-coordinate) - Fixed at the bottom
    private int ballX = 200, ballY = 150;  // Ball position
    private int ballXDirection = 2, ballYDirection = 2;  // Ball movement direction
    private int score = 0;  // Player score
    private boolean gameOver = false;  // Game over flag
    private JButton restartButton;

    // Track key states
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public PongGame() {
        Timer timer = new Timer(5, this);
        timer.start();
        setPreferredSize(new Dimension(400, 300));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        setupUI();
    }

    private void setupUI() {
        setLayout(null);  // Use absolute positioning

        restartButton = new JButton("Restart");
        restartButton.setBounds(150, 130, 100, 30);
        restartButton.addActionListener(e -> restartGame());
        restartButton.setVisible(false);  // Hidden initially

        add(restartButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over", getWidth() / 2 - 80, getHeight() / 2);
            restartButton.setVisible(true);  // Show restart button
            return;
        }

        // Draw paddle and ball
        g.setColor(Color.WHITE);
        g.fillRect(paddleX, paddleY, 100, 10);  // Paddle
        g.fillOval(ballX, ballY, 10, 10);  // Ball

        // Draw the score
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, 30, 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;  // Skip updates if the game is over

        // Move the paddle based on key states
        if (leftPressed) {
            paddleX = Math.max(paddleX - 10, 0);  // Move paddle left
        }
        if (rightPressed) {
            paddleX = Math.min(paddleX + 10, getWidth() - 100);  // Move paddle right
        }

        // Move the ball
        ballX += ballXDirection;
        ballY += ballYDirection;

        // Ball collision with the top wall
        if (ballY <= 0) ballYDirection *= -1;

        // Ball collision with paddles
        if (ballX >= paddleX && ballX <= paddleX + 100 && ballY >= paddleY - 10 && ballY <= paddleY) {
            ballYDirection *= -1;  // Reflect ball
            score++;  // Increase score
        }

        // Ball collision with the sides
        if (ballX <= 0 || ballX >= getWidth() - 10) ballXDirection *= -1;

        // Ball collision with the bottom
        if (ballY >= getHeight()) {
            gameOver = true;  // End the game
        }

        repaint();
    }

    private void restartGame() {
        gameOver = false;
        score = 0;
        ballX = getWidth() / 2 - 5;
        ballY = getHeight() / 2 - 5;
        ballXDirection = (Math.random() < 0.5) ? 2 : -2;
        ballYDirection = 2;  // Always falls down
        restartButton.setVisible(false);  // Hide restart button
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) return;  // Ignore key events if the game is over

        // Update key states
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Update key states
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong Game");
        PongGame pong = new PongGame();
        frame.add(pong);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
