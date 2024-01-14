package com.khaoula.tictactoe;

import java.rmi.Naming;
import java.util.UUID;
import javax.swing.*;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TicTacToeClient {
    private TicTacToeGame game;
    private UUID sessionId; // Session ID for the game
    private char currentPlayerSymbol = 'X'; // Start with player 'X'
    private JButton[][] buttons = new JButton[3][3]; // Buttons for the board
    private JFrame frame; 
    private JPanel panel; // Panel for accessing in updateBoard

    public TicTacToeClient() {
        try {
            game = (TicTacToeGame) Naming.lookup("//localhost/TicTacToeGame");
            sessionId = game.startNewGame(); // Start a new game session and store session ID
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Tic-Tac-Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);

        panel = new JPanel(new GridLayout(4, 3, 3, 3)); 
        panel.setBackground(Color.GRAY);

        frame.add(panel);

        // Adding buttons for the game board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton button = new JButton();
                button.setBorderPainted(false);
                button.setFocusPainted(false);
                button.addActionListener(new ButtonListener(i, j));
                panel.add(button);
                buttons[i][j] = button;
            }
        }

        // Adding a restart button
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> restartGame());
        panel.add(restartButton);

        frame.setVisible(true);
    }

    private void restartGame() {
        try {
            game.restartGame(sessionId); // Use sessionId
            updateBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ButtonListener implements ActionListener {
        private final int x;
        private final int y;

        public ButtonListener(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                game.makeMove(sessionId, x, y, currentPlayerSymbol); // Use sessionId
                updateBoard();
                checkGameStatus();
                // TODO: Handle turn switching logic
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateBoard() {
        try {
            char[][] board = game.getBoard(sessionId); // Use sessionId
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setText(Character.toString(board[i][j]));
                    buttons[i][j].invalidate();
                    buttons[i][j].validate();
                    buttons[i][j].repaint();

                    if (board[i][j] != ' ') {
                        buttons[i][j].setEnabled(false);
                    } else {
                        buttons[i][j].setEnabled(true);
                    }
                }
            }

            SwingUtilities.invokeLater(() -> {
                panel.revalidate();
                panel.repaint();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkGameStatus() {
        try {
            String status = game.checkStatus(sessionId); // Use sessionId
            if (!"In Progress".equals(status)) {
                JOptionPane.showMessageDialog(null, status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TicTacToeClient();
    }
}

