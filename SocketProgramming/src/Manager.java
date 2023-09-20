import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class Manager {
    static ArrayList<PlayerObj> players = new ArrayList<>();

    public static void main(String[] args) {
        try {
            int serverPortNumber = 80;
            ServerSocket connectionSocket = new ServerSocket(serverPortNumber);
            while (true) {
                String returnMessage = "";
                Socket dataSocket = connectionSocket.accept(); // accepts each request sent to it
                BufferedReader meesageStream = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

                String message = meesageStream.readLine();
                String[] request = decipherMessage(message);
                int function = Integer.parseInt(request[0]);

                if (function == 1) {
                    try {
                        returnMessage = register(request) ? "SUCCESS" : "FAILURE";
                    } catch (Exception e) {
                        returnMessage = "FAILURE\n" + e.getMessage();
                    }
                } else if (function == 2) {

                } else if (function == 3) {

                } else if (function == 4) {

                }

                PrintStream socketOutput = new PrintStream(dataSocket.getOutputStream());
                socketOutput.print(returnMessage);
                socketOutput.flush();
                dataSocket.close();
            }
            // connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] decipherMessage(String message) {
        String delimeter = "="; // Does it make sense?
        return message.split(delimeter);
    }

    public static boolean register(String[] request) {
        String name, address;
        int m_port, r_port, p_port;
        name = request[1];
        if (name.length() > 15)
            throw new IllegalArgumentException("Invalid Request: Player name should only be 15 characters or less.");
        try {
            // InetSocketAddress address = new
            // InetSocketAddress(InetAddress.getByAddress(request[2].getBytes()),
            // Integer.parseInt(request[3]));
            address = request[2];
            m_port = Integer.parseInt(request[3]);
            r_port = Integer.parseInt(request[4]);
            p_port = Integer.parseInt(request[5]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Error: Port needs to be a number.");
        }

        PlayerObj tmp = new PlayerObj(name, address, m_port, r_port, p_port);

        for (PlayerObj player : players) {
            if (player.equals(tmp)) {
                return false;
            }
        }

        players.add(tmp);

        return true;
    }
}
