# Overview
This project implements a Tic-Tac-Toe game using Java RMI, created for a Middleware course. It enables two players to play Tic-Tac-Toe remotely. This project is collaboratively developed by Khaoula Jbari, Safae Ajoutate, and Imrane Asrir.

# Components
- **TicTacToeGame**: The interface defining the remote methods used in the game.
- **TicTacToeServer**: Implements the TicTacToeGame interface, managing game sessions and player interactions.
- **TicTacToeClient**: The client-side application that players interact with to play the game.
- **GameSession**: Represents a game session, holding the game state and managing game logic.

# How to Run
1. Start the RMI registry.
2. Run `TicTacToeServer` to initiate the server.
3. Run `TicTacToeClient` on two different machines (or instances) to start playing.
4. Follow the on-screen instructions to either start a new game or join an existing one using a session ID.

# Features
- Start a new game or join an existing game using a session ID.
- Interactive Tic-Tac-Toe board with a graphical user interface.
- Network communication using Java RMI.

# Collaborators
- Khaoula Jbari
- Safae Ajoutate
- Imrane Asrir
