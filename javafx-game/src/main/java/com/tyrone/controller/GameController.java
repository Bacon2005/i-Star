package com.tyrone.controller;

import com.tyrone.Exceptions.DeckEmptyException;
import com.tyrone.Exceptions.InvalidBetException;
import com.tyrone.classes.Card;
import com.tyrone.classes.Deck;
import com.tyrone.classes.HumanPlayer;
import com.tyrone.threads.Audio;
import com.tyrone.threads.Dealer;
import com.tyrone.threads.Jumpscare;

import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.util.Duration;

public class GameController {

    private Deck deck;
    private HumanPlayer player;
    private Dealer dealer;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private HBox dealerCards;

    @FXML
    private HBox playerCards;

    @FXML
    private Button hitButton;

    @FXML
    private Button standButton;

    @FXML
    private Button newGameButton;

    @FXML
    private Button betBtn;

    @FXML
    private Label statusLabel;

    @FXML
    private Label playerValueLabel;

    @FXML
    private Label dealerValueLabel;

    @FXML
    private ImageView hiddenDealerCard;

    @FXML
    private ImageView dealerPhoto;

    @FXML
    private ImageView jumpscareImg;

    @FXML
    private ImageView cardImg;

    @FXML
    private Label playerBal;

    private Card hiddenDealerCardData;

    @FXML
    private Label loseCount;

    @FXML
    private TextField betField;
    @FXML
    private Label currentBet;

    private int loseCounter = 0;
    private int MAX_LOSS = 5;

    private double bet = 0;

    private double playerMoney;

    Jumpscare jumpscare;
    // Reusable Images

    private static final Image dealerWaiting = new Image(
            GameController.class.getResource("/images/Dealer/waiting.jpg").toExternalForm());

    private static final Image dealerWin = new Image(
            GameController.class.getResource("/images/Dealer/win.jpg").toExternalForm());

    private static final Image dealerLose = new Image(
            GameController.class.getResource("/images/Dealer/lose.jpg").toExternalForm());

    private static final Image dealerTie = new Image(
            GameController.class.getResource("/images/Dealer/tie.jpg").toExternalForm());

    private static final Image dealerJumpscare = new Image(
            GameController.class.getResource("/images/Dealer/Jumpscare.jpg").toExternalForm());

    private static final Map<String, Image> cardImageCache = new HashMap<>();

    private static final Image CARD_BACK = new Image(GameController.class
            .getResource("/images/cards/cardBack.jpg")
            .toExternalForm());

    public void setPlayerMoney(double money) {
        this.playerMoney = money;
        System.out.println("Player money received: " + money);

        if (player != null) {
            player.startBalance(playerMoney);
            startNewGame();
        }
    }

    // Runs automatically when UI loads
    @FXML
    public void initialize() {
        Audio.playBackgroundMusic();
        jumpscare = new Jumpscare(jumpscareImg);
        jumpscare.start();

        player = new HumanPlayer("Player");
    }

    private Image getCardImage(String fileName) {
        return cardImageCache.computeIfAbsent(fileName, name -> new Image(GameController.class
                .getResource("/images/cards/" + name)
                .toExternalForm()));
    }

    // Start a new round
    @FXML
    private void startNewGame() {
        bet = 0;
        updateCurrentBetField(bet);
        player.resetHand();
        Audio.resumeBackgroundMusic();
        cardImg.setDisable(false);

        showDealerImage(dealerWaiting);

        deck = new Deck();
        // player = new HumanPlayer("Player");
        dealer = new Dealer();

        dealerCards.getChildren().clear();
        playerCards.getChildren().clear();

        updatePlayerBalance();

        statusLabel.setText("Game started!");

        try {

            // Player initial cards
            dealCardToPlayer();
            dealCardToPlayer();

            // Dealer initial cards
            dealCardToDealer(false);
            dealCardToDealer(true);

            updateValues();

        } catch (DeckEmptyException e) {
            e.printStackTrace();
        }
        betBtn.setDisable(false);
        hitButton.setDisable(false);
        standButton.setDisable(false);
        newGameButton.setDisable(true);
    }

    // HIT BUTTON
    @FXML
    private void handleHit() {
        // player did not bet anything
        if (bet == 0) {
            System.out.println("please bet something");
        } else {
            try {
                dealCardToPlayer();
                PauseTransition delay = new PauseTransition(Duration.millis(300));
                delay.play();
                updateValues();

                if (player.isBust()) {
                    statusLabel.setText("Player Bust! Dealer wins.");
                    loseCounter();
                    if (loseCounter == MAX_LOSS) {
                        jumpscareExitOnLose();
                    } else if (player.getBalance() == 0) {
                        jumpscareExitOnLose();
                    }
                    showDealerImage(dealerWin);

                    Audio.pauseBackgroundMusic();
                    Audio.playLoseSound();
                    endRound();
                }

            } catch (DeckEmptyException e) {
                e.printStackTrace();
            }
        }
    }

    // STAND BUTTON
    @FXML
    private void handleStand() {
        if (bet == 0) {
            System.out.println("bet somehting");
        } else {
            hitButton.setDisable(true);
            standButton.setDisable(true);

            statusLabel.setText("Dealer's turn...");

            revealDealerCard();

            dealer.listener = new Dealer.DealerListener() {

                @Override
                public void onDealerDraw(Card card) {
                    ImageView view = createCardView(card);
                    animateCardToTarget(dealerCards, view);
                    updateValues();
                }

                @Override
                public void onDealerFinished() {
                    determineWinner();
                }
            };

            dealer.playTurn(deck);
        }
    }

    // Deal card to player
    private void dealCardToPlayer() throws DeckEmptyException {

        Card card = deck.drawCard();
        player.receiveCard(card);

        ImageView cardView = createCardView(card);
        animateCardToTarget(playerCards, cardView);
    }

    // Deal second card to dealer
    private void dealCardToDealer(boolean hidden) throws DeckEmptyException {

        Card card = deck.drawCard();
        dealer.getHand().addCard(card);

        ImageView cardView;

        if (hidden) {
            hiddenDealerCardData = card;

            cardView = new ImageView(CARD_BACK);

            cardView.setFitWidth(90);
            cardView.setFitHeight(130);

            hiddenDealerCard = cardView;
        } else {
            cardView = createCardView(card);
        }

        animateCardToTarget(dealerCards, cardView);
    }

    private void revealDealerCard() {

        Image image = getCardImage(hiddenDealerCardData.getImageFileName());

        hiddenDealerCard.setImage(image);
        hiddenDealerCardData = null;

        updateValues();
    }

    // Create card image
    private ImageView createCardView(Card card) {

        Image image = getCardImage(card.getImageFileName());

        ImageView view = new ImageView(image);
        view.setFitWidth(90);
        view.setFitHeight(130);

        return view;
    }

    // Animate card flying from deck to a target box
    private void animateCardToTarget(HBox targetBox, ImageView finalCardView) {

        Bounds startBounds = cardImg.localToScene(cardImg.getBoundsInLocal());
        Point2D startInRoot = rootPane.sceneToLocal(startBounds.getMinX(), startBounds.getMinY());

        // Pre-place invisibly so HBox centers it correctly, then capture its scene
        // position
        finalCardView.setOpacity(0);
        targetBox.getChildren().add(finalCardView);
        targetBox.applyCss();
        targetBox.layout();
        Bounds finalCardBounds = finalCardView.localToScene(finalCardView.getBoundsInLocal());
        targetBox.getChildren().remove(finalCardView);
        finalCardView.setOpacity(1);

        Point2D destInRoot = rootPane.sceneToLocal(finalCardBounds.getMinX(), finalCardBounds.getMinY());

        ImageView flyingCard = new ImageView(cardImg.getImage());
        flyingCard.setFitWidth(finalCardView.getFitWidth());
        flyingCard.setFitHeight(finalCardView.getFitHeight());
        flyingCard.setManaged(false);
        flyingCard.setLayoutX(startInRoot.getX());
        flyingCard.setLayoutY(startInRoot.getY());

        rootPane.getChildren().add(flyingCard);

        TranslateTransition transition = new TranslateTransition(Duration.millis(400), flyingCard);
        transition.setToX(destInRoot.getX() - startInRoot.getX());
        transition.setToY(destInRoot.getY() - startInRoot.getY());
        transition.setInterpolator(Interpolator.EASE_OUT);
        transition.setOnFinished(e -> {
            rootPane.getChildren().remove(flyingCard);
            targetBox.getChildren().add(finalCardView);
        });
        transition.play();
    }

    // Update hand values
    private void updateValues() {

        playerValueLabel.setText("Player: " + player.getHand().calculateValue());

        if (hiddenDealerCardData != null)
            dealerValueLabel.setText("Dealer: ?");
        else
            dealerValueLabel.setText("Dealer: " + dealer.getHand().calculateValue());
    }

    private void showDealerImage(Image image) {
        dealerPhoto.setImage(image);
        dealerPhoto.setFitWidth(154);
        dealerPhoto.setFitHeight(163);
    }

    // Determine winner
    private void determineWinner() {

        int playerValue = player.getHand().calculateValue();
        int dealerValue = dealer.getHand().calculateValue();

        if (dealer.isBust()) {
            statusLabel.setText("Dealer Bust! Player wins!");

            showDealerImage(dealerLose);

            Audio.pauseBackgroundMusic();
            Audio.playWinSound();

            playerWin();
        } else if (playerValue > dealerValue) {
            statusLabel.setText("Player wins!");
            Audio.pauseBackgroundMusic();
            Audio.playWinSound();

            showDealerImage(dealerLose);

            playerWin();
        } else if (dealerValue > playerValue) {
            statusLabel.setText("Dealer wins!");

            loseCounter();
            if (loseCounter == MAX_LOSS) {
                jumpscareExitOnLose();
            } else if (player.getBalance() == 0) {
                jumpscareExitOnLose();
            }
            showDealerImage(dealerWin);

            Audio.pauseBackgroundMusic();
            Audio.playLoseSound();
        } else {
            statusLabel.setText("Tie");
            if (player.getBalance() == 0) {
                jumpscareExitOnLose();
            }
            showDealerImage(dealerTie);

        }

        endRound();
    }

    // Disable buttons after round
    private void endRound() {
        betBtn.setDisable(true);
        cardImg.setDisable(true);
        hitButton.setDisable(true);
        standButton.setDisable(true);
        newGameButton.setDisable(false);
    }

    @FXML
    private void loseCounter() {
        loseCounter++;
        loseCount.setText("Lose Count: " + loseCounter);
    }

    private void jumpscareExitOnLose() {
        jumpScare();
        PauseTransition delay = new PauseTransition(Duration.millis(300));
        delay.setOnFinished(e -> {
            Platform.exit();
            System.exit(0);
        });
        delay.play();
    }

    @FXML
    private void updatePlayerBalance() {
        playerBal.setText("Your Balance: " + player.getBalance());
    }

    @FXML
    private void updateCurrentBetField(double bet) {
        currentBet.setText("Current Bet: " + bet);
    }

    // OK button will call this and get the amount inside the betField
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

    private void playerWin() {
        double amountWon = bet * 2; // dealer gives times the amount depending how much the player bet
        player.updateBalance(amountWon);
        updatePlayerBalance();
    }

    // easter egg jumpscare
    @FXML
    private void jumpScare() {
        System.out.println("Jumpscare");

        jumpscareImg.setImage(dealerJumpscare);

        Audio.stopBackgroundMusic();
        Audio.playJumpscareSound();
        jumpscareImg.setVisible(true);

        PauseTransition delay = new PauseTransition(Duration.millis(300)); // delay
        delay.setOnFinished(e -> jumpscareImg.setVisible(false));
        delay.play();
        Audio.playBackgroundMusic();
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
