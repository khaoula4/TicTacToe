package com.khaoula.tictactoe;

import java.util.UUID;


import java.rmi.RemoteException;
import java.util.UUID;

public class GameSession {
    private UUID sessionId;
    private char[][] board;
    private Character currentPlayer;
    private UUID player1;
    private UUID player2;

    public GameSession(UUID player1) {
        this.sessionId = UUID.randomUUID();
        this.board = new char[3][3];
        this.currentPlayer = 'X'; // X starts first
        this.player1 = player1;
        this.player2 = null; // Will be set when second player joins
        // Initialize board to empty state
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
        // Check for a win for both 'X' and 'O'
        if (isWinningCondition('X')) {
            return "Player X Wins!";
        } else if (isWinningCondition('O')) {
            return "Player O Wins!";
        } else if (isBoardFull()) {
            // If no one has won and the board is full, it's a draw
            return "Draw!";
        } else {
            // Game is still in progress
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

    public void setPlayer2(UUID player2) {
        this.player2 = player2;
    }


	
}
