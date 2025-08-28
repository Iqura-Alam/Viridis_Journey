package model;

public enum EnergySourceType {
    SOLAR(3), // Max 3 solar sources
    WIND(4),  // Max 4 wind sources
    GAS(2),   // Max 2 gas sources
    COAL(2),  // Max 2 coal sources
    BATTERY(5); // Max 5 battery sources

    private final int maxLimit;

    EnergySourceType(int maxLimit) {
        this.maxLimit = maxLimit;
    }

    public int getMaxLimit() {
        return maxLimit;
    }
}
