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

}
