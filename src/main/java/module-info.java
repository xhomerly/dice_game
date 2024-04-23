module com.xhomerly.dice_game {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.xhomerly.dice_game to javafx.fxml;
    exports com.xhomerly.dice_game;
}