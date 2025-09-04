package controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class AboutController {


    @FXML
    private void goBack() {
        GameManager gm = GameManager.getInstance();
        gm.loadScreen("Home.fxml");  // 👈 or whichever screen is your previous one
    }
}
