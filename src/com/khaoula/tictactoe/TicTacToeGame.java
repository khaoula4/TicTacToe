package com.khaoula.tictactoe;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface TicTacToeGame extends Remote {
    UUID startNewGame() throws RemoteException;
    void makeMove(UUID gameId, int x, int y, char playerSymbol) throws RemoteException;
    char[][] getBoard(UUID gameId) throws RemoteException;
    String checkStatus(UUID gameId) throws RemoteException;
    void restartGame(UUID gameId) throws RemoteException;
    void joinGame(UUID sessionId, UUID playerId) throws RemoteException;
}
