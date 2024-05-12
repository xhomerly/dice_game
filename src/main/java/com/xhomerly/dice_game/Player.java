package com.xhomerly.dice_game;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Player {
    private byte position;
    private int score;
    private final String username; // konstantni prezdivka nastavena pri startu hry
    private HBox playerHBox; // HBox v leaderboards
    private Label positionLabel; // Labely pro nastavovani hodnot; fx:id mi nefungovali
    private Label scoreLabel;

    public Player(byte position, String username) {
        this.position = position;
        this.username = username;
    }

    public byte getPosition() {
        return position;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score += score;
    }

    public String getUsername() {
        return username;
    }

    public HBox getPlayerHBox() {
        return playerHBox;
    }

    public void setPlayerHBox(HBox playerHBox) {
        this.playerHBox = playerHBox;
    }

    public void setPositionLabel(Label positionLabel) {
        this.positionLabel = positionLabel;
    }

    public void setScoreLabel(Label scoreLabel) {
        this.scoreLabel = scoreLabel;
    }

    public void updateLabels() {
        positionLabel.setText("#"+position);
        scoreLabel.setText(""+score);
    }
}
