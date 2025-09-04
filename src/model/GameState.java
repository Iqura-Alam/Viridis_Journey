package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState {
    private String currentScreen;
    private City city;
    private Tile[][] grid;              // 10x10 grid
    private List<EnergySource> placedSources; 
    private Random rngSeed;
    private int score;
    private int stars;
    private List<String> history;

    // --- Progression fields ---
    private int currentDay;
    private int maxDays = 3;            // ✅ survive 3 days to win
    private int retriesLeft;

    // --- Main Constructor (flexible) ---
    public GameState(String currentScreen, City city, Tile[][] grid,
                     List<EnergySource> placedSources, int score, int stars, List<String> history) {
        this.currentScreen = currentScreen;
        this.city = city;
        this.grid = grid != null ? grid : new Tile[10][10];
        this.placedSources = (placedSources != null) ? placedSources : new ArrayList<>();
        this.rngSeed = new Random();
        this.score = score;
        this.stars = stars;
        this.history = (history != null) ? history : new ArrayList<>();

        this.currentDay = 1;

        // ✅ retries depend on difficulty
        switch (city.getDifficulty()) {
            case EASY -> this.retriesLeft = 3;
            case MEDIUM -> this.retriesLeft = 2;
            case HARD -> this.retriesLeft = 1;
        }
    }

    // --- Convenience Constructor (new game setup) ---
    public GameState(City city) {
        this("DaySetup.fxml", city, new Tile[10][10], null, 0, 0, null);
    }

    // --- Getters ---
    public String getCurrentScreen() { return currentScreen; }
    public City getCity() { return city; }
    public Tile[][] getGrid() { return grid; }
    public List<EnergySource> getPlacedSources() { return placedSources; }
    public int getScore() { return score; }
    public int getStars() { return stars; }
    public List<String> getHistory() { return history; }
    public int getCurrentDay() { return currentDay; }
    public int getMaxDays() { return maxDays; }
    public int getRetriesLeft() { return retriesLeft; }

    // --- Setters ---
    public void setCurrentScreen(String currentScreen) { this.currentScreen = currentScreen; }
    public void setScore(int score) { this.score = score; }
    public void setStars(int stars) { this.stars = stars; }
    public void setCurrentDay(int day) { this.currentDay = day; }
    public void setRetriesLeft(int retries) { this.retriesLeft = retries; }
    public void setCity(City city) { this.city = city; }

    // --- Helpers ---
    public void addPlacedSource(EnergySource src) {
        if (src != null) placedSources.add(src);
    }

    public void addHistory(String event) {
        if (event != null && !event.isBlank()) history.add(event);
    }

    // --- Day Progression ---
    public void advanceDay() {
        Difficulty diff = city.getDifficulty();

        // Increase demand based on difficulty
        double growthRate = switch (diff) {
            case EASY -> 0.10;
            case MEDIUM -> 0.12;
            case HARD -> 0.15;
        };

        // ✅ smoother incremental growth
        int newDemand = city.getBaseDemand() + (int) Math.ceil(city.getBaseDemand() * growthRate);
        city.setBaseDemand(newDemand);

        // Adjust budget (player always under ideal)
        int budgetBoost = (int) (newDemand * 80); // tweak balance
        city.setBudget(city.getBudget() + budgetBoost);

        // Advance day
        currentDay++;
    }

    // --- Game state checks ---
    public boolean hasWon() {
        return currentDay > maxDays;
    }

    public boolean hasRetriesLeft() {
        return retriesLeft > 0;
    }
}
