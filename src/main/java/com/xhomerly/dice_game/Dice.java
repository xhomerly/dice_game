package com.xhomerly.dice_game;

import javafx.scene.image.ImageView;

public class Dice {
    private byte value;
    private boolean isLocked;
    private ImageView dice;

    public Dice(ImageView dice, boolean isLocked) {
        this.dice = dice;
        this.isLocked = isLocked;
    }

    public ImageView getImageView() {
        return dice;
    }

    public void setValue(Byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLock(boolean locked) {
        isLocked = locked;
    }
}
