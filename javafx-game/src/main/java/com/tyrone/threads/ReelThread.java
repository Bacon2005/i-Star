package com.tyrone.threads;

import java.util.List;

import com.tyrone.classes.Symbol;

import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class ReelThread extends Thread {

    private ImageView reelView;
    private List<Symbol> symbols;
    private Symbol result;
    private int spinCycles;

    public ReelThread(ImageView reelView, List<Symbol> symbols, int spinCycles) {
        this.reelView = reelView;
        this.symbols = symbols;
        this.spinCycles = spinCycles;
    }

    public Symbol getResult() {
        return result;
    }

    private Symbol getRandomSymbol() {
        int totalWeight = 0;

        for (Symbol s : symbols)
            totalWeight += s.getWeight();

        int random = (int) (Math.random() * totalWeight);

        int current = 0;
        for (Symbol s : symbols) {
            current += s.getWeight();
            if (random < current)
                return s;
        }

        return symbols.get(0);
    }

    @Override
    public void run() {
        try {
            // spin based on cycles
            for (int i = 0; i < spinCycles; i++) {
                Symbol temp = getRandomSymbol();

                Platform.runLater(() -> {
                    reelView.setImage(temp.getImage());
                });

                Thread.sleep(100);
            }

            Audio.playSlotHit();
            // final result
            result = getRandomSymbol();

            Platform.runLater(() -> {
                reelView.setImage(result.getImage());
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}