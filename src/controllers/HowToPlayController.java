package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

public class HowToPlayController {

    @FXML
    private ScrollPane scrollPane; // Reference to ScrollPane in FXML (optional)

    /**
     * Go back to the Home screen.
     */
    @FXML
    private void goBack() {
        GameManager.getInstance().loadScreen("Home.fxml");
    }

    /**
     * Optional: Reset scroll position to top when screen loads.
     */
    @FXML
    private void initialize() {
        if (scrollPane != null) {
            scrollPane.setVvalue(0); // Scroll to top
        }
    }
}
