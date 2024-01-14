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

            // Initial prompt to the user
            Object[] options = {"Start New Game", "Join Existing Game"};
            int choice = JOptionPane.showOptionDialog(null, 
                "Do you want to start a new game or join an existing one?", 
                "Tic-Tac-Toe Game", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, options, options[0]);

            if (choice == JOptionPane.YES_OPTION) {
                // Start a new game
                sessionId = game.startNewGame();
                JOptionPane.showMessageDialog(null, "Your session ID: " + sessionId.toString() 
                    + "\nShare this ID with another player to join the game.");
            } else {
                // Join an existing game
                String sessionIdString = JOptionPane.showInputDialog("Enter the Session ID:");
                sessionId = UUID.fromString(sessionIdString);
                // Add a method in the server interface to join an existing game if needed
            }

            // Continue setting up the UI
            initializeUI();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
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
                if (currentPlayerSymbol == 'X' || currentPlayerSymbol == 'O') { // Check if it's this client's turn
                    game.makeMove(sessionId, x, y, currentPlayerSymbol);
                    updateBoard();
                    checkGameStatus();
                    currentPlayerSymbol = '\0'; // Reset the symbol to prevent further moves until updated
                }
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
        // Additional code to handle enabling/disabling buttons based on the current player
        handleButtonEnabling();
    }

    private void handleButtonEnabling() {
        boolean isMyTurn = (currentPlayerSymbol == 'X' || currentPlayerSymbol == 'O');
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(isMyTurn && buttons[i][j].getText().equals(" "));
            }
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

