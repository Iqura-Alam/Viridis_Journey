package model;

import java.util.ArrayList;
import java.util.List;

public class SimulationEngine {

    public static ReportModel runSimulation(GameState gameState) {
        System.out.println(">>> SimulationEngine.runSimulation CALLED");

        // --- Step 1: Production Phase ---
        double totalEnergyProduced = 0;
        for (EnergySource energySource : gameState.getPlacedSources()) {
            double output = energySource.calculateOutput(gameState.getCity().getWeather());
            System.out.println("Source " + energySource.getType() + " produced " + output);
            totalEnergyProduced += output;
        }

        // --- Step 2: Distribution Phase ---
        double energyDistributed = distributeEnergy(gameState, totalEnergyProduced);

        // --- Step 3: Battery Rules (Charging/Discharging) ---
        manageBatteries(gameState, totalEnergyProduced, energyDistributed);

        // --- Step 4: Pollution Phase ---
        double pollutionChange = updatePollution(gameState);

        // --- Step 5: Stars Calculation ---
        int stars = calculateStars(gameState, energyDistributed, pollutionChange);

        // --- Step 6: Report Generation ---
        ReportModel report = generateReport(gameState, totalEnergyProduced, energyDistributed, pollutionChange, stars);

        System.out.println(">>> Report generated: Produced=" + report.getEnergyProduced() +
                           " Distributed=" + report.getEnergyDistributed() +
                           " PollutionChange=" + report.getPollutionChange() +
                           " BudgetRemaining=" + report.getBudgetRemaining());
        return report;
    }

    private static double distributeEnergy(GameState gameState, double totalEnergyProduced) {
        double energyDistributed = 0;
        switch (gameState.getCity().getDifficulty()) {
            case EASY:
                energyDistributed = easyDistribution(gameState, totalEnergyProduced);
                break;
            case MEDIUM:
                energyDistributed = mediumDistribution(gameState, totalEnergyProduced);
                break;
            case HARD:
                energyDistributed = hardDistribution(gameState, totalEnergyProduced);
                break;
        }
        return energyDistributed;
    }

    public static double easyDistribution(GameState gameState, double totalEnergyProduced) {
        double demand = gameState.getCity().getBaseDemand();
        return totalEnergyProduced >= demand ? demand : totalEnergyProduced;
    }


    public static double mediumDistribution(GameState gameState, double totalEnergyProduced) {
        double energyDistributed = 0;
        List<Building> buildingsInRange = getBuildingsInRange(gameState);  // Get all buildings in range of energy sources

        // First, fulfill energy demands for priority 1 and 2 buildings
        for (Building building : buildingsInRange) {
            if (totalEnergyProduced <= 0) break;
            
            // Only fulfill demand for priority 1 (Hospital) and 2 (School)
            if (building.getPriority() == 1 || building.getPriority() == 2) {
                double requiredEnergy = building.getBaseDemand();
                double allocated = Math.min(totalEnergyProduced, requiredEnergy);
                energyDistributed += allocated;
                totalEnergyProduced -= allocated;
            }
        }

        // Then, fulfill energy demands for remaining buildings (priority 3 and 4)
        for (Building building : buildingsInRange) {
            if (totalEnergyProduced <= 0) break;
            
            // Skip buildings that already have their demand fulfilled
            if (building.getPriority() == 1 || building.getPriority() == 2) continue;
            
            // Fulfill demand for other buildings
            double requiredEnergy = building.getBaseDemand();
            double allocated = Math.min(totalEnergyProduced, requiredEnergy);
            energyDistributed += allocated;
            totalEnergyProduced -= allocated;
        }

        return energyDistributed;
    }


    public static double hardDistribution(GameState gameState, double totalEnergyProduced) {
        double energyDistributed = 0;
        List<Building> buildingsInRange = getBuildingsInRange(gameState);  // Get all buildings in range of energy sources

        // Fulfill energy demands for all buildings (priority doesn't matter in Hard Mode)
        for (Building building : buildingsInRange) {
            if (totalEnergyProduced <= 0) break;

            double requiredEnergy = building.getBaseDemand();
            double allocated = Math.min(totalEnergyProduced, requiredEnergy);
            energyDistributed += allocated;
            totalEnergyProduced -= allocated;  // Subtract the allocated energy
        }
        return energyDistributed;
    }



    private static List<Building> getBuildingsInRange(GameState gameState) {
        List<Building> buildingsInRange = new ArrayList<>();

        // Loop through all energy sources
        for (EnergySource energySource : gameState.getPlacedSources()) {
            int range = energySource.getRange();  // Get the range of the current energy source
            Tile energySourceTile = energySource.getPosition(); // Get the position (Tile) of the energy source
            int energySourceX = energySourceTile.getRow();  // Get the row (X coordinate) of the energy source
            int energySourceY = energySourceTile.getCol();  // Get the col (Y coordinate) of the energy source

            // Loop over all buildings and check if they are within the range of the current energy source
            for (Building building : gameState.getCity().getBuildings()) {
                Tile buildingTile = building.getPosition();  // Get the position (Tile) of the building
                int buildingX = buildingTile.getRow();  // Get the row (X coordinate) of the building
                int buildingY = buildingTile.getCol();  // Get the col (Y coordinate) of the building
                
                // Calculate the Manhattan distance between the energy source and the building
                int distance = Math.abs(energySourceX - buildingX) + Math.abs(energySourceY - buildingY);
                
                // If the building is within range of this energy source, add it to the list
                if (distance <= range) {
                    buildingsInRange.add(building);
                }
            }
        }
        return buildingsInRange;
    }




    private static void manageBatteries(GameState gameState, double totalEnergyProduced, double energyDistributed) {
        double surplus = totalEnergyProduced - energyDistributed;
        if (surplus > 0) {
            chargeBatteries(gameState, surplus);
        } else {
            dischargeBatteries(gameState, -surplus);
        }
    }

    private static void chargeBatteries(GameState gameState, double surplus) {
        for (EnergySource energySource : gameState.getPlacedSources()) {
            if (energySource instanceof BatteryEnergySource) {
                BatteryEnergySource battery = (BatteryEnergySource) energySource;
                battery.charge((int) surplus);
                surplus -= battery.getChargeLevel();
                if (surplus <= 0) break;
            }
        }
    }

    private static void dischargeBatteries(GameState gameState, double deficit) {
        for (EnergySource energySource : gameState.getPlacedSources()) {
            if (energySource instanceof BatteryEnergySource) {
                BatteryEnergySource battery = (BatteryEnergySource) energySource;
                battery.discharge((int) deficit);
                deficit -= battery.getChargeLevel();
                if (deficit <= 0) break;
            }
        }
    }


    public static double updatePollution(GameState gameState) {
        double pollutionChange = 0;

        // Calculate pollution from all non-battery energy sources
        for (EnergySource source : gameState.getPlacedSources()) {
            if (source instanceof BatteryEnergySource) continue;
            double output = source.calculateOutput(gameState.getCity().getWeather());
            pollutionChange += source.getPollutionPerUnit() * output;
        }

        // Current pollution + change
        int currentPollution = gameState.getCity().getPollution();
        int newPollution = currentPollution + (int) pollutionChange;

        // Cap pollution based on difficulty
        switch (gameState.getCity().getDifficulty()) {
            case EASY -> newPollution = Math.min(newPollution, 100);   // full pollution allowed
            case MEDIUM -> newPollution = Math.min(newPollution, 50);  // limited pollution
            case HARD -> newPollution = Math.max(0, newPollution);     // no pollution allowed, minimum 0
        }

        // Update city pollution
        gameState.getCity().setPollution(newPollution);

        // Return the calculated pollution change for reporting
        return pollutionChange;
    }


    public static int calculateStars(GameState gameState, double energyDistributed, double pollutionChange) {
        double coverage = energyDistributed / gameState.getCity().getBaseDemand();
        int stars = 0; // default to 0 for no success

        switch (gameState.getCity().getDifficulty()) {
            case EASY -> {
                if (coverage >= 1.0) stars = 3;           // full demand met
                else if (coverage >= 0.9) stars = 2;      // almost full
                else if (coverage >= 0.7) stars = 1;      // minimal success
            }
            case MEDIUM -> {
                boolean priorityMet = true;
                for (Building b : gameState.getCity().getBuildings()) {
                    if (b.getPriority() <= 2 && energyDistributed < b.getBaseDemand()) {
                        priorityMet = false;
                        break;
                    }
                }
                if (priorityMet && coverage >= 0.8 && pollutionChange <= 50) stars = 3;
                else if (priorityMet && coverage >= 0.7) stars = 2;
                else if (coverage >= 0.6) stars = 1;
            }
            case HARD -> {
                boolean allMet = true;
                for (Building b : gameState.getCity().getBuildings()) {
                    if (energyDistributed < b.getBaseDemand()) {
                        allMet = false;
                        break;
                    }
                }
                if (allMet && pollutionChange == 0) stars = 3;
                else if (allMet && coverage >= 0.9) stars = 2;
                else if (coverage >= 0.8) stars = 1;
            }
        }

        return stars;
    }



	private static ReportModel generateReport(GameState gameState,
	                                          double totalEnergyProduced,
	                                          double energyDistributed,
	                                          double pollutionChange,
	                                          int stars) {
	    ReportModel report = new ReportModel();
	    report.setEnergyProduced(totalEnergyProduced);
	    report.setEnergyDistributed(energyDistributed);
	    report.setPollutionChange(pollutionChange);
	    report.setStars(stars);
	    report.setScore((int)(gameState.getCity().getBaseDemand() * stars));
	
	    // ✅ NEW: store both initial and remaining budgets
	    report.setInitialBudget(10000); // or gameState.getCity().getInitialBudget() if you track it
	    report.setBudgetRemaining(gameState.getCity().getBudget());
	
	    report.setTotalPollution(gameState.getCity().getPollution());
	
	    if (gameState.getCity().getPollution() >= 90) {
	        report.addEvent("Health Crisis in city!");
	    }
	    if (energyDistributed < gameState.getCity().getBaseDemand() * 0.5) {
	        report.addEvent("Massive blackout affected residents!");
	    }
	    return report;
	}

    
    
}
