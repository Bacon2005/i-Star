package com.tyrone.classes;

import java.util.ArrayList;
import java.util.Collections;

import com.tyrone.Exceptions.DeckEmptyException;

public class Deck {
    ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        populateDeck();
        shuffle();
    }

    // Fills the deck with the 52 cards
    private void populateDeck() {
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }

        System.out.println("Deck is created");
    }

    // shuffles the deck
    private void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() throws DeckEmptyException {
        if (!cards.isEmpty()) {
            return cards.remove(cards.size() - 1);
        }
        throw new DeckEmptyException("Deck is Empty!");
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int remainingCards() {
        return cards.size();
    }
}
