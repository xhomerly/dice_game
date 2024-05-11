package com.xhomerly.dice_game;

//todo: positioning implementuju az budu mit hotovy pocitani bodu

import javafx.scene.layout.HBox;

public class Player {
//    private byte position;
    private int score;
    private final String username;
    private HBox playerHBox;

    public HBox getPlayerHBox() {
        return playerHBox;
    }

    public void setPlayerHBox(HBox playerHBox) {
        this.playerHBox = playerHBox;
    }

    public Player(String username) {
        this.username = username;
    }

    public void setScore(int score) {
        this.score += score;
    }

//    public void setPosition(byte position) {
//        this.position = position;
//    }

//    public byte getPosition() {
//        return position;
//    }

    public int getScore() {
        return score;
    }

    public String getUsername() {
        return username;
    }
}
