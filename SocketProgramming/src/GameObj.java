import java.util.ArrayList;

public class GameObj {
    private final int id;
    private final ArrayList<PlayerObj> players; // This should include the dealer
    private final PlayerObj dealer;
    private Deck deck = new Deck();

    public GameObj(ArrayList<PlayerObj> players, PlayerObj dealer, int id) {
        this.id = id;
        this.players = players;
        this.dealer = dealer;
    }

    public GameObj(ArrayList<PlayerObj> players, PlayerObj dealer) {
        this(players, dealer, Manager.gameId++);
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

    @Override
    public String toString() {
        String str = "\nGame " + getId() + ": \n"
                + String.format("\t%3s: %-15s %-15s %-6s %-6s %-6s\n", "i", "name", "address",
                        "m-port",
                        "r-port", "p-port")
                + String.format("\t%3d: %-15s %-15s %-6d %-6d %-6d (Dealer)\n",
                        1, getDealer().getName(),
                        getDealer().getAddress(), getDealer().getM_port(),
                        getDealer().getR_port(), getDealer().getP_port());

        int i = 2;
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