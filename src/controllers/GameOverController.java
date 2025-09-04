package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;

import java.io.IOException;

public class GameOverController {

    @FXML private Label reasonLabel;

    public void setReason(String reason) {
        reasonLabel.setText(reason);
    }

    /**
     * Retry the game from the Difficulty screen.
     * Opens full screen while keeping the message visible.
     */
    @FXML
    private void retryFromStart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Difficulty.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) reasonLabel.getScene().getWindow();
            
            double screenWidth = Screen.getPrimary().getBounds().getWidth();
            double screenHeight = Screen.getPrimary().getBounds().getHeight();

            Scene scene = new Scene(root, screenWidth, screenHeight);

            currentStage.setScene(scene);
            currentStage.setFullScreen(true);
            currentStage.setMaximized(true);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Quit to Home screen and display full screen.
     */
    @FXML
    private void quitToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Home.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) reasonLabel.getScene().getWindow();
            
            double screenWidth = Screen.getPrimary().getBounds().getWidth();
            double screenHeight = Screen.getPrimary().getBounds().getHeight();

            Scene scene = new Scene(root, screenWidth, screenHeight);

            currentStage.setScene(scene);
            currentStage.setFullScreen(true);
            currentStage.setMaximized(true);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show a temporary message dialog on top without replacing the current screen.
     */
    public static void showMessageOnTop(String message) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setAlwaysOnTop(true);
        dialog.setTitle("Message");

        Label label = new Label(message);
        label.setStyle("-fx-font-size: 18; -fx-padding: 20; -fx-text-fill: white; -fx-background-color: #e74c3c;");

        Scene scene = new Scene(label);
        dialog.setScene(scene);

        dialog.showAndWait();
    }
}
