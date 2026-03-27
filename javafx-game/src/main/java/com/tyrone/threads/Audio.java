package com.tyrone.threads;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer; //A thread
import javafx.util.Duration;

public class Audio {

    private static MediaPlayer bgMusic;

    private static MediaPlayer bgMenuMusic;

    // private static final AudioClip bgMenuMusic = new AudioClip(
    // Audio.class.getResource("/audio/RightRound.mp3").toExternalForm());;

    private static final double TARGET_VOLUME = 0.2;

    private static final AudioClip winSound = new AudioClip(Audio.class.getResource("/audio/win.mp3").toExternalForm());

    private static final AudioClip loseSound = new AudioClip(
            Audio.class.getResource("/audio/lose.mp3").toExternalForm());

    private static final AudioClip jumpScarePaul = new AudioClip(
            Audio.class.getResource("/audio/jumpscare.mp3").toExternalForm());

    private static final AudioClip jumpScareSound1 = new AudioClip(
            Audio.class.getResource("/audio/scery2.mp3").toExternalForm());

    private static final AudioClip jumpScareSound2 = new AudioClip(
            Audio.class.getResource("/audio/Think Fast.mp3").toExternalForm());

    private static final AudioClip money = new AudioClip(
            Audio.class.getResource("/audio/cha ching.mp3").toExternalForm());

    private static final AudioClip slotSpin = new AudioClip(
            Audio.class.getResource("/audio/slotSpinning.mp3").toExternalForm());

    private static final AudioClip slotResult = new AudioClip(
            Audio.class.getResource("/audio/slotResult.mp3").toExternalForm());

    private static final AudioClip slotHit = new AudioClip(
            Audio.class.getResource("/audio/hit.mp3").toExternalForm());

    private static final AudioClip slotLose = new AudioClip(
            Audio.class.getResource("/audio/aww.mp3").toExternalForm());

    private static final AudioClip jumpScareSound3 = new AudioClip(
            Audio.class.getResource("/audio/jumpscare3.mp3").toExternalForm());

    private static final AudioClip daveTalking = new AudioClip(
            Audio.class.getResource("/audio/talking/talking.mp3").toExternalForm());

    private static final AudioClip daveGreeting = new AudioClip(
            Audio.class.getResource("/audio/talking/greeting.mp3").toExternalForm());

    private static final AudioClip daveAngry = new AudioClip(
            Audio.class.getResource("/audio/talking/angry.mp3").toExternalForm());

    private static final AudioClip daveHappy = new AudioClip(
            Audio.class.getResource("/audio/talking/happy.mp3").toExternalForm());

    // DAVE TALKING
    public static void playDaveTalking() {
        daveTalking.play();
    }

    public static void playDaveGreeting() {
        daveGreeting.play();
    }

    public static void playDaveHappy() {
        daveHappy.play();
    }

    public static void playDaveAngry() {
        daveAngry.play();
    }

    // SOUND EFFECT
    public static void playWinSound() {
        winSound.play();
        money.play();
    }

    public static void playMoneySound() {
        money.play();
    }

    public static void playSlotLose() {
        slotLose.play();
    }

    public static void playSlotResult() {
        slotResult.play();
    }

    public static void playSlotHit() {
        slotHit.play();
    }

    // SLOTS
    public static void playLoseSound() {
        loseSound.play();
    }

    public static void playSlotSpin() {
        slotSpin.play();
        slotSpin.setCycleCount(AudioClip.INDEFINITE);
    }

    public static void stopSpinSound() {
        slotSpin.stop();
    }

    // MENU BACKGROUND MUSIC
    public static void playMenuBackgroundMusic() {
        initMenuMusic();

        bgMenuMusic.setVolume(0);
        bgMenuMusic.play();

        fadeTo(bgMenuMusic, TARGET_VOLUME, 3); // fade in 3 seconds
    }

    public static void stopMenuBackgroundMusic() {
        if (bgMenuMusic == null)
            return;

        bgMenuMusic.stop();
    }

    public static void lowerMenuMusic() {
        if (bgMenuMusic == null)
            return;

        fadeTo(bgMenuMusic, 0.05, 1.5); // fade to very low volume
    }

    public static void louderMenuMusic() {
        if (bgMenuMusic == null)
            return;

        fadeTo(bgMenuMusic, TARGET_VOLUME, 1.5); // fade to very low volume
    }

    // BACKGROUND MUSIC SETUP
    private static void initMusic() {
        if (bgMusic != null)
            return;

        Media media = new Media(
                Audio.class.getResource("/audio/casinoMusic.mp3").toExternalForm());

        bgMusic = new MediaPlayer(media);
        bgMusic.setCycleCount(MediaPlayer.INDEFINITE);
    }

    private static void initMenuMusic() {
        if (bgMenuMusic != null)
            return;

        Media media = new Media(
                Audio.class.getResource("/audio/RightRound.mp3").toExternalForm());

        bgMenuMusic = new MediaPlayer(media);
        bgMenuMusic.setCycleCount(MediaPlayer.INDEFINITE);
    }

    public static void playBackgroundMusic() {
        initMusic();

        bgMusic.setVolume(0);
        bgMusic.play();

        fadeTo(bgMusic, TARGET_VOLUME, 3); // fade in 3 seconds
    }

    public static void pauseBackgroundMusic() {
        if (bgMusic == null)
            return;

        fadeTo(bgMusic, 0, 1.5, () -> bgMusic.pause());
    }

    public static void resumeBackgroundMusic() {
        if (bgMusic == null)
            return;

        bgMusic.setVolume(0);
        bgMusic.play();

        fadeTo(bgMusic, TARGET_VOLUME, 1.5);
    }

    public static void stopBackgroundMusic() {
        if (bgMusic == null)
            return;
        bgMusic.stop();
    }

    private static void fadeTo(MediaPlayer player, double targetVolume, double seconds) {
        fadeTo(player, targetVolume, seconds, null);
    }

    private static void fadeTo(MediaPlayer player, double targetVolume, double seconds, Runnable onFinished) {
        if (player == null)
            return;

        Timeline fade = new Timeline(
                new KeyFrame(Duration.seconds(seconds),
                        new KeyValue(player.volumeProperty(), targetVolume)));

        if (onFinished != null) {
            fade.setOnFinished(e -> onFinished.run());
        }

        fade.play();
    }

    public static void playJumpscareSound() {
        jumpScarePaul.setVolume(1);
        jumpScarePaul.play();
    }

    public static void playJumpscareSound1() {
        jumpScareSound1.setVolume(1);
        jumpScareSound1.play();
    }

    public static void playJumpscareSound2() {
        jumpScareSound2.setVolume(1);
        jumpScareSound2.play();
    }

    public static void playJumpscareSound3() {
        jumpScareSound3.setVolume(1);
        jumpScareSound3.play();
    }
}