public class PlayerObj {
    final private String name;
    final private String address;
    final private int m_port, r_port, p_port;

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
        return name.equals(obj.name) || (address.equals(obj.address)
                && (m_port == obj.getM_port() || r_port == obj.getR_port() || p_port == obj.getP_port()));
    }

}
