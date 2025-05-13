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

## Project Structure

```
Project/
├── backend/                # Java backend for the Go Fish game
│   ├── .vscode/           # (If any VS Code configuration files exist)
│   ├── lib/               # (If any external libraries are used)
│   └── src/               # Contains all Java source code
│       ├── Card.java      # Represents a playing card
│       ├── Deck.java      # Manages the deck of cards
│       ├── GameObj.java   # Represents a game session
│       ├── Manager.java   # The central server that manages player registration and games
│       ├── ManagerThread.java # Handles individual client connections to the Manager
│       ├── Player.java    # The client application for players
│       └── PlayerObj.java # Represents a player in the game
│
├── frontend/              # React frontend (new addition)
│   ├── public/            # Public assets
│   ├── src/               # React source code
│   ├── package.json       # NPM dependencies
│   └── ...                # Other React app files
│
├── .gitignore
├── FinalProjectReport.docx
├── FinalProjectReport.pdf
└── README.md              # This file
```

## Original Command-Line Interface (CLI)

### Prerequisites

*   Java Development Kit (JDK) installed.
*   Ability to compile and run Java programs from the command line.

### Running the Game

1.  **Compile the Java files:**
    Navigate to the `backend/src` directory and compile all `.java` files.
    ```bash
    cd backend/src
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

## React Frontend (Under Development)

The project now includes a React-based frontend for a more modern user experience:

### Prerequisites

*   Node.js and npm installed.

### Running the Frontend

1.  **Install dependencies:**
    ```bash
    cd frontend
    npm install
    ```

2.  **Start the development server:**
    ```bash
    npm start
    ```
    This will start the React development server, typically on port 3000.

### Development Plan

The React frontend is currently under development. The plan is to:

*   Create a user-friendly interface for player registration and game setup.
*   Implement real-time game state visualization using React components.
*   Connect the frontend to the Java backend using WebSockets or a similar technology.
*   Replace command-line input with graphical UI elements.
