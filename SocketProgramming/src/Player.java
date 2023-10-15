import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Player {
    public static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
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
            socket_p.setSoTimeout(1000);
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
                        Thread.sleep(500);
                        socket_r.receive(datagram);
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
                    System.out.print("How many other players do you want to play wtih you? [1:4]: "); // jjj
                    String tmp = bufferedReader.readLine();
                    String k = tmp.equals("") ? bufferedReader.readLine() : tmp;
                    if (!k.matches("^[1-4]$")) {
                        System.out.println("Error. Number should be between 1 and 4.");
                        continue;
                    }
                    cypheredMessage = cypherMessage(new String[] { choice, name, k });
                    outputStream_m.println(cypheredMessage);
                    if (br_m.readLine().startsWith("SUCCESS")) {
                        br_m.lines().forEach(e -> gameInfo += e + "\n");
                        gameInfo = gameInfo.trim();
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

    public static String[] decypherMessage(String message) {
        String delimeter = "=";
        return message.split(delimeter);
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
        while (!Player.inGameThread.isAlive()) {
            try {
                if (Player.bufferedReader.ready()) {
                    Player.userChoice = Integer.parseInt(Player.bufferedReader.readLine().trim());
                    break;
                } else
                    Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
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
    GameObj thisGame;

    public InGameThread(DatagramSocket socket_r, DatagramSocket socket_p) {
        this.socket_r = socket_r;
        this.socket_p = socket_p;
    }

    public void run() {
        try {
            System.out.println("\n".repeat(20));
            setup();
            System.out.println("You are registered successfully.");

            String x;
            while (true) {
                try {
                    Thread.sleep(500);
                    x = listenToSocketR();

                    if (x != null && x.split("\n")[0].trim().equals("take-cards")) {
                        break;
                    }

                    if (thisGame.getDealer().getName().equals(Player.name))
                        break;

                    System.out.println("Waiting");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!thisGame.getDealer().getName().equals(Player.name))
                thisGame.setDeck(makeDeck(x.split("\n")[1]));

            if (thisGame.getPlayers().size() < 3) {
                for (int i = 0; i < 7; i++) {
                    getMe().getHand().add(thisGame.getDeck().draw());
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    getMe().getHand().add(thisGame.getDeck().draw());
                }
            }

            System.out.println(getMe().showHand());

            if (thisGame.getDealer().getName().equals(Player.name) && x.split("\n")[0].trim().equals("take-cards")) {
                sendYourMove();
            } else {
                sendTakeCards();
            }

            while (true) { ////////////////////////////////////////////////////////// HERE
                           ////////////////////////////////////////////////////////// ///////////////////////////////////
                String r, p[];
                while (true) { // waiting
                    try {
                        Thread.sleep(500);
                        r = listenToSocketR();
                        p = listenToSocketP();

                        if (r != null && r.split("\n")[0].trim().equals("take-cards")) {
                            thisGame.setDeck(makeDeck(r.split("\n")[1]));
                            sendYourMove();
                        }

                        if (r != null && r.split("\n")[0].trim().equals("your-move")) {
                            break;
                        }
                        if (p != null) {
                            int cardsPulled = 0;
                            String temp = "";
                            for (Card c : getMe().getHand()) {
                                if (c.getRank().equals(p[0].trim().toUpperCase())) {
                                    temp += c + " ";
                                    cardsPulled++;
                                }
                            }
                            if (cardsPulled > 0) {
                                temp = temp.trim();
                                for (String s : temp.split(" ")) {
                                    getMe().deleteCard(s);
                                }
                                sendToPeer(Player.cypherMessage(temp.split(" ")), p[1], p[2]);
                            } else {
                                sendToPeer("go-fish", p[1], p[2]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                thisGame.setDeck(makeDeck(r.split("\n")[1]));

                System.out.println("Playing");
                System.out.println(getMe().showHand());

                boolean continuePlaying = true;
                while (continuePlaying) {
                    if (getMe().getHand().size() == 0) {
                        System.out.println("since you have an empty hand you may 1: draw 5 cards or 2: skip your turn");
                        int choice = 1;
                        if (choice == 1) {
                            for (int i = 0; i < 5; i++) {
                                if (thisGame.getDeck().getDeck().size() > 0)
                                    getMe().getHand().add(thisGame.getDeck().draw());
                            }
                        }
                    }
                    System.out.print("Enter the rank of the card you want: ");
                    Player.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                    String rank = Player.bufferedReader.readLine().trim().toUpperCase();
                    if (!rankInHand(rank)) {
                        System.out.println("You can not make this move. You do not have a card of rank " + rank);
                        continue;
                    }
                    continuePlaying = fishing(rank);

                    System.out.println(getMe().showHand());
                }

                sendYourMove();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendBookToAll(String rank) throws IOException {
        String res = String.format("D%s C%s, H%s S%s", rank, rank, rank, rank);
        for (int i = 0; i < thisGame.getPlayers().size(); i++) {
            if (!thisGame.getPlayers().get(i).getName().equals(Player.name)) {
                sendToPeer(res, i + 1);
            }
        }
    }

    public void sendYourMove() throws IOException, InterruptedException {
        String[] temp = thisGame.getDeck().getDeck().toString().split(", ");
        temp[0] = temp[0].substring(1, temp[0].length());
        temp[temp.length - 1] = temp[temp.length - 1].substring(0, temp[0].length());
        String deck = Player.cypherMessage(temp);
        sendToNextPlayer("your-move" + "\n" + deck);
    }

    public void sendTakeCards() throws IOException, InterruptedException {
        String[] temp = thisGame.getDeck().getDeck().toString().split(", ");
        temp[0] = temp[0].substring(1, temp[0].length());
        temp[temp.length - 1] = temp[temp.length - 1].substring(0, temp[0].length());
        String deck = Player.cypherMessage(temp);
        sendToNextPlayer("take-cards" + "\n" + deck);
    }

    public static ArrayList<Card> makeDeck(String str) {
        ArrayList<Card> res = new ArrayList<>();
        for (String cardStr : Player.decypherMessage(str)) {
            res.add(new Card(cardStr));
        }
        return res;
    }

    public boolean rankInHand(String rank) {
        for (Card c : getMe().getHand()) {
            if (c.getRank().equals(rank.trim()))
                return true;
        }
        return false;
    }

    public boolean fishing(String rank) throws IOException, InterruptedException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        int res = 0;
        while (true) {
            System.out.println(thisGame.toString());
            System.out.print("Enter the number of the player you want to fish from: ");
            res = Integer.parseInt(bufferedReader.readLine().trim());
            if (thisGame.getPlayers().get(res - 1).getName().equals(Player.name)) {
                System.out.println("You cannot fish from yourself. Choose a different number.");
            } else
                break;
        }

        String[] response;
        sendToPeer(rank, res);
        while (true) {
            try {
                response = listenToSocketP();
                if (response != null && response[0].trim().length() != 1)
                    break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String[] temp = Player.decypherMessage(response[0]);

        if (temp[0].trim().equals("go-fish")) {
            if (thisGame.getDeck().getDeck().size() > 0)
                getMe().getHand().add(thisGame.getDeck().draw());
            else
                System.out.println("The stock is empty");

            return false;
        }

        for (String s : temp) {
            getMe().getHand().add(new Card(s));
        }
        return true;
    }

    public void setup() throws NumberFormatException, IOException, InterruptedException {
        if (Player.gameInfo.split("\n")[1].trim().equals("-1"))
            return;
        createGameObj(Player.gameInfo);

        int i = 0;
        for (i = 0; i < thisGame.getPlayers().size(); i++) {
            if (thisGame.getPlayers().get(i).getName().equals(Player.name))
                break;
        }

        sendToNextPlayer(Player.gameInfo);
    }

    public static PlayerObj createPlayerObj(String[] res) throws NumberFormatException, UnknownHostException {
        return new PlayerObj(res[0], res[1], Integer.parseInt(res[2]), Integer.parseInt(res[3]),
                Integer.parseInt(res[4]));
    }

    public void sendToNextPlayer(String message) throws IOException, InterruptedException {
        PlayerObj nextPlayer = getNextPlayer();
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, nextPlayer.getInetAddress(),
                nextPlayer.getR_port());
        socket_r.send(packet);
        Thread.sleep(500);
    }

    public PlayerObj getNextPlayer() {
        int i = 0;
        for (i = 0; i < thisGame.getPlayers().size(); i++) {
            if (thisGame.getPlayers().get(i).getName().equals(Player.name))
                break;
        }
        return thisGame.getPlayers().get((i + 1) % thisGame.getPlayers().size());
    }

    public void createGameObj(String gameInfo) throws NumberFormatException, UnknownHostException {
        String[] strings = Player.decypherMessage(gameInfo.split("\n")[0]);
        String gameId = strings[0];
        PlayerObj dealer = createPlayerObj(new String[] { strings[1], strings[2], strings[3], strings[4], strings[5] });
        ArrayList<PlayerObj> players = new ArrayList<>();
        int seed = Integer.parseInt(gameInfo.split("\n")[1].trim());
        for (int i = 0; i < (strings.length - 1) / 5; i++) {

            PlayerObj temp = createPlayerObj(new String[] { strings[i * 5 + 1], strings[i * 5 + 2], strings[i * 5 + 3],
                    strings[i * 5 + 4], strings[i * 5 + 5] });
            players.add(temp);
        }
        thisGame = new GameObj(players, dealer, Integer.parseInt(gameId), seed);
    }

    public void sendToPeer(String message, String address, String port)
            throws NumberFormatException, IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(address),
                Integer.parseInt(port));

        socket_p.send(packet);
    }

    public void sendToPeer(String message, int peerNum) throws IOException {
        PlayerObj p = thisGame.getPlayers().get(peerNum - 1);
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, p.getInetAddress(), p.getP_port());

        socket_p.send(packet);
    }

    public String[] listenToSocketP() {
        try {
            byte[] buffer = new byte[350];
            DatagramPacket datagram = new DatagramPacket(buffer, 350);
            socket_p.receive(datagram);
            return new String[] { new String(buffer), datagram.getAddress().getHostAddress(),
                    String.valueOf(datagram.getPort()) };
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String listenToSocketR() {
        try {
            byte[] buffer = new byte[350];
            DatagramPacket datagram = new DatagramPacket(buffer, 350);
            socket_r.receive(datagram);
            return new String(buffer);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PlayerObj getMe() {
        PlayerObj res = null;
        for (PlayerObj p : thisGame.getPlayers())
            if (p.getName().equals(Player.name))
                res = p;

        return res;
    }
}