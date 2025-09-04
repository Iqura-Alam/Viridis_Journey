package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaPlayer;

public class SettingsController {

    @FXML
    private Slider volumeSlider;

    @FXML
    private void initialize() {
        MediaPlayer player = GameManager.getInstance().getBackgroundMusic();
        if (player != null) {
            // Set initial slider value
            volumeSlider.setValue(player.getVolume());

            // Bind slider changes to MediaPlayer volume
            volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                player.setVolume(newVal.doubleValue());
            });
        }
    }

    @FXML
    private void goToHome() {
        GameManager.getInstance().loadScreen("Home.fxml");
    }
}
