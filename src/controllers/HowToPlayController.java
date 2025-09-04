package controllers;

import javafx.fxml.FXML;

public class HowToPlayController {

    @FXML
    private void goToHome() {
        GameManager.getInstance().loadScreen("Home.fxml");
    }
}
