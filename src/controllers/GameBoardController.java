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
    	
       setupGameBoard();

        solarButton.setDisable(false);
        windButton.setDisable(false);
        gasButton.setDisable(false);
        coalButton.setDisable(false);
        batteryButton.setDisable(false);
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


}