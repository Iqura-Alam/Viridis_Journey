package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.City;
import model.Difficulty;
import model.GameState;

public class DifficultyController {

    @FXML private Button easyButton;
    @FXML private Button mediumButton;
    @FXML private Button hardButton;

    @FXML
    private void goToDaySetup(ActionEvent event) {
        GameManager gm = GameManager.getInstance();

        // Create City with difficulty based on button clicked
        City city;
        Object source = event.getSource();
        if (source == easyButton) {
            city = new City(Difficulty.EASY);
        } else if (source == mediumButton) {
            city = new City(Difficulty.MEDIUM);
        } else {
            city = new City(Difficulty.HARD);
        }

        // Create new GameState with that City
        GameState gameState = new GameState(new City(Difficulty.HARD));

        gameState.setCity(city);   // <-- you’ll need a setter in GameState if not already there
        gm.setGameState(gameState);

        gm.loadScreen("DaySetup.fxml");
    }
    @FXML
    private void goBack() {
        GameManager gm = GameManager.getInstance();
        gm.loadScreen("Home.fxml");  // 👈 or whichever screen is your previous one
    }



}
