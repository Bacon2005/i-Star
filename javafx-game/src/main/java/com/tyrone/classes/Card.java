package com.tyrone.classes;

//Create Card Object
public class Card {
    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    public enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),
        EIGHT(8), NINE(9), TEN(10), JACK(10), QUEEN(10), KING(10), ACE(11);

        private final int value;

        Rank(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getValue() {
        return rank.getValue();
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    public String getImageFileName() {

        String suitName = switch (suit) {
            case HEARTS -> "Hearts";
            case DIAMONDS -> "Diamond";
            case CLUBS -> "Clubs";
            case SPADES -> "Spade";
        };

        String rankName = switch (rank) {
            case ACE -> "Ace";
            case KING -> "King";
            case QUEEN -> "Queen";
            case JACK -> "Jack";
            case TEN -> "10";
            case NINE -> "9";
            case EIGHT -> "8";
            case SEVEN -> "7";
            case SIX -> "6";
            case FIVE -> "5";
            case FOUR -> "4";
            case THREE -> "3";
            case TWO -> "2";
        };

        return suitName + rankName + ".jpg";
    }
}
