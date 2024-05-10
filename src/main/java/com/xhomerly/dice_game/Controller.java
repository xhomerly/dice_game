package com.xhomerly.dice_game;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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
import java.util.function.UnaryOperator;

public class Controller {
    @FXML
    private ImageView dice1, dice2, dice3, dice4, dice5, dice6;
    private Dice[] dices;

    @FXML
    private ImageView lock1, lock2, lock3, lock4, lock5, lock6;
    private ImageView[] locks = {lock1, lock2, lock3, lock4, lock5, lock6};

    @FXML private TextField playerInput;
    @FXML private Label errorLabel;
    @FXML private VBox startBox;
    @FXML private BorderPane borderPane;
    @FXML private ScrollPane leaderboardsWrapper;
    @FXML private VBox leaderboards;
    @FXML private Label currentTurn;
    @FXML private Label potentialScoreLabel;
    @FXML private Label notEnoughLabel;
    @FXML private VBox endGameBox;
    @FXML private Label winnerNameLabel;
    @FXML private Label winnerScoreLabel;

    private int currentScore;
    private byte[] currentValueArray;
    private int potentialScore;
    private int potentialScoreTrans;
    private byte numOfPlayers;
    private Player[] players;
    private byte turn = 0;
    private boolean firstRolled = false;
    private String[] scoreLabels;
    private ArrayList<Boolean> lockedBefore = new ArrayList<Boolean>();
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
        lockedBefore.add(0, true);
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
        System.out.println("Toto je porad jen debug faze, potom pores tohle v initialize"); //todo: nastavit tohle jako default v scene builderu
        startBox.setVisible(true);
        borderPane.setVisible(false);
        //todo: tohle potom smaž až budeš mít nickname settings
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
                scoreLabels = new String[numOfPlayers];
                errorLabel.setText("");
                startBox.setVisible(false);
                borderPane.setVisible(true);
                players = new Player[numOfPlayers];
                for (byte i = 0; i < numOfPlayers; i++) {
                    String username = "Player " + (i + 1);
                    players[i] = new Player(username);

                    Font font = new Font("Arial", 20);

                    Label positon = new Label("#" + (i + 1));
                    positon.setPrefSize(50, 50);
                    positon.setAlignment(Pos.CENTER);
                    positon.setFont(font);
                    positon.setTextFill(Color.WHITE);

                    Label usernameLabel = new Label(username);
                    usernameLabel.setPrefSize(126, 50);
                    usernameLabel.setAlignment(Pos.CENTER);
                    usernameLabel.setFont(font);
                    usernameLabel.setTextFill(Color.WHITE);

                    Label scoreText = new Label("score:");
                    scoreText.setPrefSize(100, 50);
                    scoreText.setAlignment(Pos.CENTER_RIGHT);
                    scoreText.setFont(font);
                    scoreText.setTextFill(Color.WHITE);

                    Label score = new Label("" + players[i].getScore());
                    score.setPrefSize(111, 50);
                    score.setAlignment(Pos.CENTER);
                    score.setFont(font);
                    score.setTextFill(Color.WHITE);
                    score.setId("scoreLabel" + i);
                    scoreLabels[i] = "scoreLabel" + i;

                    HBox player = new HBox(positon, usernameLabel, scoreText, score);
                    player.setPrefSize(415, 50);

                    leaderboards.getChildren().add(player);
                }
                leaderboardsWrapper.setContent(leaderboards);
                currentTurn.setText(players[turn].getUsername());

            } else {
                errorLabel.setText("The number is invalid. Set something between 2 - 99");
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("The number is not a valid byte value. Set something between 2 - 99");
        }
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
        numberOfThrows++;
        if (numberOfThrows != lockedBefore.size()) {
            lockedBefore.add(numberOfThrows - 1, false);
        }
        if (!lockedBefore.get(numberOfThrows-1)) {
            currentScore = 0;
        }

        numberOfThrows = 0;
        lockedBefore.clear();
        lockedBefore.add(0, true);
        players[turn].setScore(currentScore);
        Label scoreLabel = (Label) leaderboards.lookup("#" + scoreLabels[turn]);
        scoreLabel.setText("" + players[turn].getScore());
        potentialScoreLabel.setText("0");
        notEnoughLabel.setText("");
        currentScore = 0;
        potentialScore = 0;
        potentialScoreTrans = 0;
        currentValueArray = new byte[]{9, 9, 9, 9, 9, 9, 9, 9};

        if (players[turn].getScore() >= 10000) {
            endGameBox.setVisible(true);
            borderPane.setVisible(false);
            winnerNameLabel.setText(""+players[turn].getUsername());
            winnerScoreLabel.setText(""+players[turn].getScore());
        } else {
            if (turn < numOfPlayers - 1) turn++;
            else turn = 0;
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
}