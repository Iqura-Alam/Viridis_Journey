package controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
public class GameBoardController {

    // ====== FXML ======
    @FXML
    private Pane gameGrid;
    @FXML
    private Button solarButton, windButton, gasButton, coalButton, batteryButton;
    @FXML
    private Label demandLabel, budgetLabel, pollutionLabel, weatherLabel;
    @FXML
    private Pane messagePane;
    @FXML
    private VBox leftPanel;
    @FXML
    private Button solarImgButton, windImgButton, gasImgButton, coalImgButton, batteryImgButton;
    @FXML
    private ImageView solarImage, windImage, gasImage, coalImage, batteryImage;
    // ====== Board constants ======
    private static final int BOARD_ROWS = 10;
    private static final int BOARD_COLS = 10;
    private static final double TILE_W = 128;
    private static final double TILE_H = 64;
    private static final double OFFSET_X = 100 / 2.0;
    private static final double OFFSET_Y = 60;
    private Weather currentWeather;
    // ====== State ======
    private EnergySourceType selectedType;
    private GameState gameState;

    private Image gridImg, houseImg, factoryImg, powerImg, schoolImg, hospitalImg;
    private Image solarImg, windImg, gasImg, coalImg, batteryImg;

    private int maxEnergySources;
    private Map<EnergySourceType, Integer> energySourceLimits;
    private Map<EnergySourceType, Integer> energySourceCounts;
    private Difficulty selectedDifficulty = Difficulty.MEDIUM;
    private final List<Tile> placedTiles = new ArrayList<>();
    // Shadow preview for drag-and-drop
    private ImageView dragShadow = new ImageView();
    private boolean dragging = false;
    private Tile dragTargetTile = null;

    // ====== Lifecycle ======
    public void setGameState(GameState gameState) {

        this.gameState = gameState;

        selectedDifficulty = gameState.getCity().getDifficulty();

        Weather todayWeather = gameState.getCity().getWeather();

        updateWeather(todayWeather);


        updateHUD();

    }

    @FXML

    private List<StackPane> highlightedTiles = new ArrayList<>();

    @FXML
    public void initialize() {
        // Load images using absolute file paths
        gridImg = new Image("file:D:/ViridisJourney2/src/img/GridISO.png");
        houseImg = new Image("file:D:/ViridisJourney2/src/img/houseISO.png");
        hospitalImg = new Image("file:D:/ViridisJourney2/src/img/hospitalISO.png");
        factoryImg = new Image("file:D:/ViridisJourney2/src/img/FactoryISO.png");
        powerImg = new Image("file:D:/ViridisJourney2/src/img/powerISO.png");
        solarImg = new Image("file:D:/ViridisJourney2/src/img/solarISO.png");
        windImg = new Image("file:D:/ViridisJourney2/src/img/turbineISO.png");
        gasImg = new Image("file:D:/ViridisJourney2/src/img/gasISO.png");
        coalImg = new Image("file:D:/ViridisJourney2/src/img/coalISO.png");
        batteryImg = new Image("file:D:/ViridisJourney2/src/img/batteryISO.png");
        schoolImg = new Image("file:D:/ViridisJourney2/src/img/schoolISO.png");


        solarImage.setImage(solarImg);
        windImage.setImage(windImg);
        gasImage.setImage(gasImg);
        coalImage.setImage(coalImg);
        batteryImage.setImage(batteryImg);
        // Set button click actions
        solarButton.setOnAction(e -> selectedType = EnergySourceType.SOLAR);
        windButton.setOnAction(e -> selectedType = EnergySourceType.WIND);
        gasButton.setOnAction(e -> selectedType = EnergySourceType.GAS);
        coalButton.setOnAction(e -> selectedType = EnergySourceType.COAL);
        batteryButton.setOnAction(e -> selectedType = EnergySourceType.BATTERY);

        // Setup drag functionality
        setupDragForButton(solarImgButton, EnergySourceType.SOLAR, solarImg);
        setupDragForButton(windImgButton, EnergySourceType.WIND, windImg);
        setupDragForButton(gasImgButton, EnergySourceType.GAS, gasImg);
        setupDragForButton(coalImgButton, EnergySourceType.COAL, coalImg);
        setupDragForButton(batteryImgButton, EnergySourceType.BATTERY, batteryImg);

        // Add drop shadow to game grid
        gameGrid.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.25)));
        leftPanel.setPickOnBounds(true);
        double btnSize = 50; // match with text button height
        setupImageButton(solarImgButton, solarImg, btnSize, btnSize, "Cost: 500\nOutput: 50\nPollution: 0");
        setupImageButton(windImgButton, windImg, btnSize, btnSize, "Cost: 600\nOutput: 40\nPollution: 0");
        setupImageButton(gasImgButton, gasImg, btnSize, btnSize, "Cost: 800\nOutput: 80\nPollution: 3");
        setupImageButton(coalImgButton, coalImg, btnSize, btnSize, "Cost: 1000\nOutput: 100\nPollution: 5");
        setupImageButton(batteryImgButton, batteryImg, btnSize, btnSize, "Cost: 1000\nOutput: 0\nPollution: 0");
        // Setup tooltips after drag setup (ensures no interference)
        solarButton.setTooltip(new Tooltip("Cost: 500\nOutput: 50\nPollution: 0"));
        windButton.setTooltip(new Tooltip("Cost: 600\nOutput: 40\nPollution: 0"));
        gasButton.setTooltip(new Tooltip("Cost: 800\nOutput: 80\nPollution: 3"));
        coalButton.setTooltip(new Tooltip("Cost: 1000\nOutput: 100\nPollution: 5"));
        batteryButton.setTooltip(new Tooltip("Cost: 1000\nOutput: 0\nPollution: 0"));

        // Make sure buttons are enabled (tooltips only show on enabled nodes)
        solarButton.setDisable(false);
        windButton.setDisable(false);
        gasButton.setDisable(false);
        coalButton.setDisable(false);
        batteryButton.setDisable(false);
    }

    @FXML
    private void hoverOn(MouseEvent event) {
        Button btn = (Button) event.getSource();
        switch (btn.getId()) {
            case "solarButton" ->
                    btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #f39c12; -fx-text-fill: black; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "windButton" ->
                    btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #2980b9; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "gasButton" ->
                    btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "coalButton" ->
                    btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #606060; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "batteryButton" ->
                    btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
        }
    }

    @FXML
    private void hoverOff(MouseEvent event) {
        Button btn = (Button) event.getSource();
        switch (btn.getId()) {
            case "solarButton" ->
                    btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #f1c40f; -fx-text-fill: black; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "windButton" ->
                    btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "gasButton" ->
                    btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "coalButton" ->
                    btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "batteryButton" ->
                    btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
        }
    }

    private void setupImageButton(Button button, Image img, double width, double height, String tooltipText) {
        // ImageView for the button
        ImageView icon = new ImageView(img);
        icon.setFitWidth(width);
        icon.setFitHeight(height);
        button.setGraphic(icon);

        // Remove background & border
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-border-color: transparent;");

        // Hover effect
        button.setOnMouseEntered(e -> icon.setOpacity(0.7));
        button.setOnMouseExited(e -> icon.setOpacity(1.0));

        // Tooltip
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setStyle("-fx-font-size: 12px;");
        button.setTooltip(tooltip);
    }


    private void setupDragForButton(Button button, EnergySourceType type, Image img) {
        button.setOnMousePressed(e -> {
            selectedType = type;
            dragShadow.setImage(img);
            dragShadow.setOpacity(0.7);
            dragShadow.setFitWidth(TILE_W);
            dragShadow.setFitHeight(TILE_H);

            if (!gameGrid.getChildren().contains(dragShadow))
                gameGrid.getChildren().add(dragShadow);

            dragging = true;
            dragShadow.setMouseTransparent(true);

            Point2D local = gameGrid.sceneToLocal(e.getSceneX(), e.getSceneY());
            dragShadow.setLayoutX(local.getX() - TILE_W / 2);
            dragShadow.setLayoutY(local.getY() - TILE_H / 2);
        });

        button.setOnMouseDragged(e -> {
            if (!dragging) return;

            // Convert scene to local coordinates relative to grid
            Point2D local = gameGrid.sceneToLocal(e.getSceneX(), e.getSceneY());
            dragShadow.setLayoutX(local.getX() - TILE_W / 2);
            dragShadow.setLayoutY(local.getY() - TILE_H / 2);

            // Highlight range for current tile
            Tile target = getTileUnderMouseLocal(local.getX(), local.getY());
            if (target != null && target != dragTargetTile) {
                // clear old highlights
                for (StackPane pane : highlightedTiles) pane.setBackground(null);
                highlightedTiles.clear();

                dragTargetTile = target;
                int range = createEnergySource(selectedType, target).getRange();
                highlightedTiles = highlightRangeWithShadow(dragTargetTile, range);
            }
        });

        button.setOnMouseReleased(e -> {
            if (dragging && dragTargetTile != null && dragTargetTile.getType() == TileType.EMPTY) {
                // Place energy source on the target tile
                placeEnergySourceOnTile(dragTargetTile, selectedType);
            }
            dragging = false;
            dragTargetTile = null;
            gameGrid.getChildren().remove(dragShadow);

            // clear highlights
            for (StackPane pane : highlightedTiles) pane.setBackground(null);
            highlightedTiles.clear();
        });

        dragShadow.setEffect(new DropShadow(20, Color.YELLOW));
    }


    // Highlight tiles in a plus shape (up, down, left, right) within range
    // Highlight tiles in a square (includes diagonals) within range
    private List<StackPane> highlightRangeWithShadow(Tile centerTile, int range) {
        List<StackPane> highlighted = new ArrayList<>();

        int row = centerTile.getRow();
        int col = centerTile.getCol();

        for (int r = row - range; r <= row + range; r++) {
            for (int c = col - range; c <= col + range; c++) {
                if (r >= 0 && r < BOARD_ROWS && c >= 0 && c < BOARD_COLS) {
                    // Chebyshev distance includes diagonals
                    int chebyshevDist = Math.max(Math.abs(r - row), Math.abs(c - col));
                    if (chebyshevDist <= range) {
                        addHighlight(r, c, highlighted);
                    }
                }
            }
        }

        return highlighted;
    }


    // Helper: get the actual isometric tile node and apply the shadow
    private void addHighlight(int r, int c, List<StackPane> highlighted) {
        StackPane tileNode = getTileNode2(r, c); // must fetch by row/col
        if (tileNode != null) {
            tileNode.setBackground(new Background(new BackgroundFill(
                    Color.rgb(255, 255, 0, 0.6), // translucent yellow overlay
                    CornerRadii.EMPTY,
                    Insets.EMPTY
            )));
            highlighted.add(tileNode);
        }
    }

    // Utility: find the tile node for a row/col
    private StackPane getTileNode2(int row, int col) {
        for (Node node : gameGrid.getChildren()) {
            if (node instanceof StackPane tilePane) {
                // check row/col via userData
                Object data = tilePane.getUserData();
                if (data instanceof int[] pos) {
                    if (pos[0] == row && pos[1] == col) {
                        return tilePane;
                    }
                }
            }
        }
        return null;
    }

    private Tile getTileUnderMouseLocal(double localX, double localY) {
        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                StackPane tilePane = (StackPane) getTileNode(r, c);
                if (tilePane != null) {
                    double x = tilePane.getLayoutX();
                    double y = tilePane.getLayoutY();
                    if (localX >= x && localX <= x + TILE_W &&
                            localY >= y && localY <= y + TILE_H)
                        return gameState.getGrid()[r][c];
                }
            }
        }
        return null;
    }
}