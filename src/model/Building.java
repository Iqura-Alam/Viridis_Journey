package model;

public class Building {
    private String id;
    private BuildingType type;          // ✅ now uses the standalone enum
    private int baseDemand;
    private int priority;
    private int pollutionSensitivity;
    private Tile position;              // position on the grid

    public Building(String id, BuildingType type, int baseDemand, int priority, int pollutionSensitivity, Tile position) {
        this.id = id;
        this.type = type;
        this.baseDemand = baseDemand;
        this.priority = priority;
        this.pollutionSensitivity = pollutionSensitivity;
        this.position = position;
    }

    // --- Getters ---
    public String getId() { return id; }
    public BuildingType getType() { return type; }
    public int getBaseDemand() { return baseDemand; }
    public int getPriority() { return priority; }
    public int getPollutionSensitivity() { return pollutionSensitivity; }
    public Tile getPosition() { return position; }

    @Override
    public String toString() {
        return "Building{" +
               "id='" + id + '\'' +
               ", type=" + type +
               ", baseDemand=" + baseDemand +
               '}';
    }
}
