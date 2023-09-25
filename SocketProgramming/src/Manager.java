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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Manager {
    public static ArrayList<GameObj> games = new ArrayList<>();
    public static ArrayList<PlayerObj> players = new ArrayList<>();
    public static Lock playersLock = new ReentrantLock();
    public static Lock gamesLock = new ReentrantLock();
    public static int gameId = 1;

    public static void main(String[] args) {
        /*
         * Waits for each request and creates a thread for it.
         */
        try {
            int serverPortNumber = Integer.parseInt(args[0]);
            ServerSocket connectionSocket = new ServerSocket(serverPortNumber);
            while (true) {
                Socket dataSocket = connectionSocket.accept(); // accepts each request sent to it
                ManagerThread thread = new ManagerThread(dataSocket);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
