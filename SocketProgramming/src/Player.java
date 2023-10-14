import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Player {
    public static Scanner scanner = new Scanner(System.in);
    static Lock userChoiceLock = new ReentrantLock();
    static int userChoice = 0;
    static String gameInfo = "";
    static String myAddress;
    static String ServerAddress;
    static int serverPortNum;
    static String name;
    static String m_port, r_port, p_port;
    static DatagramSocket socket_r;
    static DatagramSocket socket_p;
    static InGameThread inGameThread;

    public static void main(String[] args) {
        /*
         * Establishes connection with manager.
         * Shows options to player and awaits their choice.
         * Sends cyphered message to manager and waits response.
         * Prints response for player to see.
         * Repeats process.
         */
        try {
            myAddress = InetAddress.getLocalHost().getHostAddress();
            ServerAddress = args[0];
            serverPortNum = Integer.parseInt(args[1]);
            name = args[3];
            m_port = args[2];
            r_port = String.valueOf(Integer.parseInt(args[2]) + 1);
            p_port = String.valueOf(Integer.parseInt(args[2]) + 2);

            socket_r = new DatagramSocket(Integer.parseInt(r_port), InetAddress.getLocalHost());
            socket_p = new DatagramSocket(Integer.parseInt(p_port), InetAddress.getLocalHost());

            socket_r.setSoTimeout(1000);
            while (true) {
                if (name.contains("=")) {
                    System.out.println("Error: Input must not contain '='. Please rerun program with valid input.");
                    return;
                }
                GetUserInputThread getUserInputThread = new GetUserInputThread();
                inGameThread = new InGameThread(socket_r, socket_p);

                Socket socket_m = new Socket(ServerAddress, serverPortNum);
                PrintStream outputStream_m = new PrintStream(socket_m.getOutputStream());
                BufferedReader br_m = new BufferedReader(new InputStreamReader(socket_m.getInputStream()));

                String cypheredMessage = "";
                getUserInputThread.start();

                byte[] buffer = new byte[350];
                DatagramPacket datagram = new DatagramPacket(buffer, 350);
                while (getUserInputThread.isAlive()) {
                    try {
                        System.out.println("Waiting for datagram");
                        socket_r.receive(datagram);
                        System.out.println("Started ingame thread");
                        startInGameThread(false, 0, buffer);
                        inGameThread.join();
                        break;
                    } catch (SocketTimeoutException e) {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (userChoice == 0) {
                    continue;
                }

                String choice = String.valueOf(userChoice);

                if (userChoice == 1 || userChoice == 4) {
                    cypheredMessage = cypherMessage(
                            new String[] { choice, name, myAddress, m_port, r_port, p_port });
                } else if (userChoice == 2) {
                    cypheredMessage = "2";
                } else if (userChoice == 3) {
                    cypheredMessage = "3";
                } else if (userChoice == 5) {
                    System.out.print("How many other players do you want to play wtih you? [1:5]: ");
                    String tmp = scanner.nextLine();
                    String k = tmp.equals("") ? scanner.nextLine() : tmp;
                    if (!k.matches("^[1-5]$")) {
                        System.out.println("Error. Number should be between 1 and 5.");
                        continue;
                    }
                    cypheredMessage = cypherMessage(new String[] { choice, name, k });
                    outputStream_m.println(cypheredMessage);
                    if (br_m.readLine().startsWith("SUCCESS")) {
                        br_m.lines().forEach(e -> gameInfo += e + "\n");
                        startInGameThread(true, Integer.valueOf(k), null);
                    }

                    userChoice = 0;

                    socket_m.close();
                    continue;
                } else if (userChoice == 99) {
                    System.out.println("Bye Bye :)");
                    cypheredMessage = "99";
                    outputStream_m.println(cypheredMessage);
                    System.exit(0);
                }

                outputStream_m.println(cypheredMessage);
                br_m.lines().forEach(e -> System.out.println(e));

                userChoice = 0;
                socket_m.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String cypherMessage(String[] requestBody) {
        String res = "";
        for (String str : requestBody) {
            res += str + "=";
        }
        return res.substring(0, res.length() - 1);
    }

    public static void startInGameThread(boolean dealer, int k, byte[] buffer)
            throws IOException, InterruptedException {
        if (!dealer) {
            gameInfo = new String(buffer);
            String[] arr;
            String temp;
            temp = String
                    .valueOf(Integer.parseInt(gameInfo.split("\n")[(int) (gameInfo.lines().count() - 1)].trim()) - 1);
            arr = gameInfo.split("\n");
            arr[gameInfo.split("\n").length - 1] = temp;
            gameInfo = String.join("\n", arr);
        } else
            gameInfo = gameInfo + "\n" + k;

        inGameThread.start();
        inGameThread.join();
    }
}

class GetUserInputThread extends Thread {
    public void run() {
        printOptions();
        Player.userChoice = Player.scanner.nextInt();
    }

    public static void printOptions() {
        System.out.println("\nEnter any of the following numbers for the option in front of it:");
        System.out.println("  1. Register to Go Fish");
        System.out.println("  2. List all currently registered players");
        System.out.println("  3. List all currently running games");
        System.out.println("  4. Unregister from Go Fish");
        System.out.println("  5. Start game");
        System.out.println(" 99. Exit");
        System.out.print("Enter your choice: ");
    }
}

class InGameThread extends Thread {
    DatagramSocket socket_r;
    DatagramSocket socket_p;

    public InGameThread(DatagramSocket socket_r, DatagramSocket socket_p) {
        this.socket_r = socket_r;
        this.socket_p = socket_p;
    }

    public void run() {
        try {
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            setup();
            System.out.println("You are registered successfully.");
            while (true) {
                
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setup() throws IOException {
        boolean foundMyself = false;
        boolean finalPlayerNotDealer = false;
        PlayerObj nextPlayer = null;
        PlayerObj dealer = null;
        for (int i = 3; i < Player.gameInfo.split("\n").length - 2; i++) {
            String s = Player.gameInfo.split("\n")[i];

            if (Player.gameInfo.split("\n")[Player.gameInfo.split("\n").length - 1].equals("0"))
                return;

            String[] res = findRealStrings(s);
            PlayerObj temp = new PlayerObj(res[0], res[1], Integer.parseInt(res[2]), Integer.parseInt(res[3]),
                    Integer.parseInt(res[4]));
            if (temp.getName().equals(Player.name)) {
                foundMyself = true;
                continue;
            }

            if (s.trim().substring(0, 3).trim().equals("1")) {
                res = findRealStrings(s);
                dealer = new PlayerObj(res[0], res[1], Integer.parseInt(res[2]), Integer.parseInt(res[3]),
                        Integer.parseInt(res[4]));
            }

            if (foundMyself) {
                res = findRealStrings(s);
                nextPlayer = new PlayerObj(res[0], res[1], Integer.parseInt(res[2]), Integer.parseInt(res[3]),
                        Integer.parseInt(res[4]));
                finalPlayerNotDealer = true;
                break;
            }
        }

        if (!finalPlayerNotDealer) {
            System.out.println("Next is dealer");
            nextPlayer = dealer;
        }

        byte[] buffer = Player.gameInfo.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, nextPlayer.getInetAddress(),
                nextPlayer.getR_port());
        socket_r.send(packet);
    }

    public static String[] findRealStrings(String s) {
        String[] res = new String[5];
        res[0] = s.substring(5, 20).trim();
        res[1] = s.substring(20, 36).trim();
        res[2] = s.substring(37, 43).trim();
        res[3] = s.substring(44, 50).trim();
        res[4] = s.substring(51, 57).trim();
        return res;
    }
}