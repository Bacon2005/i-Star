package com.tyrone.controller;

import java.io.IOException;

import com.tyrone.threads.Audio;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class MenuController {
    @FXML
    private ImageView welcomeImg;
    @FXML
    private ImageView waiterImg;
    @FXML
    private Button enterBtn;
    @FXML
    private AnchorPane daveDialouge;
    @FXML
    private AnchorPane welcomePane;
    @FXML
    private AnchorPane gamePane;
    @FXML
    private Label waiterText;
    @FXML
    private Button continueBtn;
    @FXML
    private Button yesBtn;
    @FXML
    private Button noBtn;
    @FXML
    private ImageView miniDave;

    private Integer currentText = 0;

    private double playerMoney;

    private static final Image disgustedDave = new Image(
            GameController.class.getResource("/images/daveDisgusted2.png").toExternalForm());

    private static final Image rulesDave = new Image(
            GameController.class.getResource("/images/DaveRules.png").toExternalForm());

    private static final Image drinksDave = new Image(
            GameController.class.getResource("/images/drinksDave.png").toExternalForm());

    private static final Image defaultDave = new Image(
            GameController.class.getResource("/images/DaveInstructions.png").toExternalForm());

    String[] waiterDialouge = {
            "Welcome to i-Star",
            "Would you like a drink?",
            "Ugh, Very ungrateful. Heres 1k instead of my usual 5k to new guests.",
            "Here you go! Also heres 5k on the house",
            "Only one Rule here",
            "Don't be broke!!! or else...",
            "GoodLuck!" };

    @FXML
    private void initialize() {
        Audio.playMenuBackgroundMusic();
    }

    private void skipIntro() {
        Audio.playMenuBackgroundMusic();
        welcomePane.setVisible(false);
        daveDialouge.setVisible(false);
        gamePane.setVisible(true);
    }

    @FXML
    private void enterBtn() {
        Audio.lowerMenuMusic();
        welcomePane.setVisible(false);
        daveDialouge.setVisible(true);
        talkToDave();
    }

    private void talkToDave() {
        if (currentText == 0) {
            Audio.playDaveGreeting();
            waiterImg.setImage(defaultDave);
            waiterText.setText(waiterDialouge[currentText]);
        } else if (currentText == 1) {
            Audio.playDaveTalking();
            waiterText.setText(waiterDialouge[currentText]);
            yesBtn.setVisible(true);
            noBtn.setVisible(true);
            continueBtn.setVisible(false);
        } else if (currentText == 2) {
            Audio.playMoneySound();
            Audio.playDaveAngry();
            playerMoney = 1000.00;
            miniDave.setImage(disgustedDave);
            waiterImg.setImage(disgustedDave);
            waiterText.setText(waiterDialouge[currentText]);
        } else if (currentText == 3) {
            Audio.playMoneySound();
            Audio.playDaveHappy();
            playerMoney = 5000.00;
            miniDave.setImage(drinksDave);
            waiterImg.setImage(drinksDave);
            waiterText.setText(waiterDialouge[currentText]);
        } else if (currentText == 6) {
            Audio.playDaveGreeting();
            miniDave.setImage(defaultDave);
            waiterImg.setImage(defaultDave);
            waiterText.setText(waiterDialouge[currentText]);
        } else if (currentText == 7) {
            daveDialouge.setVisible(false);
            gamePane.setVisible(true);
            Audio.louderMenuMusic();
        } else {
            Audio.playDaveTalking();
            miniDave.setImage(rulesDave);
            waiterImg.setImage(rulesDave);
            waiterText.setText(waiterDialouge[currentText]);

        }
    }

    @FXML
    private void continueText() {
        if (currentText == 2) {
            currentText = 4;
        } else {
            currentText++;
        }
        talkToDave();
        System.out.println(currentText);
    }

    @FXML
    private void yesText() {
        currentText = 3;
        miniDave.setImage(drinksDave);
        waiterImg.setImage(drinksDave);
        talkToDave();
        yesBtn.setVisible(false);
        noBtn.setVisible(false);
        continueBtn.setVisible(true);
        System.out.println(currentText);
    }

    @FXML
    private void noText() {
        currentText++;
        talkToDave();
        yesBtn.setVisible(false);
        noBtn.setVisible(false);
        continueBtn.setVisible(true);
        System.out.println(currentText);
    }

    @FXML
    private void goToBlackjack(ActionEvent event) throws IOException {
        Audio.stopMenuBackgroundMusic();
        SceneController.switchScene(event, "/fxml/blackjack.fxml", controller -> {
            if (controller instanceof GameController slotsCtrl) {
                slotsCtrl.setPlayerMoney(playerMoney);
            }
        });
    }

    @FXML
    private void goToSlots(ActionEvent event) throws IOException {
        Audio.stopMenuBackgroundMusic();
        SceneController.switchScene(event, "/fxml/slots.fxml", controller -> {
            if (controller instanceof SlotsGameController slotsCtrl) {
                slotsCtrl.setPlayerMoney(playerMoney);
            }
        });
    }

    public void setVisited(double money) {
        skipIntro();
        Audio.stopBackgroundMusic();
        playerMoney = money;
    }
}
