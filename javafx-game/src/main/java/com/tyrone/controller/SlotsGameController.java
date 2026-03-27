package com.tyrone.controller;

import java.io.IOException;
import java.util.List;

import com.tyrone.Exceptions.InvalidBetException;
import com.tyrone.classes.HumanPlayer;
import com.tyrone.classes.Symbol;
import com.tyrone.threads.Audio;
import com.tyrone.threads.Jumpscare;
import com.tyrone.threads.ReelThread;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import javafx.util.Duration;

public class SlotsGameController {
    private HumanPlayer player;

    @FXML
    private Label loseCount;
    @FXML
    private TextField betField;
    @FXML
    private Label currentBet;
    @FXML
    private Label playerBal;
    @FXML
    private Button betBtn;
    @FXML
    private Button spinBtn;
    @FXML
    private ImageView reel1;
    @FXML
    private ImageView reel2;
    @FXML
    private ImageView reel3;

    @FXML
    private ImageView jumpscareImg;

    private int loseCounter = 0;
    private int MAX_LOSS = 5;

    private double bet = 0;

    private double playerMoney;

    // weight determines the rarity of the symbol. The higher weight the more
    // common.
    // The payout is basically the multiplier of the symbol. EX: 3 Hurv in a row is
    // 2x payout

    List<Symbol> symbols = List.of(
            new Symbol("Hurv", 40, 2, "/images/slots/hurv.jpg"),
            new Symbol("Ian", 30, 5, "/images/slots/ian.jpg"),
            new Symbol("Noah", 20, 10, "/images/slots/noah.jpg"),
            new Symbol("Zeke", 15, 20, "/images/slots/zeke.jpg"),
            new Symbol("James", 10, 30, "/images/slots/james.jpg"),
            new Symbol("Cual", 5, 40, "/images/slots/cual.jpg"),
            new Symbol("Elijah", 2, 500, "/images/slots/elijah.jpg"),
            new Symbol("Rar", 1, 0, "/images/slots/superRare.png"));

    Jumpscare jumpscare;

    public void setPlayerMoney(double money) {
        playerMoney = money;
        System.out.println("Player money received: " + playerMoney);

        // Start game ONLY when money is received
        if (player != null) {
            startGame();
        }
    }

    @FXML
    public void initialize() {
        Audio.playBackgroundMusic();
        player = new HumanPlayer("Player");
        jumpscare = new Jumpscare(jumpscareImg);
        jumpscare.start();
    }

    private void startGame() {
        setupReels();
        player.startBalance(playerMoney);
        updatePlayerBalance();
    }

    private void setupReels() {
        ImageView[] reels = { reel1, reel2, reel3 };

        for (ImageView reel : reels) {
            reel.setFitWidth(250);
            reel.setFitHeight(250);
            reel.setPreserveRatio(false); // important for different sizes

            // crop to square so all look equal
            Rectangle clip = new Rectangle(250, 250);
            reel.setClip(clip);

            reel.setSmooth(true);
        }
    }

    @FXML
    private void handleSpin() {

        if (bet <= 0) {
            System.out.println("Bet smthn");
            return;
        }

        betBtn.setDisable(true);
        spinBtn.setDisable(true);
        // different durations
        ReelThread r1 = new ReelThread(reel1, symbols, 30);
        ReelThread r2 = new ReelThread(reel2, symbols, 40);
        ReelThread r3 = new ReelThread(reel3, symbols, 50);

        r1.start();
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        r2.start();
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        r3.start();
        Audio.playSlotSpin();
        new Thread(() -> {
            try {
                r1.join();
                r2.join();
                r3.join();
                Audio.stopSpinSound();
                checkResult(r1.getResult(), r2.getResult(), r3.getResult());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void updateBet() {
        try {
            bet = Double.parseDouble(betField.getText());
            if (bet <= 0) {
                betField.clear();
                betField.setPromptText("Enter a Positive Number");
                return;
            }
        } catch (NumberFormatException e) {
            betField.clear();
            betField.setPromptText("Enter a Number");
        }

        try {
            player.playerBet(bet);
            updateCurrentBetField(bet);
            // clears the field
            betField.clear();
            // update player Balance
            updatePlayerBalance();
        } catch (InvalidBetException e) {
            System.out.println("Exception Caught: " + e.getMessage());
        }
    }

    private void checkResult(Symbol s1, Symbol s2, Symbol s3) {

        Platform.runLater(() -> {

            double winnings = 0;
            Symbol matchedSymbol = null;

            if (s1.getName() == "Rar" || s2.getName() == "Rar" || s3.getName() == "Rar") {
                jumpScare();
                betBtn.setDisable(false);
                spinBtn.setDisable(false);
                return;
            }

            // 3-match
            if (s1.getName().equals(s2.getName()) && s2.getName().equals(s3.getName())) {
                winnings = bet * s1.getPayout();
                System.out.println("3 IN A ROW WIN");
                Audio.playSlotResult();
            }
            // 2-match
            else {
                // detect which 2 symbols matched
                if (s1.getName().equals(s2.getName()))
                    matchedSymbol = s1;
                else if (s2.getName().equals(s3.getName()))
                    matchedSymbol = s2;
                else if (s1.getName().equals(s3.getName()))
                    matchedSymbol = s1;

                if (matchedSymbol != null) { // only use if not null
                    double twoMatchMultiplier;
                    if (matchedSymbol.getPayout() >= 20)
                        twoMatchMultiplier = 0.75;
                    else
                        twoMatchMultiplier = 0.5;

                    winnings = bet * matchedSymbol.getPayout() * twoMatchMultiplier;
                    Audio.playSlotResult();
                }
            }

            if (winnings > 0) {
                player.updateBalance(winnings);
                updatePlayerBalance();
                System.out.println("WIN: " + winnings);
            } else {
                loseCounter(); // no match

                if (loseCounter == MAX_LOSS || player.getBalance() == 0) {
                    jumpscareExitOnLose();
                }
                Audio.playSlotLose();
                System.out.println("LOSE");
            }
            bet = 0;
            updateCurrentBetField(bet);
            betBtn.setDisable(false);
            spinBtn.setDisable(false);
        });
    }

    @FXML
    private void updatePlayerBalance() {
        playerBal.setText("Your Balance: " + player.getBalance());
    }

    @FXML
    private void updateCurrentBetField(double bet) {
        currentBet.setText("Current Bet: " + bet);
    }

    @FXML
    private void loseCounter() {
        loseCounter++;
        loseCount.setText("Lose Count: " + loseCounter);
    }

    private void jumpscareExitOnLose() {
        jumpScare();
        PauseTransition delay = new PauseTransition(Duration.millis(3000));
        delay.setOnFinished(e -> {
            Platform.exit();
            System.exit(0);
        });
        delay.play();
    }

    private static final Image whiteFace = new Image(
            Jumpscare.class.getResource("/images/Jumpscare/whiteFace.jpg").toExternalForm());

    @FXML
    private void jumpScare() {
        System.out.println("Jumpscare");

        jumpscareImg.setImage(whiteFace);
        Audio.stopBackgroundMusic();
        jumpscareImg.setVisible(true);
        Audio.playJumpscareSound3();
        PauseTransition delay = new PauseTransition(Duration.millis(3000)); // delay
        delay.setOnFinished(e -> {
            jumpscareImg.setVisible(false);
            Audio.playBackgroundMusic();
        });
        delay.play();
    }

    @FXML
    private void allIn() {
        bet = player.getBalance();
        try {
            player.playerBet(bet);
            updateCurrentBetField(bet);
            betField.clear();
            updatePlayerBalance();
        } catch (InvalidBetException e) {
            System.out.println("Exception Caught: " + e.getMessage());
        }
    }

    // Switch back to homepage
    @FXML
    private void chooseGame(ActionEvent event) throws IOException {
        jumpscare.stopGame();
        SceneController.switchScene(event, "/fxml/menu.fxml", controller -> {
            if (controller instanceof MenuController menuCtrl) {
                menuCtrl.setVisited(player.getBalance());
            }
        });
    }
}
