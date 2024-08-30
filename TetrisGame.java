// Author: Shafay Chughtai
// Project: ArcadeHub

// STILL NOT WORKING!

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class TetrisGame extends JPanel implements ActionListener {
    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 20;
    private static final int TILE_SIZE = 30;

    private Timer gameTimer;
    private int[][] grid;
    private Tetromino currentPiece;
    private boolean isGameOver = false;
    private boolean isPieceFalling = true; // New flag to control automatic dropping

    private final Color[] COLORS = {
        Color.BLACK, Color.CYAN, Color.BLUE, Color.ORANGE, Color.YELLOW, 
        Color.GREEN, Color.MAGENTA, Color.RED
    };

    public TetrisGame() {
        grid = new int[GRID_HEIGHT][GRID_WIDTH];
        gameTimer = new Timer(500, this);
        setPreferredSize(new Dimension(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isGameOver) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT -> movePiece(-1);
                        case KeyEvent.VK_RIGHT -> movePiece(1);
                        case KeyEvent.VK_DOWN -> {
                            isPieceFalling = false; // Allow the piece to be controlled by the player
                            dropPiece();
                        }
                        case KeyEvent.VK_UP -> rotatePiece();
                    }
                }
            }
        });
        startNewGame();
    }

    private void startNewGame() {
        isGameOver = false;
        grid = new int[GRID_HEIGHT][GRID_WIDTH]; // Clear the grid
        currentPiece = new Tetromino();
        isPieceFalling = true; // Start with automatic falling
        gameTimer.start();
    }

    private void movePiece(int dx) {
        if (!isGameOver && !collides(currentPiece.x + dx, currentPiece.y, currentPiece.shape)) {
            currentPiece.x += dx;
            repaint();
        }
    }

    private void dropPiece() {
        if (!isGameOver) {
            if (!collides(currentPiece.x, currentPiece.y + 1, currentPiece.shape)) {
                currentPiece.y += 1;
            } else {
                lockPiece();
                clearLines();
                currentPiece = new Tetromino();
                if (collides(currentPiece.x, currentPiece.y, currentPiece.shape)) {
                    isGameOver = true;
                    gameTimer.stop();
                }
                isPieceFalling = true; // Reset the flag for automatic falling
            }
            repaint();
        }
    }

    private void rotatePiece() {
        if (!isGameOver) {
            int[][] rotatedShape = rotate(currentPiece.shape);
            if (!collides(currentPiece.x, currentPiece.y, rotatedShape)) {
                currentPiece.shape = rotatedShape;
                repaint();
            }
        }
    }

    private boolean collides(int x, int y, int[][] shape) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int newX = x + col;
                    int newY = y + row;
                    if (newX < 0 || newX >= GRID_WIDTH || newY >= GRID_HEIGHT || (newY >= 0 && grid[newY][newX] != 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void lockPiece() {
        for (int row = 0; row < currentPiece.shape.length; row++) {
            for (int col = 0; col < currentPiece.shape[row].length; col++) {
                if (currentPiece.shape[row][col] != 0) {
                    grid[currentPiece.y + row][currentPiece.x + col] = currentPiece.shape[row][col];
                }
            }
        }
    }

    private void clearLines() {
        for (int row = GRID_HEIGHT - 1; row >= 0; row--) {
            boolean isFull = true;
            for (int col = 0; col < GRID_WIDTH; col++) {
                if (grid[row][col] == 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                for (int newRow = row; newRow > 0; newRow--) {
                    System.arraycopy(grid[newRow - 1], 0, grid[newRow], 0, GRID_WIDTH);
                }
                for (int col = 0; col < GRID_WIDTH; col++) {
                    grid[0][col] = 0;
                }
                row++; // Recheck the current row
            }
        }
    }

    private int[][] rotate(int[][] shape) {
        int size = shape.length;
        int[][] newShape = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                newShape[col][size - row - 1] = shape[row][col];
            }
        }
        return newShape;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isPieceFalling) {
            dropPiece();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                if (grid[row][col] != 0) {
                    g.setColor(COLORS[grid[row][col]]);
                    g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        if (currentPiece != null) {
            for (int row = 0; row < currentPiece.shape.length; row++) {
                for (int col = 0; col < currentPiece.shape[row].length; col++) {
                    if (currentPiece.shape[row][col] != 0) {
                        g.setColor(COLORS[currentPiece.shape[row][col]]);
                        g.fillRect((currentPiece.x + col) * TILE_SIZE, (currentPiece.y + row) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }
        if (isGameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over", getWidth() / 2 - 80, getHeight() / 2);
        }
    }

    private class Tetromino {
        int[][] shape;
        int x = GRID_WIDTH / 2 - 2;
        int y = 0;

        public Tetromino() {
            shape = generateRandomPiece();
        }

        private int[][] generateRandomPiece() {
            Random rand = new Random();
            switch (rand.nextInt(7)) {
                case 0 -> {
                    return new int[][] { {1, 1, 1, 1} };           // I
                }
                case 1 -> {
                    return new int[][] { {2, 2}, {2, 2} };         // O
                }
                case 2 -> {
                    return new int[][] { {0, 3, 0}, {3, 3, 3} };   // T
                }
                case 3 -> {
                    return new int[][] { {4, 4, 0}, {0, 4, 4} };   // Z
                }
                case 4 -> {
                    return new int[][] { {0, 5, 5}, {5, 5, 0} };   // S
                }
                case 5 -> {
                    return new int[][] { {6, 6, 6}, {0, 0, 6} };   // L
                }
                case 6 -> {
                    return new int[][] { {7, 7, 7}, {7, 0, 0} };   // J
                }
                default -> throw new IllegalStateException("Unexpected value: " + rand.nextInt(7));
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris Game");
        TetrisGame game = new TetrisGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
