// Author: Shafay Chughtai
// Project: ArcadeHub


import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class TicTacToe extends JPanel {
    private final int SIZE = 3;
    private final JButton[][] buttons = new JButton[SIZE][SIZE];
    private boolean isPlayerOne = true;
    private boolean isGameOver = false;
    private String gameMode;

    public TicTacToe(String gameMode) {
        this.gameMode = gameMode;
        setLayout(new GridLayout(SIZE, SIZE));
        initializeButtons();
        setPreferredSize(new Dimension(300, 300));
    }

    private void initializeButtons() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.PLAIN, 60));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                add(buttons[i][j]);
            }
        }
        if (gameMode.equals("1 Player")) {
            // Additional logic for AI player (e.g., add an AI opponent)
        }
    }

    private class ButtonClickListener implements ActionListener {
        private final int row, col;

        ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[row][col].getText().equals("") && !isGameOver) {
                if (isPlayerOne) {
                    buttons[row][col].setText("X");
                } else {
                    buttons[row][col].setText("O");
                }
                if (checkForWinner()) {
                    isGameOver = true;
                    JOptionPane.showMessageDialog(TicTacToe.this, (isPlayerOne ? "Player X" : "Player O") + " wins!");
                } else if (isBoardFull()) {
                    isGameOver = true;
                    JOptionPane.showMessageDialog(TicTacToe.this, "It's a draw!");
                }
                isPlayerOne = !isPlayerOne;
                if (gameMode.equals("1 Player") && !isPlayerOne && !isGameOver) {
                    makeAiMove(); // AI move for 1 Player mode
                }
            }
        }
    }

    private boolean checkForWinner() {
        // Check rows, columns and diagonals
        for (int i = 0; i < SIZE; i++) {
            if (buttons[i][0].getText().equals(buttons[i][1].getText()) && buttons[i][1].getText().equals(buttons[i][2].getText()) && !buttons[i][0].getText().equals("")) {
                return true;
            }
            if (buttons[0][i].getText().equals(buttons[1][i].getText()) && buttons[1][i].getText().equals(buttons[2][i].getText()) && !buttons[0][i].getText().equals("")) {
                return true;
            }
        }
        if (buttons[0][0].getText().equals(buttons[1][1].getText()) && buttons[1][1].getText().equals(buttons[2][2].getText()) && !buttons[0][0].getText().equals("")) {
            return true;
        }
        if (buttons[0][2].getText().equals(buttons[1][1].getText()) && buttons[1][1].getText().equals(buttons[2][0].getText()) && !buttons[0][2].getText().equals("")) {
            return true;
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (buttons[i][j].getText().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void makeAiMove() {
        // Basic AI logic: Randomly select an empty spot
        Random rand = new Random();
        int row, col;
        do {
            row = rand.nextInt(SIZE);
            col = rand.nextInt(SIZE);
        } while (!buttons[row][col].getText().equals(""));
        buttons[row][col].setText("O");
        if (checkForWinner()) {
            isGameOver = true;
            JOptionPane.showMessageDialog(TicTacToe.this, "AI (O) wins!");
        } else if (isBoardFull()) {
            isGameOver = true;
            JOptionPane.showMessageDialog(TicTacToe.this, "It's a draw!");
        }
        isPlayerOne = true;
    }
}
