package com.xhomerly.dice_game;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class Controller {
    @FXML
    private ImageView dice1, dice2, dice3, dice4, dice5, dice6;
    private ImageView[] dices = {dice1, dice2, dice3, dice4, dice5, dice6};

    private final Image[] dicesImg = {
            new Image(getClass().getResourceAsStream("dice1.png")),
            new Image(getClass().getResourceAsStream("dice2.png")),
            new Image(getClass().getResourceAsStream("dice3.png")),
            new Image(getClass().getResourceAsStream("dice4.png")),
            new Image(getClass().getResourceAsStream("dice5.png")),
            new Image(getClass().getResourceAsStream("dice6.png"))
    };

    @FXML
    private ImageView lock1, lock2, lock3, lock4, lock5, lock6;
    private ImageView[] locks = {lock1, lock2, lock3, lock4, lock5, lock6};

    private RotateTransition transition1, transition2, transition3, transition4, transition5, transition6;
    private RotateTransition[] transitions = {transition1, transition2, transition3, transition4, transition5, transition6};

    public byte values[];

    public boolean isLocked[]; //todo: tu hezkou JS funkci s ? :

    public void initialize() {
        values = new byte[dices.length];
        for (byte i = 0; i < dices.length; i++) {
            dices[i] = new ImageView();
        }
        for (byte i = 0; i < transitions.length; i++) {
            transitions[i] = new RotateTransition();
        }
        for (byte i = 0; i < locks.length; i++) {
            locks[i] = new ImageView();
        }
    }

    public void roll() {
//        MediaPlayer mediaPlayer = new MediaPlayer(new Media(getClass().getResource("heheheha.mp3").toString()));
//        mediaPlayer.play();
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
                case 1: transitions[i].setNode(dice2);
                    dice = dice2;
                    break;
                case 2: transitions[i].setNode(dice3);
                    dice = dice3;
                    break;
                case 3: transitions[i].setNode(dice4);
                    dice = dice4;
                    break;
                case 4: transitions[i].setNode(dice5);
                    dice = dice5;
                    break;
                case 5: transitions[i].setNode(dice6);
                    dice = dice6;
                    break;
            }
            transitions[i].setInterpolator(Interpolator.EASE_OUT);
            transitions[i].play();
            byte tmp = values[i];
            ImageView finalDice = dice;
            transitions[i].setOnFinished(event -> {
                switch (tmp) {
                    case 1: finalDice.setImage(dicesImg[0]);
                        break;
                    case 2: finalDice.setImage(dicesImg[1]);
                        break;
                    case 3: finalDice.setImage(dicesImg[2]);
                        break;
                    case 4: finalDice.setImage(dicesImg[3]);
                        break;
                    case 5: finalDice.setImage(dicesImg[4]);
                        break;
                    case 6: finalDice.setImage(dicesImg[5]);
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
        System.out.println(diceNum);
    }
}