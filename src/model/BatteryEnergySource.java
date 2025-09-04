package model;

import java.util.Random;

public class BatteryEnergySource extends EnergySource {
    private static final Random rng = new Random();  // Same RNG instance for the session

    private int capacity;
    private int chargeLevel;

    public BatteryEnergySource(String id, int cost, int baseOutput, int range, int pollutionPerUnit, 
                               double reliability, double minVar, double maxVar, int maintenanceCost, int capacity,Tile tile) {
        super(id, EnergySourceType.BATTERY, cost, baseOutput, range, pollutionPerUnit, reliability, minVar, maxVar, maintenanceCost,tile);
        this.capacity = capacity;
        this.chargeLevel = 0;  // Initially empty
    }

    @Override
    public int calculateOutput(Weather weather) {
        // Batteries don't generate energy, they only store it
        return chargeLevel;  // Return the current charge level
    }

    public void charge(int amount) {
        this.chargeLevel = Math.min(this.chargeLevel + amount, capacity);
    }

    public void discharge(int amount) {
        this.chargeLevel = Math.max(this.chargeLevel - amount, 0);
    }

    public int getChargeLevel() { return chargeLevel; }

    public int getCapacity() { return capacity; }
}
