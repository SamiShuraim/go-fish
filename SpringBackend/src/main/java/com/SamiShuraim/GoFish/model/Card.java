package com.SamiShuraim.GoFish.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Card {
    private String suit;
    private String rank;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Card(String x) {
        x = x.strip().trim();
        this.suit = x.substring(0, 1);
        this.rank = x.substring(1, x.length());
    }

    @Override
    public String toString() {
        return suit + rank;
    }
} 