import java.util.ArrayList;

public class PlayerObj {
    final private String name;
    final private String address;
    final private int m_port, r_port, p_port;
    private ArrayList<GameObj> inGame;

    public PlayerObj(String name, String address, int m_port, int r_port, int p_port) {
        this.name = name;
        this.address = address;
        this.m_port = m_port;
        this.r_port = r_port;
        this.p_port = p_port;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
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

    public ArrayList<GameObj> getInGame() {
        return this.inGame;
    }

    public void setInGame(ArrayList<GameObj> inGame) {
        this.inGame = inGame;
    }

    @Override
    public boolean equals(Object o) {
        /*
         * Returns true if new user
         * has the same name as another user or
         * has the same address and port number of either m, r, or p.
         */
        if (!(o instanceof PlayerObj))
            return false;
        PlayerObj obj = (PlayerObj) o;
        return name.equals(obj.getName()) || ((address.equals(obj.getAddress())
                && (m_port == obj.getM_port() || r_port == obj.getR_port() || p_port == obj.getP_port())));
    }

    @Override
    public String toString() {
        return String.format("%-15s %-15s %-6d %-6d %-6d\n", getName(),
                getAddress(), getM_port(), getR_port(), getP_port());
    }
}
