package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Difficulty;
import model.GameState;
import model.Weather;

import java.util.Random;

public class DaySetupController {

    private GameState gameState;

    @FXML private VBox rootPane;     
    @FXML private Label energyDemandLabel;
    @FXML private Label budgetLabel;
    @FXML private Label weatherLabel;

    private boolean fxmlLoaded = false;

    @FXML
    private void initialize() {
        fxmlLoaded = true;
        if (gameState != null) {
            setupWeather();
            updateUI();
        }
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        
        if (fxmlLoaded) {
            setupWeather();
            updateUI();
        }
        
    }

    /**
     * Randomly generate weather for the day and set in GameState.
     */
    private void setupWeather() {
        Weather[] weathers = Weather.values();
        Random rand = new Random();
        Weather todayWeather = weathers[rand.nextInt(weathers.length)];
        gameState.getCity().setWeather(todayWeather); // store in GameState
    }

    /**
     * Update labels and background according to difficulty and weather.
     */
    private void updateUI() {
        if (gameState == null || energyDemandLabel == null) return;

        Difficulty difficulty = gameState.getCity().getDifficulty();
        Weather currentWeather = gameState.getCity().getWeather();

        // --- Energy demand and budget text ---
        switch (difficulty) {
            case EASY -> {
                energyDemandLabel.setText("Energy Demand: Low");
                budgetLabel.setText("Budget: High");
            }
            case MEDIUM -> {
                energyDemandLabel.setText("Energy Demand: Moderate");
                budgetLabel.setText("Budget: Medium");
            }
            case HARD -> {
                energyDemandLabel.setText("Energy Demand: High");
                budgetLabel.setText("Budget: Low");
            }
        }

        // --- Weather text ---
        weatherLabel.setText("Weather: " + capitalize(currentWeather.name()));

        // --- Background image ---
        String bgPath = "file:D:/ViridisJourney2/src/img/" + currentWeather.name().toLowerCase() + ".jpg";
        rootPane.setStyle("-fx-background-image: url('" + bgPath + "'); -fx-background-size: cover;");
    }

    private String capitalize(String str) {
        return str.charAt(0) + str.substring(1).toLowerCase();
    }

    @FXML
    private void goToGameBoard() {
        GameManager.getInstance().loadScreen("GameBoard.fxml");
    }
}