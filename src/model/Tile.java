package model;

public class Tile {

    private int row, col;
    private TileType type; // Tile can be empty, energy source, or consumer (building)
    private BuildingType buildingType; // If the tile has a building, this holds the type of building
    private EnergySource energySource; // If the tile has an energy source, it will be stored here

    // Constructor
    public Tile(int row, int col, TileType type) {
        this.row = row;
        this.col = col;
        this.type = type;
    }

    // Getter and Setter for TileType
    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    // Getter and Setter for BuildingType
    public BuildingType getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(BuildingType buildingType) {
        this.buildingType = buildingType;
    }

    // Getter and Setter for Energy Source
    public EnergySource getEnergySource() {
        return energySource;
    }

    public void setEnergySource(EnergySource energySource) {
        this.energySource = energySource;
    }

    // Getter for row and col
    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
