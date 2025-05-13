package com.SamiShuraim.GoFish.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerObj {
    private String id;
    private String name;
    private String address;
    private int m_port, r_port, p_port;
    private ArrayList<GameObj> inGame;
    private InetAddress inetAddress;
    private ArrayList<Card> hand = new ArrayList<>();
    private ArrayList<ArrayList<Card>> basket = new ArrayList<>();
    private String status = "Available"; // Available, In Game, etc.

    public PlayerObj(String name, String address, int m_port, int r_port, int p_port) throws UnknownHostException {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
        this.m_port = m_port;
        this.r_port = r_port;
        this.p_port = p_port;
        this.inetAddress = InetAddress.getByName(address);
    }

    public String showHand() {
        if (hand.isEmpty()) {
            return "Empty hand";
        }
        
        StringBuilder str = new StringBuilder();
        for (Card card : this.hand) {
            str.append(card).append(", ");
        }
        return str.substring(0, str.length() - 2) + '\n';
    }

    public String showBasket() {
        if (basket.isEmpty()) {
            return "No books";
        }
        
        StringBuilder str = new StringBuilder();
        for (ArrayList<Card> book : this.basket) {
            for (Card c : book) {
                str.append(c.toString()).append(" ");
            }
            str.append("\n");
        }

        return str.toString();
    }

    public void addToBasket(String rank) {
        ArrayList<Card> res = new ArrayList<>();
        String temp = String.format("D%s C%s, H%s S%s", rank, rank, rank, rank);
        for (String cardStr : temp.split(" ")) {
            res.add(new Card(cardStr));
        }
        basket.add(res);
    }

    public boolean checkHand(String rank) {
        ArrayList<Card> tmp = new ArrayList<>();
        for (Card c : this.hand) {
            if (c.getRank().equals(rank.trim()))
                tmp.add(c);
        }
        if (tmp.size() == 4) {
            this.deleteCardByRank(rank);
            this.basket.add(tmp);
            return true;
        }
        return false;
    }

    public void deleteCard(String s) {
        for (Card c : hand) {
            if (c.toString().equals(s.trim())) {
                hand.remove(c);
                return;
            }
        }
    }

    public void deleteCardByRank(String rank) {
        ArrayList<Card> temp = new ArrayList<>();
        for (Card c : hand) {
            if (c.getRank().equals(rank)) {
                temp.add(c);
            }
        }

        for (Card c : temp) {
            hand.remove(c);
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