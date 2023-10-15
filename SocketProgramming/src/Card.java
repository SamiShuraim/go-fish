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

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public String toString() {
        return suit + rank;
    }
}
