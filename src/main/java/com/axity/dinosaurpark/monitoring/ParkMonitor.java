package com.axity.dinosaurpark.monitoring;

import com.axity.dinosaurpark.simulation.ParkState;

public class ParkMonitor {

    private ParkMonitor() {
    }

    public static void displaySnapshot(ParkState state) {
        System.out.println("[Step " + state.getCurrentStep() + "] "
                + "Turistas activos: " + state.countActiveTourists() + " | "
                + "Dinos en recinto: " + state.countDinosaursInEnclosure() + " | "
                + "Energia: " + String.format("%.1f", state.getPowerPlant().getEnergyPercentage()) + "% | "
                + "Eventos activos: " + state.getActiveEventNames() + " | "
                + "Vehiculos no disponibles: " + state.countVehiclesInUse());
    }
}
