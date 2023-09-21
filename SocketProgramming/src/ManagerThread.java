import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

public class ManagerThread extends Thread {
    Socket dataSocket;

    public ManagerThread(Socket dataSocket) {
        this.dataSocket = dataSocket;
    }

    public void run() {
        try {
            String returnMessage = "FAILURE\n";
            BufferedReader meesageStream = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            PrintStream socketOutput = new PrintStream(dataSocket.getOutputStream());

            String message = meesageStream.readLine();
            String[] request = decypherMessage(message);

            int function = Integer.parseInt(request[0]);

            if (function == 1) { // Register player
                try {
                    returnMessage = register(request) ? "SUCCESS" : "FAILURE";
                } catch (Exception e) {
                    returnMessage += e.getMessage();
                }
            } else if (function == 2) { // Query players
                Manager.playersLock.lock();
                returnMessage = String.valueOf(Manager.players.size()) + "\n"
                        + String.format("%3s: %-15s %-15s %-6s %-6s %-6s\n", "i", "name", "address",
                                "m-port",
                                "r-port", "p-port");

                for (int i = 1; i <= Manager.players.size(); i++) {
                    PlayerObj player = Manager.players.get(i - 1);
                    returnMessage += String.format("%3s: ", i) + player.toString();
                }
                Manager.playersLock.unlock();

            } else if (function == 3) {
                Manager.gamesLock.lock();
                Manager.playersLock.lock();
                returnMessage = String.valueOf(Manager.games.size());
                for (int i = 1; i <= Manager.games.size(); i++) {
                    GameObj game = Manager.games.get(i - 1);
                    returnMessage += game.toString();
                }
                Manager.playersLock.unlock();
                Manager.gamesLock.unlock();
            } else if (function == 4) {
                returnMessage = unregister(request) ? "SUCCESS" : "FAILURE";
            } else if (function == 5) {
                returnMessage = startGame(request);
            } else if (function == 99) {
                return;
            }

            socketOutput.print(returnMessage);
            socketOutput.flush();
            dataSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] decypherMessage(String message) {
        String delimeter = "="; // Does it make sense?
        return message.split(delimeter);
    }

    public static PlayerObj constructPlayer(String[] request) {
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

    public static boolean register(String[] request) {
        PlayerObj tmp = constructPlayer(request);

        boolean flag = true;

        Manager.playersLock.lock();
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

    public static boolean unregister(String[] request) {
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
        String res = "";
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
        res += game.toString();
        Manager.gamesLock.unlock();
        Manager.playersLock.unlock();

        return res;
    }

    public static void addToPlayers(PlayerObj newPlayer) {
        Manager.playersLock.lock();
        Manager.players.add(newPlayer);
        Manager.playersLock.unlock();
    }
}