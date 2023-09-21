import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Player {
    public static void main(String[] args) {
        try {
            String address = args[0];
            int serverPortNum = Integer.parseInt(args[1]);
            while (true) {
                String name = args[3];
                String m_port = args[2], r_port = String.valueOf(Integer.parseInt(args[2]) + 1),
                        p_port = String.valueOf(Integer.parseInt(args[2]) + 2);
                Socket clientSocket = new Socket(address, serverPortNum);
                PrintStream outputStream = new PrintStream(clientSocket.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                Scanner scanner = new Scanner(System.in);

                if (name.contains("=")) {
                    System.out.println("Error: Input must not contain '='. Please rerun program with valid input.");
                    return;
                }

                String cypheredMessage = "";
                printOptions();
                int userChoice = scanner.nextInt();
                String choice = String.valueOf(userChoice);

                if (userChoice == 1 || userChoice == 4) {
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
                    outputStream.println(cypheredMessage);
                    System.exit(0);
                }

                outputStream.println(cypheredMessage);
                br.lines().forEach(e -> System.out.println(e));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
