package com.tyrone.classes;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private final ArrayList<Card> cards = new ArrayList<>();

    public Hand() {

    }

    // Add a card to the hand
    public void addCard(Card card) {
        cards.add(card);
    }

    // Get all cards in hand
    public List<Card> getCards() {
        return cards;
    }

    // Clear hand for new round
    public void clear() {
        cards.clear();
    }

    public int calculateValue() {
        int total = 0;
        int aceCount = 0;
        for (Card card : cards) {
            total += card.getValue();
            if (card.getRank() == Card.Rank.ACE) {
                aceCount++;
            }
        }

        while (total > 21 && aceCount > 0) {
            total -= 10; // Ace counts as 1 instead of 11
            aceCount--;
        }

        return total;
    }

    public boolean isBust() {
        if (calculateValue() > 21) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return cards.toString() + " (" + calculateValue() + ")";
    }
}
