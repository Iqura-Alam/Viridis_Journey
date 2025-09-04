package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;

public class SimulationResultsController {

    @FXML private Label producedLabel;
    @FXML private Label demandLabel;
    @FXML private ProgressBar demandProgressBar;
    @FXML private ProgressBar pollutionProgressBar;
    @FXML private ProgressBar budgetProgressBar;
    @FXML private ListView<String> triggeredEventsList;
    @FXML private Label starsLabel;

    private ReportModel report;
    private GameState gameState;

    public void setSimulationReport(ReportModel report) {
        this.report = report;
        tryUpdateUI();
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        tryUpdateUI();
    }

    private void tryUpdateUI() {
        if (report != null && gameState != null && demandLabel != null) {
            updateUI();
        }
    }

    public void setData(GameState gameState, ReportModel report) {
        this.gameState = gameState;
        this.report = report;
        updateUI();
    }

    private void updateUI() {
        if (report == null) return;

        double baseDemand = gameState.getCity().getBaseDemand();
        double produced = report.getEnergyProduced();
        double distributed = report.getEnergyDistributed();

        // Produced
        producedLabel.setText("Produced: " + produced);

        // Distributed vs Demand
        if (distributed > baseDemand) {
            demandLabel.setText("Distributed: " + baseDemand + " / " + baseDemand +
                                " (+ surplus " + (distributed - baseDemand) + ")");
        } else {
            demandLabel.setText("Distributed: " + distributed + " / " + baseDemand);
        }
        demandProgressBar.setProgress(Math.min(distributed / baseDemand, 1.0));

        // Pollution
        pollutionProgressBar.setProgress(report.getTotalPollution() / 100.0);

        // Budget
        double startBudget = report.getInitialBudget();
        budgetProgressBar.setProgress((double) report.getBudgetRemaining() / startBudget);

        // Events
        triggeredEventsList.getItems().clear();
        triggeredEventsList.getItems().addAll(report.getTriggeredEvents());

        // Stars
        starsLabel.setText("Stars: " + report.getStars());
    }

 // =====================
 // Difficulty-based logic
 // =====================

 private boolean checkWinEasy() {
     // Easy: full base demand must be met, pollution allowed up to 100
     double coverage = report.getEnergyDistributed() / gameState.getCity().getBaseDemand();
     return coverage >= 1.0; // full demand met
 }

 private boolean checkWinMedium() {
     // Medium: all priority 1 & 2 buildings must be fully powered, limited pollution (<=50)
     double energyLeft = report.getEnergyDistributed();

     for (Building b : gameState.getCity().getBuildings()) {
         if (b.getPriority() <= 2) { // priority 1 & 2
             if (energyLeft < b.getBaseDemand()) {
                 return false; // priority building not fully powered
             }
             energyLeft -= b.getBaseDemand();
         }
     }

     // Optional: check coverage for remaining buildings
     double coverage = report.getEnergyDistributed() / gameState.getCity().getBaseDemand();
     return coverage >= 0.8 && report.getTotalPollution() <= 50;
 }

 private boolean checkWinHard() {
     // Hard: all buildings fully powered, zero pollution
     double energyLeft = report.getEnergyDistributed();
     for (Building b : gameState.getCity().getBuildings()) {
         if (energyLeft < b.getBaseDemand()) {
             return false; // any building not fully powered
         }
         energyLeft -= b.getBaseDemand();
     }

     return report.getTotalPollution() == 0;
 }

 private boolean didPlayerWin() {
     return switch (gameState.getCity().getDifficulty()) {
         case EASY -> checkWinEasy();
         case MEDIUM -> checkWinMedium();
         case HARD -> checkWinHard();
     };
 }


    // =====================
    // Button handlers
    // =====================

    @FXML
    private void nextDay() {
        if (!didPlayerWin()) {
            goToGameOver("Day " + gameState.getCurrentDay() + " failed.");
            return;
        }

        if (gameState.getCurrentDay() >= gameState.getMaxDays()) {
            goToVictory();
            return;
        }

        // Move to the next day and reset the grid
        gameState.advanceDay();  // Advance the day in the GameState
        loadGameBoard();  // Load the game board again (which will reset and prepare the grid)
    }


    private void loadGameBoard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/GameBoard.fxml"));
            Parent root = loader.load();
            GameBoardController controller = loader.getController();
            controller.setGameState(gameState);
            controller.setupGameBoard();

            Stage stage = (Stage) demandLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }


	@FXML
    private void retry() {
        if (gameState.getRetriesLeft() <= 0) {
            goToGameOver("No retries left!");
            return;
        }

        gameState.setRetriesLeft(gameState.getRetriesLeft() - 1);

        int penalty = (int)(gameState.getCity().getBudget() * 0.1);
        gameState.getCity().setBudget(gameState.getCity().getBudget() - penalty);

        loadGameBoard();
    }

    @FXML
    private void quitToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) demandLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();

        } catch (IOException e) { e.printStackTrace(); }
    }

    // =====================
    // Navigation helpers
    // =====================

   

    private void goToVictory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Victory.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) demandLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();

        } catch (IOException e) { e.printStackTrace(); }
    }

    private void goToGameOver(String reason) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/GameOver.fxml"));
            Parent root = loader.load();
            GameOverController controller = loader.getController();
            controller.setReason(reason);

            Stage stage = (Stage) demandLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true); 
            stage.show();

        } catch (IOException e) { e.printStackTrace(); }
    }
}
