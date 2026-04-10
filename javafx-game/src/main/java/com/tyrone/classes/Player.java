package com.tyrone.classes;

public abstract class Player {
    protected Hand hand;
    protected String name;
    protected boolean turnActive;

    public Player(String name) {
        this.name = name;
        this.hand = new Hand();
        this.turnActive = false;
    }
    
    public Hand getHand() {
        return hand;
    }

    public void resetHand() {
        hand.clear();
        turnActive = false;
    }

    public void receiveCard(Card card) {
        hand.addCard(card);
    }

    public boolean isBust() {
        return hand.isBust();
    }

    public boolean hasBlackjack() {
        return hand.calculateValue() == 21 && hand.getCards().size() == 2;
    }

    // Abstract method: each type of player implements their turn
    public abstract void playTurn(Deck deck);
}
