package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class HomeController {

    @FXML
    private Label titleLabel;

    public void initialize() {
        // Load custom font
        Font.loadFont(getClass().getResource("/Font/ka1.ttf").toExternalForm(), 36);

        // Animate title: vertical bounce effect
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(titleLabel.translateYProperty(), 0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(titleLabel.translateYProperty(), -20)),
                new KeyFrame(Duration.seconds(2), new KeyValue(titleLabel.translateYProperty(), 0))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
    }

    @FXML
    private void goToDifficulty() {
        GameManager.getInstance().loadScreen("Difficulty.fxml");
    }

    @FXML
    private void goToHowToPlay() {
        GameManager.getInstance().loadScreen("HowToPlay.fxml");
    }

    @FXML
    private void goToAbout() {
        GameManager.getInstance().loadScreen("About.fxml");
    }

    @FXML
    private void goToSettings() {
        GameManager.getInstance().loadScreen("Settings.fxml");
    }

    @FXML
    private void exitGame() {
        System.exit(0);
    }
}
