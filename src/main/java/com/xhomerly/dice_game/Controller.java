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

import java.util.function.UnaryOperator;

public class Controller {
    @FXML
    private ImageView dice1, dice2, dice3, dice4, dice5, dice6;
    private ImageView[] dices = {dice1, dice2, dice3, dice4, dice5, dice6};

    @FXML
    private ImageView lock1, lock2, lock3, lock4, lock5, lock6;
    private ImageView[] locks = {lock1, lock2, lock3, lock4, lock5, lock6};

    @FXML private TextField playerInput;
    @FXML private Label errorLabel;
    @FXML private VBox startBox;
    @FXML private BorderPane borderPane;
    @FXML private ScrollPane leaderboardsWrapper;
    @FXML private VBox leaderboards;

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

    private byte numOfPlayers;

    private byte values[];

    private boolean isLocked[] = {false, false, false, false, false, false};

    public void initialize() {
        values = new byte[dices.length];
        locks = new ImageView[]{lock1, lock2, lock3, lock4, lock5, lock6};
        for (byte i = 0; i < dices.length; i++) {
            dices[i] = new ImageView();
        }
        for (byte i = 0; i < transitions.length; i++) {
            transitions[i] = new RotateTransition();
        }
        //todo:
        System.out.println("Toto je porad jen debug faze, potom pores tohle v initialize");
        startBox.setVisible(true);
        borderPane.setVisible(false);
        //todo: tohle potom smaž až budeš mít nickname settings
        playerInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) gameStart();
        });
    }

    public void gameStart() { //todo:
        playerInput.setTextFormatter(new TextFormatter<>(getNumericFilter()));
        String enteredValue = playerInput.getText();
        try {
            numOfPlayers = Byte.parseByte(enteredValue);
            if (numOfPlayers >= 2 && numOfPlayers <= 99) {
                errorLabel.setText("");
                startBox.setVisible(false);
                borderPane.setVisible(true);
                for (byte i = 1; i <= numOfPlayers; i++) {
                    Font font = new Font("Arial", 20);

                    Label positon = new Label("#" + i);
                    positon.setPrefSize(50,50);
                    positon.setAlignment(Pos.CENTER);
                    positon.setFont(font);
                    positon.setTextFill(Color.WHITE);

                    Label name = new Label("Player " + i);
                    name.setPrefSize(126,50);
                    name.setAlignment(Pos.CENTER);
                    name.setFont(font);
                    name.setTextFill(Color.WHITE);

                    Label scoreText = new Label("score:");
                    scoreText.setPrefSize(100,50);
                    scoreText.setAlignment(Pos.CENTER_RIGHT);
                    scoreText.setFont(font);
                    scoreText.setTextFill(Color.WHITE);

                    Label score = new Label("000");
                    score.setPrefSize(111,50);
                    score.setAlignment(Pos.CENTER);
                    score.setFont(font);
                    score.setTextFill(Color.WHITE);

                    HBox player = new HBox(positon, name, scoreText, score);
                    player.setPrefSize(415,50);

                    leaderboards.getChildren().add(player);
                }
                leaderboardsWrapper.setContent(leaderboards);
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
        byte random = (byte) (Math.round(Math.random() * 3));
        MediaPlayer mediaPlayer = new MediaPlayer(rolls[random]);
        mediaPlayer.play();
        for (byte i = 0; i < dices.length; i++) {
            values[i] = (byte) (Math.round(Math.random() * 5) + 1);
            transitions[i] = new RotateTransition();
            transitions[i].setAxis(Rotate.Z_AXIS);
            transitions[i].setByAngle(360);
            transitions[i].setCycleCount((int) (Math.random() * 4 + 1));
            transitions[i].setDuration(Duration.millis(500));
            transitions[i].setAutoReverse(true);
            ImageView dice = null;
            switch (i) {
                case 0:
                    transitions[i].setNode(dice1);
                    dice = dice1;
                    break;
                case 1:
                    transitions[i].setNode(dice2);
                    dice = dice2;
                    break;
                case 2:
                    transitions[i].setNode(dice3);
                    dice = dice3;
                    break;
                case 3:
                    transitions[i].setNode(dice4);
                    dice = dice4;
                    break;
                case 4:
                    transitions[i].setNode(dice5);
                    dice = dice5;
                    break;
                case 5:
                    transitions[i].setNode(dice6);
                    dice = dice6;
                    break;
            }
            transitions[i].setInterpolator(Interpolator.EASE_OUT);
            transitions[i].play();
            byte tmp = values[i];
            ImageView finalDice = dice;
            transitions[i].setOnFinished(event -> {
                switch (tmp) {
                    case 1:
                        finalDice.setImage(dicesImg[0]);
                        break;
                    case 2:
                        finalDice.setImage(dicesImg[1]);
                        break;
                    case 3:
                        finalDice.setImage(dicesImg[2]);
                        break;
                    case 4:
                        finalDice.setImage(dicesImg[3]);
                        break;
                    case 5:
                        finalDice.setImage(dicesImg[4]);
                        break;
                    case 6:
                        finalDice.setImage(dicesImg[5]);
                        break;
                }
            });
        }
        System.out.print("rolled ");
        for (byte i = 0; i < values.length; i++) {
            System.out.print(values[i]);
        }
        System.out.println(" ");
    }

    public void lock(MouseEvent event) {
        Object userData = ((Node) event.getSource()).getUserData();
        int diceNum = Integer.parseInt((String) userData);
        if (isLocked[diceNum]) {
            locks[diceNum].setVisible(false);
            isLocked[diceNum] = false;
        } else {
            locks[diceNum].setVisible(true);
            isLocked[diceNum] = true;
        }
    }
}