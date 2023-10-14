import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private static ArrayList<Card> deck = new ArrayList<Card>();
    private static String[] suits = {"D","C","H","S"};
    private static String[] ranks = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};

    private static class Card{
        private String suit;
        private String rank;
        
        public Card(String suit, String rank){
            this.suit = suit;
            this.rank = rank;
        }

        public String toString(){
            return suit + rank;
        }
    }

    public static void main(String[] args) {
        Deck deck = new Deck();
        System.out.println(deck);
        shuffleCards();
        System.out.println(deck);
        System.out.println(deck.draw());
        System.out.println(deck);
    }
    
    public Deck(){
        deck.clear();
        for(int i = 0; i < suits.length; i++){
            for(int j = 0; j < ranks.length; j++){
                deck.add(new Card(suits[i], ranks[j]));
            }
        }
    }

    public static void shuffleCards(){
        Collections.shuffle(deck);
    }

    public Card draw(){
        return deck.remove(0);
    }

    public String toString(){
        System.out.println(deck);
        return String.valueOf(deck.size());
    }


}
