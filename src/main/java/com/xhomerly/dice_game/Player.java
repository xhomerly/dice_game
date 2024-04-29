package com.xhomerly.dice_game;

public class Player {
//    private byte position;
    private int score = 0;
    private final String username;

    public Player(String username) {
//        this.position = position;
        this.username = username;
    }

//    public void setPosition(byte position) {
//        this.position = position;
//    }

    public void setScore(byte score) {
        this.score = score;
    }

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
