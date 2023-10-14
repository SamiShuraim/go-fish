import java.util.ArrayList;

public class GameObj {
    private final int id;
    private final ArrayList<PlayerObj> players;
    private final PlayerObj dealer;

    public GameObj(ArrayList<PlayerObj> players, PlayerObj dealer) {
        this.id = Manager.gameId++;
        this.players = players;
        this.dealer = dealer;
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