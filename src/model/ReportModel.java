package model;

import java.util.ArrayList;
import java.util.List;

public class ReportModel {
    private double energyProduced;
    private double energyDistributed;
    private double pollutionChange;
    private int totalPollution;
    private int stars;
    private int score;
    private int budgetRemaining;

    // ✅ NEW
    private int initialBudget;

    private List<String> triggeredEvents = new ArrayList<>();

    // --- Getters/Setters ---
    public double getEnergyProduced() { return energyProduced; }
    public void setEnergyProduced(double energyProduced) { this.energyProduced = energyProduced; }

    public double getEnergyDistributed() { return energyDistributed; }
    public void setEnergyDistributed(double energyDistributed) { this.energyDistributed = energyDistributed; }

    public double getPollutionChange() { return pollutionChange; }
    public void setPollutionChange(double pollutionChange) { this.pollutionChange = pollutionChange; }

    public int getTotalPollution() { return totalPollution; }
    public void setTotalPollution(int totalPollution) { this.totalPollution = totalPollution; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getBudgetRemaining() { return budgetRemaining; }
    public void setBudgetRemaining(int budgetRemaining) { this.budgetRemaining = budgetRemaining; }

    // ✅ NEW
    public int getInitialBudget() { return initialBudget; }
    public void setInitialBudget(int initialBudget) { this.initialBudget = initialBudget; }

    public List<String> getTriggeredEvents() { return triggeredEvents; }
    public void addEvent(String event) { this.triggeredEvents.add(event); }
}
