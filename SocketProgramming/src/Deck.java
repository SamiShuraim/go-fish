import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Deck {
    private static ArrayList<Card> deck = new ArrayList<Card>();
    private static String[] suits = { "D", "C", "H", "S" };
    private static String[] ranks = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A" };

    public Deck(int seed) {
        deck.clear();
        for (int i = 0; i < suits.length; i++) {
            for (int j = 0; j < ranks.length; j++) {
                deck.add(new Card(suits[i], ranks[j]));
            }
        }
        shuffleCards(seed);
    }

    public Deck(ArrayList<Card> deck) {
        Deck.deck = deck;
    }

    public static void shuffleCards(int seed) {
        // Collections.shuffle(deck);
        Collections.shuffle(deck, new Random(seed));
    }

    public Card draw() {
        return deck.remove(0);
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public String toString() {
        System.out.println(deck);
        return String.valueOf(deck.size());
    }

}
