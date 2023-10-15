import java.util.ArrayList;
import java.util.Random;

public class GameObj {
    private final int id;
    private final ArrayList<PlayerObj> players; // This should include the dealer
    private final PlayerObj dealer;
    private Deck deck;
    private int seed;
    public int bookCounter;

    public GameObj(ArrayList<PlayerObj> players, PlayerObj dealer, int id, int seed) {
        this.id = id;
        this.players = players;
        this.dealer = dealer;
        this.seed = seed;
        this.deck = new Deck(seed);
    }

    public GameObj(ArrayList<PlayerObj> players, PlayerObj dealer, int seed) {
        this(players, dealer, Manager.gameId++, seed);
    }

    public int getId() {
        return this.id;
    }

    public ArrayList<PlayerObj> getPlayers() {
        return this.players;
    }

    public PlayerObj getDealer() {
        return this.dealer;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<Card> x) {
        this.deck = new Deck(x);
    }

    public int getSeed() {
        return this.seed;
    }

    public void checkBooks(boolean bool){
        if(bool)
            this.bookCounter++;
    }

    @Override
    public String toString() {
        String str = "\nGame " + getId() + ": \n"
                + String.format("\t%3s: %-15s %-15s %-6s %-6s %-6s\n", "i", "name", "address",
                        "m-port",
                        "r-port", "p-port");

        int i = 1;
        for (PlayerObj player : getPlayers()) {
            str += String.format("\t%3d: %-15s %-15s %-6d %-6d %-6d\n",
                    i, player.getName(),
                    player.getAddress(), player.getM_port(),
                    player.getR_port(), player.getP_port());
            i++;
        }
        return str;
    }
}