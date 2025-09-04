package model;

import java.util.Random;

public class SolarEnergySource extends EnergySource {
    private static final Random rng = new Random();  // Single RNG instance for the session

    public SolarEnergySource(String id, int cost, int baseOutput, int range, int pollutionPerUnit, 
            double reliability, double minVar, double maxVar, int maintenanceCost,Tile tile) {
super(id, EnergySourceType.SOLAR, cost, baseOutput, range, pollutionPerUnit, reliability, minVar, maxVar, maintenanceCost,tile);
}

    @Override
    public int calculateOutput(Weather weather) {
        double weatherModifier = 1.0;

        if (weather == Weather.SUNNY) {
            weatherModifier = 1.3;  // Boost solar output on sunny days
        } else if (weather == Weather.CALM || weather == Weather.STORMY) {
            weatherModifier = 0.8;  // Reduce output on cloudy or stormy days
        }

        // Reliability and variability adjustment using session-level RNG
        double reliabilityRoll = rng.nextDouble();  // Using the same RNG instance throughout the session
        double reliabilityEffect = (reliabilityRoll <= getReliability()) ? 1.0 : 0.6;
        double variability = getMinVar() + (getMaxVar() - getMinVar()) * rng.nextDouble(); // Random variability per session

        // Calculate final output
        double output = getBaseOutput() * weatherModifier * reliabilityEffect * variability;
        return (int) Math.round(output);
    }
}
