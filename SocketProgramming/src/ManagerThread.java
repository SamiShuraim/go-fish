import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ManagerThread extends Thread {
    Socket dataSocket;

    public ManagerThread(Socket dataSocket) {
        this.dataSocket = dataSocket;
    }

    public void run() {
        /*
         * Once thread is started, it waits fot a message from the player.
         * Once recieved, it decyphers it and starts carrying out the instruction.
         * Returns a message of either "SUCCESS", "FAILURE", or "FAILURE" with a special
         * error message.
         * Terminates after sending response back to player.
         */
        try {
            String returnMessage = "";
            BufferedReader meesageStream = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            PrintStream socketOutput = new PrintStream(dataSocket.getOutputStream());

            String message = meesageStream.readLine(); // Message from player
            String[] request = decypherMessage(message);

            int function = Integer.parseInt(request[0]);

            if (function == 1) { // Register player
                try {
                    returnMessage = register(request) ? "SUCCESS" : "FAILURE";
                } catch (Exception e) {
                    returnMessage = "FAILURE\n" + e.getMessage();
                }
            } else if (function == 2) { // Query players
                returnMessage = getAllPlayers();
            } else if (function == 3) { // Query games
                returnMessage = getAllGames();
            } else if (function == 4) { // de-register
                returnMessage = unregister(request) ? "SUCCESS" : "FAILURE";
            } else if (function == 5) { // start game
                returnMessage = startGame(request);
            } else if (function == 6) {
                returnMessage = endGame(request);
            } else if (function == 99) {
                return;
            }

            socketOutput.print(returnMessage); // Sends response back to player.
            socketOutput.flush();
            dataSocket.close(); // Terminates.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] decypherMessage(String message) {
        String delimeter = "=";
        return message.split(delimeter);
    }

    public static PlayerObj constructPlayer(String[] request) throws UnknownHostException {
        /*
         * Validates info given by player and then creates and returns playerObj.
         */
        String name, address;
        int m_port, r_port, p_port;
        name = request[1];
        if (name.length() > 15)
            throw new IllegalArgumentException("Invalid Request: Player name should only be 15 characters or less.");
        try {
            address = request[2];
            m_port = Integer.parseInt(request[3]);
            r_port = Integer.parseInt(request[4]);
            p_port = Integer.parseInt(request[5]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error: Port needs to be a number.");
        }

        return new PlayerObj(name, address, m_port, r_port, p_port);
    }

    public static boolean register(String[] request) throws UnknownHostException {
        /*
         * Checks if player can be registered and then adds them to player list.
         * Player can be registered if:
         * No other player has the same username.
         * No other player has the same address and port numbers.
         */
        PlayerObj tmp = constructPlayer(request);

        boolean flag = true;

        Manager.playersLock.lock(); // Locks out other threads from accessing "players" list
        for (PlayerObj player : Manager.players) {
            if (player.equals(tmp))
                flag = false;
        }
        Manager.playersLock.unlock();

        if (flag == false)
            return false;

        addToPlayers(tmp);

        return true;
    }

    public static String getAllPlayers() {
        /*
         * Goes over all players, constructing a string that carries all of their
         * information to be later sent as the message to player.
         */
        Manager.playersLock.lock();
        String returnMessage = String.valueOf(Manager.players.size()) + "\n"
                + String.format("%3s: %-15s %-15s %-6s %-6s %-6s\n", "i", "name", "address",
                        "m-port",
                        "r-port", "p-port");

        for (int i = 1; i <= Manager.players.size(); i++) {
            PlayerObj player = Manager.players.get(i - 1);
            returnMessage += String.format("%3s: ", i) + player.toString();
        }
        Manager.playersLock.unlock();
        return returnMessage;
    }

    public static String getAllGames() {
        /*
         * Goes over all games, constructing a string that carries all of their
         * information to be later sent as the message to player.
         */
        Manager.gamesLock.lock(); // Locks out other threads from accessing "gasmes" list
        Manager.playersLock.lock();
        String returnMessage = String.valueOf(Manager.games.size());
        for (int i = 1; i <= Manager.games.size(); i++) {
            GameObj game = Manager.games.get(i - 1);
            returnMessage += game.toString();
        }
        Manager.playersLock.unlock();
        Manager.gamesLock.unlock();
        return returnMessage;
    }

    public static boolean unregister(String[] request) {
        /*
         * De-registers the player with the same name as the one making the request.
         * With no login system, this can be abused heavily.
         */
        PlayerObj playerToRemove = null;
        String name = request[1];

        Manager.playersLock.lock();
        for (PlayerObj player : Manager.players)
            if (player.getName().equals(name)) {
                playerToRemove = player;
            }

        if (playerToRemove != null)
            Manager.players.remove(playerToRemove);

        Manager.playersLock.unlock();

        return playerToRemove != null;
    }

    public static String startGame(String[] request) {
        /*
         * Finds the playerObj form of the dealer by their name.
         * Randomly selects k other players from "players" list.
         * Creates gameObj, adds it to "games" list, and returns a string representing
         * it.
         */
        String res = "SUCCESS";
        ArrayList<PlayerObj> newPlayers = new ArrayList<>();
        String name = request[1];
        PlayerObj dealer = null;
        int k = Integer.parseInt(request[2]);

        Manager.playersLock.lock();
        for (PlayerObj player : Manager.players)
            if (player.getName().equals(name))
                dealer = player;
        Manager.playersLock.unlock();

        if (dealer == null)
            return "FAILURE";

        Manager.playersLock.lock();
        for (int i = 0; i < k; i++) {
            PlayerObj player = Manager.players.get((int) (Math.random() * Manager.players.size()));
            if (player.equals(dealer))
                i--;
            else
                newPlayers.add(player);
        }
        Manager.gamesLock.lock();
        GameObj game = new GameObj(newPlayers, dealer);
        Manager.games.add(game);
        res += "\n" + game.toString();
        Manager.gamesLock.unlock();
        Manager.playersLock.unlock();

        return res;
    }

    public static String endGame(String[] request) {
        String res = "FAILURE\n";
        int gameId = Integer.valueOf(request[1]);
        String playerName = request[2];

        Manager.gamesLock.lock();
        for (GameObj g : Manager.games) {
            if (g.getId() == gameId && g.getDealer().getName().equals(playerName)) {
                res = "SUCCESS\n";
                Manager.games.remove(g);
                break;
            }
        }
        Manager.gamesLock.unlock();

        return res;
    }

    public static void addToPlayers(PlayerObj newPlayer) {
        Manager.playersLock.lock();
        Manager.players.add(newPlayer);
        Manager.playersLock.unlock();
    }
}