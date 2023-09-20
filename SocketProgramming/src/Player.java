import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Player {
    public static void main(String[] args) {
        try {
            InetAddress acceptorHost = InetAddress.getLocalHost();
            int serverPortNum = 80;
            Socket clientSocket = new Socket(acceptorHost, serverPortNum);
            (new PrintStream(clientSocket.getOutputStream())).println("HUH");

            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Player: " + br.readLine());
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println(register());
    }

    // public static boolean register() {

    // }
}
