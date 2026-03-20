package com.tyrone.threads;

import com.tyrone.classes.Card;
import com.tyrone.classes.Deck;
import com.tyrone.classes.Player;

import javafx.application.Platform;

public class Dealer extends Player {

    public interface DealerListener {
        void onDealerDraw(Card card);

        void onDealerFinished();
    }

    public DealerListener listener;

    public Dealer() {
        super("Dealer");
    }

    @Override
    public void playTurn(Deck deck) {

        new Thread(() -> {

            try {

                while (hand.calculateValue() < 17) {

                    Thread.sleep(1000);

                    Card card = deck.drawCard();
                    hand.addCard(card);

                    if (listener != null) {
                        Platform.runLater(() -> listener.onDealerDraw(card));
                    }
                }

                if (listener != null) {
                    Platform.runLater(() -> listener.onDealerFinished());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }
}
