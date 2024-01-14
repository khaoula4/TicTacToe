
package com.khaoula.tictactoe;

import java.util.UUID;
import java.rmi.RemoteException;

public class GameSession {
    private UUID sessionId;
    private char[][] board;
    private Character currentPlayer;
    private UUID player1;
    private UUID player2;

    public GameSession(UUID player1, UUID player2) {
        this.sessionId = UUID.randomUUID();
        this.board = new char[3][3];
        this.currentPlayer = 'X'; // X starts first
        this.player1 = player1;
        this.player2 = player2; // Second player is now set during initialization
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }

    public synchronized void makeMove(int x, int y, char playerSymbol) throws RemoteException {
        if (board[x][y] == ' ' && playerSymbol == currentPlayer) {
            board[x][y] = playerSymbol;
            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X'; // Switch turn
        }
    }

    public char[][] getBoard() {
        return board;
    }

    public String checkStatus() {
        if (isWinningCondition('X')) {
            return "Player X Wins!";
        } else if (isWinningCondition('O')) {
            return "Player O Wins!";
        } else if (isBoardFull()) {
            return "Draw!";
        } else {
            return "In Progress";
        }
    }

    // Check if a player has won
    private boolean isWinningCondition(char player) {
        // Check rows, columns, and diagonals for a win
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true;
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) return true;
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) return true;
        return false;
    }

    // Check if the board is full
    private boolean isBoardFull() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public synchronized void restartGame() {
        initializeBoard();
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X'; // Optionally switch starting player
    }
    
    // Getter for player1
    public UUID getPlayer1() {
        return player1;
    }

    // Setter for player1
    public void setPlayer1(UUID player1) {
        if (this.player1 == null) {
            this.player1 = player1;
        } else {
            throw new IllegalStateException("Player 1 is already set.");
        }
    }

    // Getter for player2
    public UUID getPlayer2() {
        return player2;
    }

    // Setter for player2
    public void setPlayer2(UUID player2) {
        if (this.player2 == null) {
            this.player2 = player2;
        } else {
            throw new IllegalStateException("Player 2 is already set.");
        }
    }

    // If needed, add setters and getters for player1 and player2
}

