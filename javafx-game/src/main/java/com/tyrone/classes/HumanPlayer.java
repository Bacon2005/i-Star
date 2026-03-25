package com.tyrone.classes;

import com.tyrone.Exceptions.DeckEmptyException;
import com.tyrone.Exceptions.InvalidBetException;

public class HumanPlayer extends Player {
    protected double balance;

    public HumanPlayer(String name) {
        super(name);
        this.balance = 0.0;
    }

    @Override
    public void playTurn(Deck deck) {
        // For human, we wait for button clicks (Hit / Stand)
        // So this method can be empty or just mark turn active
        turnActive = true;
        // The UIController will handle player decisions
    }

    public void hit(Deck deck) throws DeckEmptyException {
        if (turnActive) {
            Card card = deck.drawCard();
            receiveCard(card);
            System.out.println(name + " received " + card);
        }
    }

    public void stand() {
        turnActive = false;
        System.out.println(name + " stands.");
    }

    // The player used his balance to bet
    public void playerBet(double bet) throws InvalidBetException {
        if (bet > balance) {
            throw new InvalidBetException("Insufficient Bet");
        } else {
            balance = balance - bet;
        }
    }

    public void startBalance(double amount) {
        this.balance = amount;
    }

    public void updateBalance(double amountWon) {
        this.balance = balance + amountWon;
    }

    public double getBalance() {
        return balance;
    }
}