package com.khaoula.tictactoe;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;



public class TicTacToeServer extends UnicastRemoteObject implements TicTacToeGame {

    private ConcurrentHashMap<UUID, char[][]> gameBoards = new ConcurrentHashMap<>();
    private ConcurrentHashMap<UUID, Character> currentPlayers = new ConcurrentHashMap<>();
    private Map<UUID, GameSession> sessions = new ConcurrentHashMap<>();
    private UUID waitingPlayer = null; // Stores the ID of a player waiting for an opponent

    protected TicTacToeServer() throws RemoteException {
        super();
    }

    @Override
    public UUID startNewGame() throws RemoteException {
        UUID playerId = UUID.randomUUID();
        if (waitingPlayer == null) {
            // No waiting player, create new session
            waitingPlayer = playerId;
            return playerId;
        } else {
            // Pair with waiting player
            GameSession session = new GameSession(waitingPlayer);
            sessions.put(session.getSessionId(), session);
            UUID opponentId = waitingPlayer;
            waitingPlayer = null;
            return opponentId;
        }
    }
 

    @Override
    public synchronized void makeMove(UUID gameId, int x, int y, char playerSymbol) throws RemoteException {
        char[][] board = gameBoards.get(gameId);
        if (board[x][y] == ' ' && playerSymbol == currentPlayers.get(gameId)) {
            board[x][y] = playerSymbol;
            char nextPlayer = (playerSymbol == 'X') ? 'O' : 'X';
            currentPlayers.put(gameId, nextPlayer);
        }
    }

    @Override
    public char[][] getBoard(UUID gameId) throws RemoteException {
        return gameBoards.get(gameId);
    }

    @Override
    public String checkStatus(UUID gameId) throws RemoteException {
        return determineGameStatus(gameBoards.get(gameId));
    }

    private String determineGameStatus(char[][] board) {
        // Check rows, columns, diagonals for a win
        if (isWinningCondition(board, 'X')) {
            return "Player X Wins!";
        } else if (isWinningCondition(board, 'O')) {
            return "Player O Wins!";
        } else if (isBoardFull(board)) {
            return "Draw!";
        }
        return "In Progress";
    }

    private boolean isWinningCondition(char[][] board, char player) {
        // Check rows, columns and diagonals
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true;
            }
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }
        return false;
    }

    private boolean isBoardFull(char[][] board) {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public synchronized void restartGame(UUID gameId) throws RemoteException {
        char[][] board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
        gameBoards.put(gameId, board);
        currentPlayers.put(gameId, 'X'); // X starts first
    }

    public static void main(String[] args) {
        try {
        	LocateRegistry.createRegistry(1100); // Start RMI registry on port 1099
        	TicTacToeGame game = new TicTacToeServer();
        	Naming.rebind("//0.0.0.0:1100/TicTacToeGame", game);

            System.out.println("Server is ready.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
