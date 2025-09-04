package model;

import java.util.Random;

public class GasEnergySource extends EnergySource {
    private static final Random rng = new Random();  // Same RNG instance for the session

    public GasEnergySource(String id, int cost, int baseOutput, int range, int pollutionPerUnit, 
                           double reliability, double minVar, double maxVar, int maintenanceCost,Tile tile) {
        super(id, EnergySourceType.GAS, cost, baseOutput, range, pollutionPerUnit, reliability, minVar, maxVar, maintenanceCost,tile);
    }

    @Override
    public int calculateOutput(Weather weather) {
        double weatherModifier = 1.0;  // Gas is not weather-dependent

        // Reliability and variability adjustment using session-level RNG
        double reliabilityRoll = rng.nextDouble();
        double reliabilityEffect = (reliabilityRoll <= getReliability()) ? 1.0 : 0.6;
        double variability = getMinVar() + (getMaxVar() - getMinVar()) * rng.nextDouble();

        // Calculate final output
        double output = getBaseOutput() * weatherModifier * reliabilityEffect * variability;
        return (int) Math.round(output);
    }
}
