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