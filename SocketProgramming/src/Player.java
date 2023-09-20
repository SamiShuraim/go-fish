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
            while (true) {
                String name = args[1];
                String address = InetAddress.getLocalHost().getHostAddress();
                String m_port = args[0], r_port = String.valueOf(Integer.parseInt(args[0]) + 1),
                        p_port = String.valueOf(Integer.parseInt(args[0]) + 2);

                Socket clientSocket = new Socket(acceptorHost, serverPortNum);
                Scanner scanner = new Scanner(System.in);

                String cypheredMessage = "";
                printOptions();
                int userChoice = scanner.nextInt();
                String choice = String.valueOf(userChoice);

                if (userChoice == 1 || userChoice == 4) {
                    // String name = scanner.nextLine();
                    cypheredMessage = cypherMessage(new String[] { choice, name, address, m_port, r_port, p_port });
                } else if (userChoice == 2) {
                    cypheredMessage = "2";
                } else if (userChoice == 3) {
                    cypheredMessage = "3";
                } else if (userChoice == 5) {
                    String k = "1";
                    cypheredMessage = cypherMessage(new String[] { choice, name, k });
                } else if (userChoice == 99) {
                    System.out.println("Bye Bye");
                    cypheredMessage = "99";
                    (new PrintStream(clientSocket.getOutputStream())).println(cypheredMessage);
                    System.exit(0);
                }

                (new PrintStream(clientSocket.getOutputStream())).println(cypheredMessage);
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                br.lines().forEach(e -> System.out.println(e));
            }
            // clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println(register());
    }

    // public static boolean register() {

    // }

    public static void printOptions() {
        System.out.println("\nEnter any of the following numbers for the option in front of it:");
        System.out.println(" 1. Register to Go Fish");
        System.out.println(" 2. List all currently registered players");
        System.out.println(" 3. List all currently running games");
        System.out.println(" 4. Unregister from Go Fish");
        System.out.println(" 5. Start game");

        System.out.println("99. Exit");

        System.out.print("Enter your choice: ");
    }

    public static String cypherMessage(String[] requestBody) {
        String res = "";
        for (String str : requestBody) {
            res += str + "=";
        }
        return res.substring(0, res.length() - 1);
    }
}
