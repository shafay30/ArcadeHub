import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int BIRD_SIZE = 20;
    private static final int PIPE_WIDTH = 60;
    private static final int PIPE_GAP = 150;

    private int birdY = HEIGHT / 2;
    private int birdYVel = 0;
    private int score = 0;
    private boolean gameOver = false;
    private boolean started = false;

    private ArrayList<Rectangle> pipes;
    private Timer timer;
    private JButton restartButton;

    public FlappyBird() {
        timer = new Timer(20, this);
        pipes = new ArrayList<>();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.CYAN);
        setLayout(null);  // Use null layout to position components manually

        // Initialize and add the restart button
        restartButton = new JButton("Restart");
        restartButton.setBounds(WIDTH / 2 - 60, HEIGHT / 2 + 50, 120, 30);
        restartButton.addActionListener(e -> restartGame());
        add(restartButton);
        restartButton.setVisible(false); // Hide button initially

        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();  // Request focus for the panel
        timer.start();
        generatePipes();
    }

    private void generatePipes() {
        int initialX = WIDTH;
        for (int i = 0; i < 4; i++) {
            int height = new Random().nextInt(HEIGHT / 2);
            pipes.add(new Rectangle(initialX + i * 200, 0, PIPE_WIDTH, height));
            pipes.add(new Rectangle(initialX + i * 200, height + PIPE_GAP, PIPE_WIDTH, HEIGHT - height - PIPE_GAP));
        }
    }

    private void restartGame() {
        birdY = HEIGHT / 2;
        birdYVel = 0;
        score = 0;
        gameOver = false;
        started = false;
        pipes.clear();
        generatePipes();
        restartButton.setVisible(false); // Hide the restart button
        timer.start();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw bird
        g.setColor(Color.YELLOW);
        g.fillRect(WIDTH / 4, birdY, BIRD_SIZE, BIRD_SIZE);

        // Draw pipes
        g.setColor(Color.GREEN);
        for (Rectangle pipe : pipes) {
            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
        }

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 10, 25);

        // Game over message
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over", WIDTH / 2 - 100, HEIGHT / 2);
            restartButton.setVisible(true); // Show the restart button
            timer.stop(); // Stop the timer
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (started && !gameOver) {
            // Bird physics
            birdY += birdYVel;
            birdYVel += 1; // Gravity

            // Move pipes and check for collisions
            for (int i = 0; i < pipes.size(); i++) {
                Rectangle pipe = pipes.get(i);
                pipe.x -= 5; // Move pipe left

                // Check collision with bird
                if (pipe.intersects(new Rectangle(WIDTH / 4, birdY, BIRD_SIZE, BIRD_SIZE))) {
                    gameOver = true;
                }

                // Recycle pipes
                if (pipe.x + PIPE_WIDTH < 0) {
                    pipes.remove(pipe);
                    if (pipe.y == 0) {
                        int height = new Random().nextInt(HEIGHT / 2);
                        pipes.add(new Rectangle(WIDTH, 0, PIPE_WIDTH, height));
                        pipes.add(new Rectangle(WIDTH, height + PIPE_GAP, PIPE_WIDTH, HEIGHT - height - PIPE_GAP));
                    }
                }
            }

            // Bird out of bounds
            if (birdY > HEIGHT || birdY < 0) {
                gameOver = true;
            }

            // Increment score
            if (!gameOver && pipes.size() > 0 && pipes.get(0).x + PIPE_WIDTH < WIDTH / 4) {
                score++;
                pipes.remove(0); // Remove top pipe
                pipes.remove(0); // Remove bottom pipe
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!started) {
                started = true;
                birdYVel = -10; // Initial jump
            } else if (!gameOver) {
                birdYVel = -10; // Bird jumps
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        FlappyBird game = new FlappyBird();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
