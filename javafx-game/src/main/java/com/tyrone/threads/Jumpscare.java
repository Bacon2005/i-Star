package com.tyrone.threads;

import java.util.concurrent.ThreadLocalRandom;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Jumpscare extends Thread {

    private volatile boolean gameRunning = true;
    private ImageView jumpscareImg;
    private volatile boolean isPlaying = false;

    public Jumpscare(ImageView jumpscareImg) {
        this.jumpscareImg = jumpscareImg;
        setDaemon(true); // this makes the thread stop automatically on app exit
    }

    public void stopGame() {
        gameRunning = false; // safely signal thread to stop
    }

    private void jumpscare1() {

        if (isPlaying) {
            return;
        }
        isPlaying = true;
        Platform.runLater(() -> {
            Image img = new Image(getClass().getResource("/images/Jumpscare/scare1.png").toExternalForm());
            jumpscareImg.setImage(img);

            Audio.stopBackgroundMusic();
            Audio.playJumpscareSound1();

            jumpscareImg.setVisible(true);

            PauseTransition delay = new PauseTransition(Duration.millis(1000)); // delay
            delay.setOnFinished(e -> {
                jumpscareImg.setVisible(false);
                Audio.playBackgroundMusic();
                isPlaying = false;
            });
            delay.play();
        });
    }

    private void jumpscare2() {
        if (isPlaying) {
            return;
        }
        isPlaying = true;

        Platform.runLater(() -> {
            Image img = new Image(getClass().getResource("/images/Jumpscare/Eminem.png").toExternalForm());
            jumpscareImg.setImage(img);
            jumpscareImg.setVisible(true);

            Audio.stopBackgroundMusic();
            Audio.playJumpscareSound2();

            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(e -> {
                // AFTER 1 second → switch to white
                Image img2 = new Image(getClass().getResource("/images/Jumpscare/white.png").toExternalForm());
                jumpscareImg.setImage(img2);

                PauseTransition delay2 = new PauseTransition(Duration.seconds(5));
                delay2.setOnFinished(ev -> {
                    jumpscareImg.setVisible(false);
                    Audio.playBackgroundMusic();

                });
                delay2.play();
                isPlaying = false;
            });

            delay.play();
        });
    }

    @Override
    public void run() {
        while (gameRunning) {
            System.out.println("Game is running");
            int random = ThreadLocalRandom.current().nextInt(1, 101);
            int randomScare = ThreadLocalRandom.current().nextInt(1, 4);
            // int testScare = 2;
            try {
                Thread.sleep(1000); // prevent CPU spinning
                if (random >= 99) { // 1 in 100 chance of having a jumpscare every second
                    switch (randomScare) {
                        case 1:
                            jumpscare1();
                            break;
                        case 2:
                            jumpscare2();
                            break;
                        case 3:
                            jumpscare1();
                            break;
                        default:
                            break;
                    }
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
