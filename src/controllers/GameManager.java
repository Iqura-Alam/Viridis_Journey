package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.scene.media.MediaPlayer;
import model.City;
import model.Difficulty;
import model.GameState;

public class GameManager {

    private static GameManager instance;
    private Stage primaryStage;
    private GameState gameState;
    
    // 🔹 Add MediaPlayer for background music
    private MediaPlayer backgroundMusic;

    private GameManager() {}

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Make primary stage full screen at start
        this.primaryStage.setFullScreen(true);
        this.primaryStage.setFullScreenExitHint(""); // disable default ESC hint
        this.primaryStage.setMaximized(true);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void loadScreen(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/" + fxmlFile));
            Parent root = loader.load();

            // Pass GameState to controllers
            Object controller = loader.getController();
            if (controller instanceof DaySetupController dayController) {
                dayController.setGameState(gameState);
            } else if (controller instanceof GameBoardController boardController) {
                boardController.setGameState(gameState);
                boardController.setupGameBoard();  // Initialize grid after gameState is set
            }

            // Get screen dimensions for scaling
            double screenWidth = Screen.getPrimary().getBounds().getWidth();
            double screenHeight = Screen.getPrimary().getBounds().getHeight();

            Scene scene = new Scene(root, screenWidth, screenHeight);

            primaryStage.setScene(scene);

            // Ensure full screen and maximized
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
            primaryStage.setMaximized(true);

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Convenience method to initialize GameState at start
    public void initializeGame(Difficulty difficulty) {
        this.gameState = new GameState(
                "Home",
                new City(difficulty),
                new model.Tile[10][10],
                new java.util.ArrayList<>(),
                0, 0,
                new java.util.ArrayList<>()
        );
    }

    // 🔹 Getter and Setter for background music
    public void setBackgroundMusic(MediaPlayer player) {
        this.backgroundMusic = player;
    }

    public MediaPlayer getBackgroundMusic() {
        return this.backgroundMusic;
    }
}
