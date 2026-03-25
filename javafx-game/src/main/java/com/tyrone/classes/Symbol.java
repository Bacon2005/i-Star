package com.tyrone.classes;

import javafx.scene.image.Image;

public class Symbol {
    private String name;
    private int weight;
    private int payout;
    private Image image;

    public Symbol(String name, int weight, int payout, String imagePath) {
        this.name = name;
        this.weight = weight;
        this.payout = payout;
        this.image = new Image(getClass().getResource(imagePath).toExternalForm());
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public int getPayout() {
        return payout;
    }

    public Image getImage() {
        return image;
    }
}
