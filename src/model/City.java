package model;

import java.util.ArrayList;
import java.util.List;

public class City {

    private Difficulty difficulty;
    private int baseDemand;
    private int budget;
    private int pollution;
    private Weather weather; // ✅ Add weather field
    private List<Building> buildings;

    // Default constructor
    public City() {
        this.difficulty = Difficulty.EASY;
        this.baseDemand = 100;  // Default base demand for EASY mode
        this.budget = 20000;    // Default budget for EASY mode
        this.pollution = 0;     // No pollution by default
        this.buildings = new ArrayList<>();
        this.weather = Weather.SUNNY;  // Default weather
    }

    // Constructor with difficulty
    public City(Difficulty difficulty) {
        this();  // Call default constructor
        this.difficulty = difficulty;

        // Adjust values based on the difficulty level
        switch (difficulty) {
            case EASY -> {
                this.baseDemand = 0;  // Lower demand for easier gameplay
                this.budget = 6500;    // Generous budget for EASY mode
                this.pollution = 0;     // Minimal pollution to make it easy
            }
            case MEDIUM -> {
                this.baseDemand = 0;  // Moderate demand for balanced gameplay
                this.budget = 4500;    // Tighter budget for moderate challenge
                this.pollution = 5;     // Small pollution increase
            }
            case HARD -> {
                this.baseDemand = 500;  // High demand for challenging gameplay
                this.budget = 8000;     // Very tight budget for HARD mode
                this.pollution = 10;    // Significant pollution increase
            }
        }
    }

    // --- Difficulty ---
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    // --- Demand ---
    public int getBaseDemand() { return baseDemand; }
    public void setBaseDemand(int baseDemand) { this.baseDemand = baseDemand; }

    // --- Budget ---
    public int getBudget() { return budget; }
    public void setBudget(int budget) { this.budget = budget; }

    // --- Pollution ---
    public int getPollution() { return pollution; }
    public void setPollution(int pollution) { this.pollution = pollution; }

    // --- Weather ---
    public Weather getWeather() { return weather; }
    public void setWeather(Weather weather) { this.weather = weather; }

    // --- Buildings ---
    public void addBuilding(Building building) {
        if (building != null) buildings.add(building);
    }

    public List<Building> getBuildings() {
        return buildings;
    }
}
