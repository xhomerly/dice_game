package com.xhomerly.dice_game;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.UnaryOperator;

public class Controller {
    @FXML
    private ImageView dice1, dice2, dice3, dice4, dice5, dice6;
    private Dice[] dices; // v inicializaci se vytváří Dice objekty s těmito ImageViews ^

    @FXML
    private ImageView lock1, lock2, lock3, lock4, lock5, lock6;
    private ImageView[] locks = {lock1, lock2, lock3, lock4, lock5, lock6}; // pole jednotlivých locků ve scéně

    @FXML private TextField playerInput; // pro input počtu hráčů
    @FXML private Button startButton; //ten tu mám jen pro setDisable, ať hráč nezačne hru bez nastavení počtu hráčů
    @FXML private ScrollPane usernamesWrapper; // do tohoto se setne VBox usernames v
    @FXML private VBox usernames; // do tohoto vkládám textFieldy a Labely pro nastavení přezdívek
    @FXML private Label hintLabel; // toto jen zobrazuju a schovávám
    @FXML private Label errorLabel; // toto jen zobrazuju a schovávám
    @FXML private VBox startBox; // abych to schoval po začátku hry
    @FXML private BorderPane borderPane; // toto zas zobrazil
    @FXML private ScrollPane leaderboardsWrapper; // do tohoto se setne VBox leaderboards v
    @FXML private VBox leaderboards; // semka vkládám HBoxy jednotlivých hráčů
    @FXML private Label currentTurn; // zde jen nastavuji kdo zrovna hraje, je to ten label vlevo nahoře
    @FXML private Label potentialScoreLabel; //počítání potenciálního skóre
    @FXML private Label notEnoughLabel; // toto se zobrazuje jen pokud potenciální skóre nepůjde uložit
    @FXML private Button endTurnButton; // zde pouzivam jen setDisable, aby hraci nepreskakovali kola bez hozeni kostky
    @FXML private VBox endGameBox; // toto se zobrazí na konci hry
    @FXML private Label winnerNameLabel; // zde nastavuji jméno výherce
    @FXML private Label winnerScoreLabel; // zde nastavuji výherní skóre

    private TextField[] usernameInputs; // jednotlive textFieldy hážu do pole a potom ten obsah getuju z tohoto pole
    private byte[] currentValueArray; // ulozene hodnoty jsou zde
    private int potentialScore; // prepocitava se pri kazdem ulozeni/odemknuti
    private int potentialScoreTrans; // skore ktere se prenasi mezi vyresetovanimi kostek pri jednom tahu
    private int currentScore; // zde se prirazuje potentialScore a potom se dava objektu player
    private byte numOfPlayers; // pocet hracu, ktery se prirazuje na zacatku
    private Player[] players; // pole hracu
    private byte turn = 0; // pocita index hrace ktery je na rade
    private boolean firstRolled = false;
    private ArrayList<Boolean> lockedBefore = new ArrayList<>();
    private int numberOfThrows = 0;

    private RotateTransition transition1, transition2, transition3, transition4, transition5, transition6;
    private RotateTransition[] transitions = {transition1, transition2, transition3, transition4, transition5, transition6};

    private final Image[] dicesImg = {
            new Image(getClass().getResourceAsStream("dice1.png")),
            new Image(getClass().getResourceAsStream("dice2.png")),
            new Image(getClass().getResourceAsStream("dice3.png")),
            new Image(getClass().getResourceAsStream("dice4.png")),
            new Image(getClass().getResourceAsStream("dice5.png")),
            new Image(getClass().getResourceAsStream("dice6.png"))
    };

    private final Media[] rolls = {
            new Media(getClass().getResource("roll1.mp3").toString()),
            new Media(getClass().getResource("roll2.mp3").toString()),
            new Media(getClass().getResource("roll3.mp3").toString()),
            new Media(getClass().getResource("roll4.mp3").toString())
    };

    public void initialize() {
        startButton.setDisable(true);
        lockedBefore.addFirst(true);
        potentialScoreLabel.setText("0");
        locks = new ImageView[]{lock1, lock2, lock3, lock4, lock5, lock6};
        dices = new Dice[6];
        currentValueArray = new byte[]{9, 9, 9, 9, 9, 9};
        for (byte i = 0; i < dices.length; i++) {
            switch (i) {
                case 0:
                    dices[i] = new Dice(dice1, false);
                    break;
                case 1:
                    dices[i] = new Dice(dice2, false);
                    break;
                case 2:
                    dices[i] = new Dice(dice3, false);
                    break;
                case 3:
                    dices[i] = new Dice(dice4, false);
                    break;
                case 4:
                    dices[i] = new Dice(dice5, false);
                    break;
                case 5:
                    dices[i] = new Dice(dice6, false);
                    break;
            }
        }
        for (byte i = 0; i < transitions.length; i++) {
            transitions[i] = new RotateTransition();
        }
        playerInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) gameStart();
        });
    }

    public void gameStart() {
        playerInput.setTextFormatter(new TextFormatter<>(getNumericFilter()));
        String enteredValue = playerInput.getText();
        try {
            numOfPlayers = Byte.parseByte(enteredValue);
            if (numOfPlayers >= 2 && numOfPlayers <= 99) {
                usernameInputs = new TextField[numOfPlayers];
                usernames.getChildren().clear();
                usernamesWrapper.setContent(usernames);
                errorLabel.setText("");
                players = new Player[numOfPlayers];
                for (byte i = 0; i < numOfPlayers; i++) {
                    Label usernameLabel = new Label("Player's "+(i+1)+" username:");
                    usernameLabel.setFont(new Font("Arial", 18));

                    TextField usernameTextField = new TextField();
                    usernameTextField.setFont(Font.font("Arial Bold", 18));
                    usernameInputs[i] = usernameTextField;

                    HBox usernameHBox = new HBox(10); // Spacing between children
                    usernameHBox.setAlignment(javafx.geometry.Pos.CENTER);
                    usernameHBox.setPrefSize(404, 66);
                    usernameHBox.getChildren().addAll(usernameLabel, usernameTextField);

                    usernames.getChildren().add(usernameHBox);
                }
                usernamesWrapper.setContent(usernames);
                startButton.setDisable(false);
                hintLabel.setVisible(true);
            } else {
                errorLabel.setText("The number is invalid. Set something between 2 - 99");
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("The number is not a valid byte value. Set something between 2 - 99");
        }
    }

    public void gameStartUsernames() {
        for (byte i = 0; i < numOfPlayers; i++) {
            String username;
            if (usernameInputs[i].getText().isBlank()) username = "Player " + (i + 1);
            else username = usernameInputs[i].getText();
            players[i] = new Player((byte) (i+1), username);

            Font font = new Font("Arial", 20);

            Label position = new Label("#" + players[i].getPosition());
            position.setPrefSize(60, 50);
            position.setAlignment(Pos.CENTER);
            position.setFont(font);
            position.setTextFill(Color.WHITE);
            players[i].setPositionLabel(position);

            Label usernameLabel = new Label(username);
            usernameLabel.setPrefSize(117, 50);
            usernameLabel.setAlignment(Pos.CENTER_LEFT);
            usernameLabel.setFont(font);
            usernameLabel.setTextFill(Color.WHITE);

            Label scoreText = new Label("score:");
            scoreText.setPrefSize(80, 50);
            scoreText.setAlignment(Pos.CENTER);
            scoreText.setFont(font);
            scoreText.setTextFill(Color.WHITE);

            Label score = new Label("" + players[i].getScore());
            score.setPrefSize(100, 50);
            score.setAlignment(Pos.CENTER);
            score.setFont(font);
            score.setTextFill(Color.WHITE);
            players[i].setScoreLabel(score);

            HBox player = new HBox(position, usernameLabel, scoreText, score);
            player.setPrefSize(415, 50);

            leaderboards.getChildren().add(player);
            players[i].setPlayerHBox(player);
        }
        leaderboardsWrapper.setContent(leaderboards);
        currentTurn.setText(players[turn].getUsername());
        players[turn].getPlayerHBox().getStyleClass().add("activePlayer");
        endTurnButton.setDisable(true);
        startBox.setVisible(false);
        borderPane.setVisible(true);
        hintLabel.setVisible(false);
    }

    private UnaryOperator<TextFormatter.Change> getNumericFilter() {
        return change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            } else return null;
        };
    }

    public void roll() {
        endTurnButton.setDisable(false);
        numberOfThrows++;
        if (numberOfThrows != lockedBefore.size()) {
            lockedBefore.add(numberOfThrows - 1, false);
        }
        if (!lockedBefore.get(numberOfThrows - 1)) {
            currentScore = 0;
            endTurn();
        } else {
            potentialScoreTrans = potentialScore;
            currentValueArray = new byte[]{9, 9, 9, 9, 9, 9};
            if (!firstRolled) {
                for (Dice dice : dices) {
                    dice.getImageView().setOnMouseClicked(this::lock);
                    firstRolled = true;
                }
            }
            byte random = (byte) (Math.round(Math.random() * 3));
            MediaPlayer mediaPlayer = new MediaPlayer(rolls[random]);
            mediaPlayer.play();
            if (dices[0].isLocked() && dices[1].isLocked() && dices[2].isLocked() && dices[3].isLocked() && dices[4].isLocked() && dices[5].isLocked()) {
                currentValueArray = new byte[]{9, 9, 9, 9, 9, 9, 9, 9};
                currentScore = 0;
                for (int i = 0; i < dices.length; i++) {
                    dices[i].setLock(false);
                    locks[i].setVisible(false);
                    dices[i].getImageView().setOnMouseClicked(this::lock);
                }
            }
            for (byte i = 0; i < dices.length; i++) {
                if (!dices[i].isLocked()) {
                    dices[i].setValue((byte) (Math.round(Math.random() * 5) + 1));
                    transitions[i] = new RotateTransition();
                    transitions[i].setAxis(Rotate.Z_AXIS);
                    transitions[i].setByAngle(360);
                    transitions[i].setCycleCount((byte) (Math.random() * 3 + 1));
                    transitions[i].setDuration(Duration.millis(400));
                    transitions[i].setAutoReverse(true);
                    transitions[i].setNode(dices[i].getImageView());
                    transitions[i].setInterpolator(Interpolator.EASE_OUT);
                    transitions[i].play();
                    byte finalI = i;
                    transitions[i].setOnFinished(event -> dices[finalI].getImageView().setImage(dicesImg[dices[finalI].getValue() - 1]));
                } else {
                    dices[i].setValue((byte) 9);
                    dices[i].getImageView().setOnMouseClicked(null);
                }
            }
        }
    }

    public int checkScore(byte[] tmpValues) {
        int tmpScore = 0;

        if (tmpValues[0] == tmpValues[1] && tmpValues[1] == tmpValues[2] && tmpValues[2] == tmpValues[3] && tmpValues[3] == tmpValues[4] && tmpValues[4] == tmpValues[5] && tmpValues[4] != 9) {
            if (tmpValues[0] == 1) {
                tmpScore = 1000 * 2 * 2 * 2;
            } else {
                tmpScore = tmpValues[0] * 100 * 2 * 2 * 2;
            }
        } else if (tmpValues[0] == tmpValues[1] && tmpValues[2] == tmpValues[3] && tmpValues[4] == tmpValues[5] && tmpValues[4] != 9 && tmpValues[0] != tmpValues[2] && tmpValues[2] != tmpValues[4]) {
            tmpScore = 1500;
        } else if (tmpValues[0] == 1 && tmpValues[1] == 2 && tmpValues[2] == 3 && tmpValues[3] == 4 && tmpValues[4] == 5 && tmpValues[5] == 6) {
            tmpScore = 3000;
        } else {
            for (byte i = 0; i < dices.length; i++) {
                if (tmpValues[i] == tmpValues[i + 1] && tmpValues[i + 1] == tmpValues[i + 2] && tmpValues[i + 2] == tmpValues[i + 3] && tmpValues[i + 3] == tmpValues[i + 4]) {
                    if (tmpValues[i] != 9) {
                        byte exc = tmpValues[i];
                        if (tmpValues[i] == 1) {
                            tmpScore += 1000 * 2 * 2;
                        } else {
                            tmpScore += tmpValues[i] * 100 * 2 * 2;
                        }
                        for (byte y = (byte) (i + 5); y < dices.length; y++) {
                            tmpScore += checkOneOrFive(tmpValues, y, exc);
                        }
                    }
                    break;
                } else if (tmpValues[i] == tmpValues[i + 1] && tmpValues[i + 1] == tmpValues[i + 2] && tmpValues[i + 2] == tmpValues[i + 3]) {
                    if (tmpValues[i] != 9) {
                        byte exc = tmpValues[i];
                        if (tmpValues[i] == 1) {
                            tmpScore += 1000 * 2;
                        } else {
                            tmpScore += tmpValues[i] * 100 * 2;
                        }
                        for (byte y = (byte) (i + 4); y < dices.length; y++) {
                            tmpScore += checkOneOrFive(tmpValues, y, exc);
                        }
                    }
                    break;
                } else if (tmpValues[i] == tmpValues[i + 1] && tmpValues[i + 1] == tmpValues[i + 2]) {
                    if (tmpValues[i] != 9) {
                        byte exc = tmpValues[i];
                        if (tmpValues[i] == 1) {
                            tmpScore += 1000;
                        } else {
                            tmpScore += tmpValues[i] * 100;
                        }
                        for (byte y = (byte) (i + 3); y < dices.length; y++) {
                            if (tmpValues[y] == tmpValues[y + 1] && tmpValues[y + 1] == tmpValues[y + 2] && tmpValues[y] != exc) {
                                if (tmpValues[y] != 9) {
                                    if (tmpValues[y] == 1) {
                                        tmpScore += 1000;
                                    } else {
                                        tmpScore += tmpValues[y] * 100;
                                    }
                                }
                                break;
                            }
                            tmpScore += checkOneOrFive(tmpValues, y, exc);
                        }
                    }
                    break;
                } else {
                    if (tmpValues[i] == 1) {
                        tmpScore += 100;
                    }
                    if (tmpValues[i] == 5) {
                        tmpScore += 50;
                    }
                }
            }
        }
        return tmpScore;
    }

    public int checkOneOrFive(byte[] tmpValues, byte y, byte exc) {
        int tmpScore = 0;
        if (tmpValues[y] == 1 && tmpValues[y] != exc) {
            tmpScore += 100;
        }
        if (tmpValues[y] == 5 && tmpValues[y] != exc) {
            tmpScore += 50;
        }

        return tmpScore;
    }

    public void countCurrent(boolean isLocking) {
        byte[] tmpValues = new byte[10];
        for (byte i = 0; i < 10; i++) {
            if (i < currentValueArray.length) tmpValues[i] = currentValueArray[i];
            else tmpValues[i] = 9;
        }
        Arrays.sort(tmpValues);

        int tmpScore = checkScore(tmpValues);

        if (isLocking && tmpScore > 0) {
            if (lockedBefore.size() == numberOfThrows) lockedBefore.add(numberOfThrows, true);
            else lockedBefore.set(numberOfThrows, true);
        } else if (!isLocking && tmpScore == 0) {
            if (lockedBefore.size() == numberOfThrows) lockedBefore.add(numberOfThrows, false);
            else lockedBefore.set(numberOfThrows, false);
        }

        potentialScore = tmpScore + potentialScoreTrans;
        potentialScoreLabel.setText("" + potentialScore);

        if (potentialScore >= 400) {
            currentScore = potentialScore;
            notEnoughLabel.setText("");
        } else {
            currentScore = 0;
            notEnoughLabel.setText("Not enough score to save");
        }
    }

    public void lock(MouseEvent event) {
        Object userData = ((Node) event.getSource()).getUserData();
        byte diceNum = Byte.parseByte((String) userData);
        if (dices[diceNum].isLocked()) {
            locks[diceNum].setVisible(false);
            dices[diceNum].setLock(false);
            currentValueArray[diceNum] = 9;
            countCurrent(false);
        } else {
            locks[diceNum].setVisible(true);
            dices[diceNum].setLock(true);
            currentValueArray[diceNum] = dices[diceNum].getValue();
            countCurrent(true);
        }
    }

    public void endTurn() {
        endTurnButton.setDisable(true);
        numberOfThrows++;
        if (numberOfThrows != lockedBefore.size()) {
            lockedBefore.add(numberOfThrows - 1, false);
        }
        if (!lockedBefore.get(numberOfThrows-1)) {
            currentScore = 0;
        }
        numberOfThrows = 0;
        lockedBefore.clear();
        lockedBefore.addFirst(true);

        players[turn].setScore(currentScore);
        players[turn].getPlayerHBox().getStyleClass().remove("activePlayer");
        Player tmpPlayers[] = players.clone();
        Arrays.sort(tmpPlayers, Comparator.comparingInt(Player::getScore).reversed());
        for (byte i = 0; i < players.length; i++) {
            tmpPlayers[i].setPosition((byte) (i+1));
        }
        leaderboards.getChildren().clear();
        for (byte i = 0; i < players.length; i++) {
            byte position = -1;
            for (byte y = 0; y < tmpPlayers.length; y++) {
                if (tmpPlayers[y].getUsername().equals(players[i].getUsername())) {
                    position = y;
                    break;
                }
            }
            if (position != -1) {
                leaderboards.getChildren().add(tmpPlayers[i].getPlayerHBox());
                players[i].setPosition((byte) (position+1));
                players[i].updateLabels();
            }
        }
        potentialScoreLabel.setText("0");
        notEnoughLabel.setText("");
        currentScore = 0;
        potentialScore = 0;
        potentialScoreTrans = 0;
        currentValueArray = new byte[]{9, 9, 9, 9, 9, 9, 9, 9};

        if (players[turn].getScore() >= 10000) {
            endGameBox.setVisible(true);
            borderPane.setVisible(false);
            winnerNameLabel.setText(players[turn].getUsername());
            winnerScoreLabel.setText(""+players[turn].getScore());
        } else {
            if (turn < numOfPlayers - 1) turn++;
            else turn = 0;
            players[turn].getPlayerHBox().getStyleClass().add("activePlayer");
            currentTurn.setText(players[turn].getUsername());
            for (byte i = 0; i < dices.length; i++) {
                dices[i].setValue((byte) 0);
                dices[i].getImageView().setImage(new Image(getClass().getResourceAsStream("dice0.png")));
                dices[i].setLock(false);
                locks[i].setVisible(false);
                dices[i].getImageView().setOnMouseClicked(null);
                firstRolled = false;
            }
        }
    }

    public void playAgain() {
        turn = 0;
        for (byte i = 0; i < dices.length; i++) {
            dices[i].setValue((byte) 0);
            dices[i].getImageView().setImage(new Image(getClass().getResourceAsStream("dice0.png")));
            dices[i].setLock(false);
            locks[i].setVisible(false);
            dices[i].getImageView().setOnMouseClicked(null);
            firstRolled = false;
        }
        startBox.setVisible(true);
        borderPane.setVisible(false);
        initialize();
        leaderboards.getChildren().clear();
        lockedBefore.clear();
        lockedBefore.addFirst(true);
        players[turn].setScore(currentScore);
        players[turn].getPlayerHBox().getStyleClass().remove("activePlayer");
        potentialScoreLabel.setText("0");
        notEnoughLabel.setText("");
        currentScore = 0;
        potentialScore = 0;
        potentialScoreTrans = 0;
        currentValueArray = new byte[]{9, 9, 9, 9, 9, 9, 9, 9};
        endGameBox.setVisible(false);
    }
}