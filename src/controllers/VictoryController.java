package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import model.GameState;

import java.io.IOException;

public class VictoryController {

    @FXML private Label summaryLabel;
    private GameState gameState;

    public void setGameState(GameState state) {
        this.gameState = state;
        summaryLabel.setText("You survived " + state.getCurrentDay() +
                             " days with " + state.getStars() + " stars!");
    }

    @FXML
    private void playAgain() {
        try {
            // Load the GameBoard scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/GameBoard.fxml"));
            Parent root = loader.load();
            
            // Get the controller of the loaded GameBoard screen
            GameBoardController controller = loader.getController();
            
            // Set the GameState for the GameBoardController
            controller.setGameState(GameManager.getInstance().getGameState());
            
            // Call setupGameBoard() to ensure grid and state are properly initialized
            controller.setupGameBoard();

            // Get the current stage and set the new scene
            Stage stage = (Stage) summaryLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true); 
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void quitToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) summaryLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true); 
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
