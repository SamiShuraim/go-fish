package com.SamiShuraim.GoFish.model;

import java.util.ArrayList;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GameObj {
    private String id;
    private ArrayList<PlayerObj> players; // This should include the dealer
    private PlayerObj dealer;
    private Deck deck;
    private int seed;
    private int bookCounter;
    private String status = "In Progress"; // In Progress, Completed, etc.

    public GameObj(ArrayList<PlayerObj> players, PlayerObj dealer, int id, int seed) {
        this.id = String.valueOf(id);
        this.players = players;
        this.dealer = dealer;
        this.seed = seed;
        this.deck = new Deck(seed);
        this.bookCounter = 0;
    }

    public GameObj(ArrayList<PlayerObj> players, PlayerObj dealer, int seed) {
        this.id = UUID.randomUUID().toString();
        this.players = players;
        this.dealer = dealer;
        this.seed = seed;
        this.deck = new Deck(seed);
        this.bookCounter = 0;
    }

    public void setDeck(ArrayList<Card> x) {
        this.deck = new Deck(x);
    }

    public void checkBooks(boolean bool) {
        if (bool)
            this.bookCounter++;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("\nGame " + getId() + ": \n"
                + String.format("\t%3s: %-15s %-15s %-6s %-6s %-6s\n", "i", "name", "address",
                        "m-port",
                        "r-port", "p-port"));

        int i = 1;
        for (PlayerObj player : getPlayers()) {
            str.append(String.format("\t%3d: %-15s %-15s %-6d %-6d %-6d\n",
                    i, player.getName(),
                    player.getAddress(), player.getM_port(),
                    player.getR_port(), player.getP_port()));
            i++;
        }
        return str.toString();
    }
} 