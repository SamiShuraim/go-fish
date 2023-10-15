import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class PlayerObj {
    final private String name;
    final private String address;
    final private int m_port, r_port, p_port;
    private ArrayList<GameObj> inGame;
    final private InetAddress inetAddress;
    private ArrayList<Card> hand = new ArrayList<Card>();
    private ArrayList<Card>[] basket;

    public PlayerObj(String name, String address, int m_port, int r_port, int p_port) throws UnknownHostException {
        this.name = name;
        this.address = address;
        this.m_port = m_port;
        this.r_port = r_port;
        this.p_port = p_port;
        this.inetAddress = InetAddress.getByName(address);
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public InetAddress getInetAddress() {
        return this.inetAddress;
    }

    public int getM_port() {
        return this.m_port;
    }

    public int getR_port() {
        return this.r_port;
    }

    public int getP_port() {
        return this.p_port;
    }

    public ArrayList<Card> getHand() {
        return this.hand;
    }

    public ArrayList<Card>[] getBasket() {
        return this.basket;
    }

    public ArrayList<GameObj> getInGame() {
        return this.inGame;
    }

    public void setInGame(ArrayList<GameObj> inGame) {
        this.inGame = inGame;
    }

    public String showHand() {
        String str = "";
        for (int i = 0; i < this.hand.size(); i++) {
            str += this.hand.get(i) + ", ";
        }
        return str.substring(0, str.length() - 2) + '\n';
    }

    public void deleteCard(String s) {
        for (Card c : hand) {
            if (c.toString().equals(s.trim())) {
                hand.remove(c);
                return;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        /*
         * Returns true if new user
         * has the same name as another user or
         * has the same address and port number of either m, r, or p.
         * With the way we designed the port assignment, this comparison will suffice to
         * catch any invalid port numbers.
         */
        if (!(o instanceof PlayerObj))
            return false;
        PlayerObj obj = (PlayerObj) o;
        return name.equals(obj.getName()) || ((address.equals(obj.getAddress())
                && (m_port == obj.getM_port() || m_port == obj.getR_port() || m_port == obj.getP_port()
                        || p_port == obj.getM_port() || p_port == obj.getR_port())));
    }

    @Override
    public String toString() {
        return String.format("%-15s %-15s %-6d %-6d %-6d\n", getName(),
                getAddress(), getM_port(), getR_port(), getP_port());
    }
}
