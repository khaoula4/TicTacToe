package com.khaoula.tictactoe;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;

public class TicTacToeServer extends UnicastRemoteObject implements TicTacToeGame {
    private Map<UUID, GameSession> sessions = new ConcurrentHashMap<>();
    private UUID waitingPlayer = null; // Stores the ID of a player waiting for an opponent

    protected TicTacToeServer() throws RemoteException {
        super();
    }

    @Override
    public UUID startNewGame() throws RemoteException {
        UUID playerId = UUID.randomUUID();
        System.out.println("startNewGame called. Generated player ID: " + playerId);

        if (waitingPlayer == null) {
            waitingPlayer = playerId; // This player now waits for an opponent
            System.out.println("No waiting player. New player " + playerId + " is now waiting.");
            return playerId;
        } else {
            GameSession session = new GameSession(waitingPlayer, playerId);
            sessions.put(session.getSessionId(), session);
            System.out.println("New session created. Session ID: " + session.getSessionId() + " with players " + waitingPlayer + " and " + playerId);
            waitingPlayer = null; // Reset waitingPlayer
            return session.getSessionId(); // Return the session ID to both players
        }
    }

    @Override
    public synchronized void makeMove(UUID sessionId, int x, int y, char playerSymbol) throws RemoteException {
        System.out.println("makeMove called. Session ID: " + sessionId + ", Coordinates: (" + x + ", " + y + "), Player Symbol: " + playerSymbol);
        GameSession session = sessions.get(sessionId);

        if (session != null) {
            session.makeMove(x, y, playerSymbol);
        } else {
            System.out.println("Error: Session not found for ID: " + sessionId);
            throw new RemoteException("Game session not found for ID: " + sessionId);
        }
    }

    @Override
    public char[][] getBoard(UUID sessionId) throws RemoteException {
        System.out.println("getBoard called. Session ID: " + sessionId);
        GameSession session = sessions.get(sessionId);

        if (session != null) {
            return session.getBoard();
        } else {
            System.out.println("Error: Session not found for ID: " + sessionId);
            throw new RemoteException("Game session not found for ID: " + sessionId);
        }
    }

    @Override
    public String checkStatus(UUID sessionId) throws RemoteException {
        System.out.println("checkStatus called. Session ID: " + sessionId);
        GameSession session = sessions.get(sessionId);

        if (session != null) {
            return session.checkStatus();
        } else {
            System.out.println("Error: Session not found for ID: " + sessionId);
            throw new RemoteException("Game session not found for ID: " + sessionId);
        }
    }

    @Override
    public synchronized void restartGame(UUID sessionId) throws RemoteException {
        System.out.println("restartGame called. Session ID: " + sessionId);
        GameSession session = sessions.get(sessionId);

        if (session != null) {
            session.restartGame();
        } else {
            System.out.println("Error: Session not found for ID: " + sessionId);
            throw new RemoteException("Game session not found for ID: " + sessionId);
        }
    }
    @Override
    public void joinGame(UUID sessionId, UUID playerId) throws RemoteException {
        System.out.println("joinGame called. Session ID: " + sessionId + ", Player ID: " + playerId);
        GameSession session = sessions.get(sessionId);

        if (session != null) {
            // Check if the session already has two players
            if (session.getPlayer2() == null) {
                session.setPlayer2(playerId);
                System.out.println("Player " + playerId + " joined session " + sessionId);
            } else {
                System.out.println("Error: Session " + sessionId + " already has two players.");
                throw new RemoteException("Session already full.");
            }
        } else {
            System.out.println("Error: Session not found for ID: " + sessionId);
            throw new RemoteException("Game session not found for ID: " + sessionId);
        }
    }


    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            TicTacToeGame game = new TicTacToeServer();
            Naming.rebind("//0.0.0.0:1099/TicTacToeGame", game);
            System.out.println("Server is ready.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

