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
    @FXML private Pane gameGrid; 
    @FXML private Button solarButton, windButton, gasButton, coalButton, batteryButton;
    @FXML private Label demandLabel, budgetLabel, pollutionLabel, weatherLabel;
    @FXML private Pane messagePane;
    @FXML private VBox leftPanel;
    @FXML private Button solarImgButton, windImgButton, gasImgButton, coalImgButton, batteryImgButton;
    @FXML private ImageView solarImage, windImage, gasImage, coalImage, batteryImage;
    // ====== Board constants ======
    private static final int BOARD_ROWS = 10;
    private static final int BOARD_COLS = 10;
    private static final double TILE_W = 128;
    private static final double TILE_H = 64;
    private static final double OFFSET_X = 100 / 2.0;
    private static final double OFFSET_Y = 60;
    private Weather  currentWeather;
    // ====== State ======
    private EnergySourceType selectedType;
    private GameState gameState;

    private Image gridImg, houseImg, factoryImg, powerImg,schoolImg,hospitalImg;
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

        // Setup drag functionality
        setupDragForButton(solarImgButton, EnergySourceType.SOLAR, solarImg);
        setupDragForButton(windImgButton, EnergySourceType.WIND, windImg);
        setupDragForButton(gasImgButton, EnergySourceType.GAS, gasImg);
        setupDragForButton(coalImgButton, EnergySourceType.COAL, coalImg);
        setupDragForButton(batteryImgButton, EnergySourceType.BATTERY, batteryImg);

        // Add drop shadow to game grid
        gameGrid.setEffect(new DropShadow(20, Color.rgb(0,0,0,0.25)));
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

        solarButton.setDisable(false);
        windButton.setDisable(false);
        gasButton.setDisable(false);
        coalButton.setDisable(false);
        batteryButton.setDisable(false);
    }
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
    private void hoverOn(MouseEvent event) {
        Button btn = (Button) event.getSource();
        switch (btn.getId()) {
            case "solarButton" -> btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #f39c12; -fx-text-fill: black; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "windButton" -> btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #2980b9; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "gasButton" -> btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "coalButton" -> btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #606060; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "batteryButton" -> btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
        }
    }
    @FXML
    private void hoverOff(MouseEvent event) {
        Button btn = (Button) event.getSource();
        switch (btn.getId()) {
            case "solarButton" -> btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #f1c40f; -fx-text-fill: black; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "windButton" -> btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "gasButton" -> btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "coalButton" -> btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
            case "batteryButton" -> btn.setStyle("-fx-font-family: 'Karmatic Arcade'; -fx-font-size: 14; -fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 8 15 8 15; -fx-background-radius: 8;");
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
    // ====== Setup ======
    public void setupGameBoard() {
        if (gameState == null) {
            System.out.println("ERROR: gameState is null!");
            return;
        }

        setEnergySourceLimit();
        gameGrid.getChildren().clear();

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                gameState.getGrid()[r][c] = new Tile(r, c, TileType.EMPTY);
            }
        }

        for (int r = 0; r < BOARD_ROWS; r++) {
            for (int c = 0; c < BOARD_COLS; c++) {
                StackPane tilePane = createIsoTile(r, c);
                gameGrid.getChildren().add(tilePane);
            }
        }

        placeRandomBuildings(8);
        updateHUD();
    }



    // ====== Limits ======
    private void setEnergySourceLimit() {
        energySourceCounts = new HashMap<>();
        switch (selectedDifficulty) {
            case EASY -> {
                maxEnergySources = 10;
                gameState.getCity().setBudget(9000);
                energySourceLimits = Map.of(
                    EnergySourceType.SOLAR, 4,
                    EnergySourceType.WIND, 4,
                    EnergySourceType.GAS, 2,
                    EnergySourceType.COAL,1,
                    EnergySourceType.BATTERY, 2
                );
            }
            case MEDIUM -> {
                maxEnergySources = 7;
                gameState.getCity().setBudget(4500);
                energySourceLimits = Map.of(
                    EnergySourceType.SOLAR, 2,
                    EnergySourceType.WIND, 3,
                    EnergySourceType.GAS, 1,
                    EnergySourceType.COAL, 1,
                    EnergySourceType.BATTERY, 1
                );
            }
            case HARD -> {
                maxEnergySources = 5;
                gameState.getCity().setBudget(7000);
                energySourceLimits = Map.of(
                    EnergySourceType.SOLAR, 6,
                    EnergySourceType.WIND, 4,
                    EnergySourceType.GAS, 2,
                    EnergySourceType.COAL, 2,
                    EnergySourceType.BATTERY, 2
                );
            }
        }
        for (EnergySourceType t : EnergySourceType.values()) energySourceCounts.put(t, 0);
    }

    private void placeRandomBuildings(int numberOfBuildings) {
        Random rand = new Random();
        int placedCount = 0;

        // Array to store building types, to ensure at least one of each type is placed
        BuildingType[] buildingTypes = {BuildingType.HOUSE, BuildingType.SCHOOL, BuildingType.FACTORY, BuildingType.HOSPITAL};
        Set<BuildingType> placedTypes = new HashSet<>();

        // Ensure one of each building type is placed first
        for (BuildingType type : buildingTypes) {
            // Place one of this building type
            while (true) {
                int row = rand.nextInt(BOARD_ROWS);
                int col = rand.nextInt(BOARD_COLS);
                Tile tile = gameState.getGrid()[row][col];

                if (tile.getType() == TileType.EMPTY) {
                    tile.setType(TileType.CONSUMER);
                    tile.setBuildingType(type);

                    // Set the baseDemand, priority, and pollutionSensitivity based on the building type
                    int baseDemand = switch (type) {
                        case HOUSE -> 15;
                        case SCHOOL -> 25;
                        case HOSPITAL -> 30;
                        case FACTORY -> 50;
                    };

                    int priority = switch (type) {
                        case HOSPITAL -> 1;
                        case SCHOOL -> 2;
                        case HOUSE -> 3;
                        case FACTORY -> 4;
                    };

                    int pollutionSensitivity = switch (type) {
                        case HOSPITAL -> 1;
                        case SCHOOL -> 2;
                        case HOUSE -> 3;
                        case FACTORY -> 4;
                    };

                    String id = type.name() + "" + row + "" + col;
                    Building building = new Building(id, type, baseDemand, priority, pollutionSensitivity, tile);
                    gameState.getCity().addBuilding(building);

                    gameState.getCity().setBaseDemand(gameState.getCity().getBaseDemand() + baseDemand);
                    updateTileUI(row, col);
                    placedCount++;
                    placedTypes.add(type);  // Mark this building type as placed
                    break;  // Exit the loop once the building type is placed
                }
            }
        }

        // After placing one of each building type, randomly place the remaining buildings
        while (placedCount < numberOfBuildings) {
            int row = rand.nextInt(BOARD_ROWS);
            int col = rand.nextInt(BOARD_COLS);
            Tile tile = gameState.getGrid()[row][col];

            if (tile.getType() == TileType.EMPTY) {
                tile.setType(TileType.CONSUMER);
                BuildingType type = randomBuildingType();
                tile.setBuildingType(type);

                // Set the baseDemand, priority, and pollutionSensitivity based on the building type
                int baseDemand = switch (type) {
                    case HOUSE -> 15;
                    case SCHOOL -> 25;
                    case HOSPITAL -> 30;
                    case FACTORY -> 50;
                };

                int priority = switch (type) {
                    case HOSPITAL -> 1;
                    case SCHOOL -> 2;
                    case HOUSE -> 3;
                    case FACTORY -> 4;
                };

                int pollutionSensitivity = switch (type) {
                    case HOSPITAL -> 1;
                    case SCHOOL -> 2;
                    case HOUSE -> 3;
                    case FACTORY -> 4;
                };

                String id = type.name() + "" + row + "" + col;
                Building building = new Building(id, type, baseDemand, priority, pollutionSensitivity, tile);
                gameState.getCity().addBuilding(building);

                gameState.getCity().setBaseDemand(gameState.getCity().getBaseDemand() + baseDemand);
                updateTileUI(row, col);
                placedCount++;
            }
        }
    }

    private BuildingType randomBuildingType() {
        BuildingType[] types = {BuildingType.HOUSE, BuildingType.SCHOOL, BuildingType.FACTORY, BuildingType.HOSPITAL};
        return types[new Random().nextInt(types.length)];
    }

    private void handleTileClick(int row, int col) {
        if (selectedType == null) return;
        Tile tile = gameState.getGrid()[row][col];
        if (tile.getType() != TileType.EMPTY) {
            showMessage("Cannot place here! Tile already occupied.");
            return;
        }
        placeEnergySourceOnTile(tile, selectedType);
    }

    private void placeEnergySourceOnTile(Tile tile, EnergySourceType type) {
        if (energySourceCounts.get(type) >= energySourceLimits.get(type)) {
            showMessage("Limit reached for " + type);
            return;
        }

        EnergySource source = createEnergySource(type,tile);
        if (gameState.getCity().getBudget() < source.getCost()) {
            showMessage("Not enough budget!");
            return;
        }

        gameState.getCity().setBudget(gameState.getCity().getBudget() - source.getCost());
        tile.setEnergySource(source);
        tile.setType(TileType.SOURCE_REF);
        placedTiles.add(tile);
        gameState.addPlacedSource(source);

        energySourceCounts.put(type, energySourceCounts.get(type) + 1);
        maxEnergySources--;

        updateTileUI(tile.getRow(), tile.getCol());
        updateHUD();
    }



    private EnergySource createEnergySource(EnergySourceType type, Tile tile) {
        return switch (type) {
            case SOLAR -> new SolarEnergySource("solar", 700, 30, 1, 0, 0.95, 0.8, 1.2, 12, tile);
            case WIND -> new WindEnergySource("wind", 1000, 40, 1, 0, 0.85, 0.7, 1.3, 18, tile);
            case GAS -> new GasEnergySource("gas", 800, 80, 2, 2, 0.9, 0.8, 1.2, 20, tile);
            case COAL -> new CoalEnergySource("coal", 1500, 80, 2, 5, 0.95, 0.85, 1.3, 25, tile);
            case BATTERY -> new BatteryEnergySource("battery", 100, 0, 5, 0, 0.95, 0.9, 1.1, 8, 100, tile);
            default -> throw new IllegalArgumentException("Unknown energy source type: " + type);
        };
    }


    private Node getTileNode(int row, int col) {
        int index = row * BOARD_COLS + col;
        if (index < 0 || index >= gameGrid.getChildren().size()) return null;
        return gameGrid.getChildren().get(index);
    }

    // ====== HUD ======
    private void updateHUD() {
        demandLabel.setText("Demand: " + gameState.getCity().getBaseDemand());
        budgetLabel.setText("Budget: " + gameState.getCity().getBudget());
        pollutionLabel.setText("Pollution: " + gameState.getCity().getPollution());
    }
    public void updateWeather(Weather weather) {

    	this.currentWeather = weather;

    	weatherLabel.setText("Weather: " + weather.name());

    	}

    @FXML
    private void undoPlacement() {
        if (!placedTiles.isEmpty()) {
            Tile lastTile = placedTiles.remove(placedTiles.size() - 1);
            EnergySource source = lastTile.getEnergySource();

            if (source != null) {
                gameState.getCity().setBudget(gameState.getCity().getBudget() + source.getCost());
                lastTile.setEnergySource(null);
                lastTile.setType(TileType.EMPTY);

                energySourceCounts.put(source.getType(), energySourceCounts.get(source.getType()) - 1);
                maxEnergySources++;

                updateTileUI(lastTile.getRow(), lastTile.getCol());
                updateHUD();
            } else showMessage("No energy source to undo.");
        } else showMessage("No placements to undo.");
    }
     // ====== Reset / Undo / Simulation ======
    @FXML
    private void resetDay() {
    	  int budget = 0;
          switch (selectedDifficulty) {
              case EASY:
                  budget = 9000;
                  break;
              case MEDIUM:
                  budget = 4500;
                  break;
              case HARD:
                  budget = 7000;
                  break;
          }

          // Set the budget and reset pollution
          gameState.getCity().setBudget(budget);
        gameState.getCity().setPollution(0);

        for (int r = 0; r < BOARD_ROWS; r++)
            for (int c = 0; c < BOARD_COLS; c++) {
                Tile t = gameState.getGrid()[r][c];
                t.setEnergySource(null);
                if (t.getType() == TileType.SOURCE_REF) t.setType(TileType.EMPTY);
                updateTileUI(r, c);
            }

        placedTiles.clear();
        for (EnergySourceType t : EnergySourceType.values()) energySourceCounts.put(t, 0);

        maxEnergySources = switch (selectedDifficulty) {
            case EASY -> 10;
            case MEDIUM -> 7;
            case HARD -> 5;
        };

        updateHUD();
    }



 @FXML
    private void runSimulation() {
        disablePlacementButtons();
        SimulationEngine engine = new SimulationEngine();
        ReportModel report = engine.runSimulation(gameState);
        showSimulationResults(report);
    }


 private void showSimulationResults(ReportModel report) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/SimulationResults.fxml"));
            Parent root = loader.load();
            SimulationResultsController controller = loader.getController();
            controller.setData(gameState, report);
            Stage stage = (Stage) gameGrid.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true); 
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}