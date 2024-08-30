import java.awt.*;
import javax.swing.*;

public class ArcadeHub extends JFrame {
    private static final String IMAGE_PATH = "ADDFILEPATHHERE";

    public ArcadeHub() {
        setTitle("Welcome to ArcadeHub!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new CardLayout());

        JPanel mainMenu = createMainMenu();
        add(mainMenu, "MainMenu");

        FlappyBird flappyBird = new FlappyBird();
        add(flappyBird, "FlappyBird");

        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "MainMenu");

        setVisible(true);
    }

    private JPanel createMainMenu() {
        JPanel mainMenu = new JPanel(new GridLayout(3, 3)); // Adjust grid size as needed

        // Create buttons for each game
        JButton snakeButton = createGameButton("Snake", IMAGE_PATH + "snakesurvival.png", new SnakeSurvival());
        JButton pongButton = createGameButton("Pong", IMAGE_PATH + "ponggame.jpg", new PongGame());
        JButton brickBreakerButton = createGameButton("Brick Breaker", IMAGE_PATH + "brickbreaker.jpg", new BrickBreaker());
        JButton flappyBirdButton = createGameButton("Flappy Bird", IMAGE_PATH + "flappybird.png", new FlappyBird());
        JButton pacmanButton = createGameButton("Pac-Man", IMAGE_PATH + "pacman.png", new PacManGame());
        JButton ticTacToeButton = new JButton("Tic-Tac-Toe", new ImageIcon(IMAGE_PATH + "tictactoe.png"));

        // Action listener for Tic-Tac-Toe button
        ticTacToeButton.addActionListener(e -> switchToGameModeSelection());

        // Add buttons to main menu
        mainMenu.add(snakeButton);
        mainMenu.add(pongButton);
        mainMenu.add(brickBreakerButton);
        mainMenu.add(flappyBirdButton);
        mainMenu.add(pacmanButton);
        mainMenu.add(ticTacToeButton);

        return mainMenu;
    }
    private JButton createGameButton(String text, String iconPath, JPanel gamePanel) {
        ImageIcon icon = new ImageIcon(iconPath);
        if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.err.println("Error loading image: " + iconPath);
        }
    
        // Determine the button size (match to your image size or desired size)
        int buttonWidth = 400; // Adjust to fit your image dimensions
        int buttonHeight = 200; // Adjust to fit your image dimensions
    
        // Resize the icon to fit the button
        Image img = icon.getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
    
        JButton button = new JButton(text, icon);
        button.setPreferredSize(new Dimension(buttonWidth, buttonHeight)); // Set button size
        button.addActionListener(e -> switchToGame(gamePanel));
        return button;
    }
    
    

    private void switchToGame(JPanel gamePanel) {
        getContentPane().removeAll();
        gamePanel.setPreferredSize(new Dimension(800, 600)); // Ensure consistent dimensions
        add(gamePanel);
        add(createBackButton(), BorderLayout.SOUTH); // Add back button at the bottom
        revalidate();
        repaint();
        gamePanel.requestFocusInWindow(); // Ensure game panel is focused
    }

    private JButton createBackButton() {
        JButton backButton = new JButton("Back to Arcade");
        backButton.addActionListener(e -> switchToMainMenu());
        return backButton;
    }

    private void switchToMainMenu() {
        getContentPane().removeAll();
        add(createMainMenu());
        revalidate();
        repaint();
    }

    private void switchToGameModeSelection() {
        int response = JOptionPane.showOptionDialog(this, "Choose Game Mode:", "Tic-Tac-Toe",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new Object[]{"1 Player", "2 Player"}, "1 Player");

        String gameMode = response == 0 ? "1 Player" : "2 Player";
        JPanel ticTacToePanel = new TicTacToe(gameMode); // Pass the selected game mode to TicTacToe
        switchToGame(ticTacToePanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ArcadeHub::new);
    }
}
