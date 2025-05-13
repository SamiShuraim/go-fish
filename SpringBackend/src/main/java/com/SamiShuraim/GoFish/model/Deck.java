package com.SamiShuraim.GoFish.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Deck {
    private ArrayList<Card> deck = new ArrayList<>();
    private static final String[] SUITS = { "D", "C", "H", "S" };
    private static final String[] RANKS = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };

    public Deck(int seed) {
        deck.clear();
        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(new Card(suit, rank));
            }
        }
        shuffleCards(seed);
    }

    public Deck(ArrayList<Card> deck) {
        this.deck = deck;
    }

    public void shuffleCards(int seed) {
        Collections.shuffle(deck, new Random(seed));
    }

    public Card draw() {
        if (deck.isEmpty()) {
            return null;
        }
        return deck.remove(0);
    }

    @Override
    public String toString() {
        return String.valueOf(deck.size());
    }
} 