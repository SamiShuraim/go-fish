# CSE434-Socket-Programming: Go Fish Game

This project implements a multiplayer "Go Fish" card game using Java socket programming. It features a central server (`Manager`) that handles player registration and game initiation, and direct peer-to-peer communication between players during gameplay.

## Features

*   **Player Registration:** Players can register with the game server with a unique name.
*   **List Players:** View a list of all currently registered players.
*   **List Games:** View a list of currently active game sessions.
*   **Start Game:** Registered players can initiate a new game of Go Fish, specifying the number of opponents (1-4).
*   **Multiplayer Gameplay:** Supports multiple players in a single game.
*   **Go Fish Logic:** Implements the core rules of the Go Fish card game:
    *   Asking other players for cards of a specific rank.
    *   Drawing cards from the deck ("Go Fish") if the asked player doesn't have the card.
    *   Forming "books" (sets of four cards of the same rank).
    *   Determining the winner based on the number of books.
*   **Socket Communication:**
    *   Uses TCP sockets for client-server communication with the `Manager`.
    *   Uses UDP sockets for peer-to-peer communication during active gameplay.

## Original Command-Line Interface (CLI)

### Prerequisites

*   Java Development Kit (JDK) installed.
*   Ability to compile and run Java programs from the command line.

### Running the Game

1.  **Compile the Java files:**
    Navigate to the `SocketProgramming/src` directory and compile all `.java` files.
    ```bash
    cd SocketProgramming/src
    javac *.java
    ```

2.  **Start the Manager (Server):**
    Open a terminal and run the `Manager` class, providing a port number for the server to listen on.
    ```bash
    java Manager <server_port_number>
    ```
    For example:
    ```bash
    java Manager 7000
    ```

3.  **Start Player(s) (Clients):**
    For each player, open a new terminal and run the `Player` class with the following arguments:
    ```bash
    java Player <server_address> <server_port_number> <client_m_port> <player_name>
    ```
    *   `<server_address>`: The IP address or hostname of the machine running the `Manager` (e.g., `localhost` if running on the same machine).
    *   `<server_port_number>`: The port number the `Manager` is listening on (e.g., `7000`).
    *   `<client_m_port>`: A unique base port number for this player's communication. The player will use this port, `port + 1` (for `r_port`), and `port + 2` (for `p_port`). Ensure this port and the subsequent two are available.
    *   `<player_name>`: The desired name for the player.

    For example:
    ```bash
    java Player localhost 7000 8001 Alice
    java Player localhost 7000 8004 Bob
    ```

4.  **Playing the Game:**
    Once connected, each player will see a menu of options:
    *   Register to Go Fish
    *   List all currently registered players
    *   List all currently running games
    *   Unregister from Go Fish
    *   Start game
    *   Exit

    Follow the on-screen prompts to play the game.

## Future Plans: React Frontend

The current implementation uses a command-line interface. The project is planned to be refactored to:

*   Separate the game logic (backend) from the user interface.
*   Develop a React-based frontend to provide a graphical user interface for a richer user experience.
*   Adapt the socket communication to work seamlessly with a web-based frontend, likely involving WebSockets or a similar technology.

## Project Structure

```
Project/
├── SocketProgramming/
│   ├── .vscode/
│   ├── lib/      # (If any external libraries are used)
│   └── src/      # Contains all Java source code (.java files) and compiled classes (.class files)
│       ├── Card.java
│       ├── Deck.java
│       ├── GameObj.java
│       ├── GetUserInputThread.java
│       ├── InGameThread.java
│       ├── Manager.java
│       ├── ManagerThread.java
│       ├── Player.java
│       └── PlayerObj.java
├── .gitignore
├── FinalProjectReport.docx
├── FinalProjectReport.pdf
└── README.md       # This file
```
