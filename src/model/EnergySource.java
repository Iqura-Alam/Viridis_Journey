package model;

import java.util.Random;

public abstract class EnergySource {
    private String id;
    private EnergySourceType type;
    private int cost;
    private int baseOutput;
    private int range;
    private int pollutionPerUnit;
    private double reliability;
    private double minVar;
    private double maxVar;
    private int maintenanceCost;
    private Tile position;  // Tile to store the position of the energy source (row, column)
    
    

    // Constructor with position as a Tile
    public EnergySource(String id, EnergySourceType type, int cost, int baseOutput, int range, 
                        int pollutionPerUnit, double reliability, double minVar, double maxVar, 
                        int maintenanceCost, Tile position) {
        this.id = id;
        this.type = type;
        this.cost = cost;
        this.baseOutput = baseOutput;
        this.range = range;
        this.pollutionPerUnit = pollutionPerUnit;
        this.reliability = reliability;
        this.minVar = minVar;
        this.maxVar = maxVar;
        this.maintenanceCost = maintenanceCost;
        this.position = position; 
        // Set the position (Tile)
    }

    // Getters
    public String getId() { return id; }
    public EnergySourceType getType() { return type; }
    public int getCost() { return cost; }
    public int getBaseOutput() { return baseOutput; }
    public int getRange() { return range; }
    public int getPollutionPerUnit() { return pollutionPerUnit; }
    public double getReliability() { return reliability; }
    public double getMinVar() { return minVar; }
    public double getMaxVar() { return maxVar; }
    public int getMaintenanceCost() { return maintenanceCost; }

    // Get position of the energy source
    public Tile getPosition() { return position; }

    // Abstract method for calculating energy output based on weather
    public abstract int calculateOutput(Weather weather);

    @Override
    public String toString() {
        return "EnergySource{" + "id='" + id + '\'' + ", type=" + type + ", baseOutput=" + baseOutput + "}";
    }
}
