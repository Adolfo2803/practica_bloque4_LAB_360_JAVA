package com.axity.dinosaurpark.simulation;

import com.axity.dinosaurpark.config.ParkConfig;
import com.axity.dinosaurpark.event.*;
import com.axity.dinosaurpark.model.*;
import com.axity.dinosaurpark.monitoring.ParkMonitor;
import com.axity.dinosaurpark.persistence.DatabaseService;
import com.axity.dinosaurpark.persistence.ExpenseRecord;
import com.axity.dinosaurpark.zone.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulationEngine {

    private final ParkConfig config;
    private final ParkState state;
    private final ArrivalZone arrivalZone;
    private final CentralHub centralHub;
    private final BathroomZone bathroomZone;
    private final PowerPlant powerPlant;
    private final List<ObservationEnclosure> enclosures;
    private final List<SimulationEvent> allEvents;
    private final List<Guard> guards;
    private final List<Technician> technicians;
    private final int totalSteps;
    private final int arrivalBatchSize;
    private final int monitoringInterval;

    public SimulationEngine(ParkConfig config) {
        this.config = config;
        this.totalSteps = config.getTotalSteps();
        this.arrivalBatchSize = config.getInt("simulation.arrivalBatchSize", 5);
        this.monitoringInterval = config.getInt("monitoring.intervalSteps", 10);

        DatabaseService db = new DatabaseService(config.getString("db.path", "./data/parkdb"));
        Random rng = new Random();

        List<Tourist> tourists = createTourists();
        List<Dinosaur> dinosaurs = createDinosaurs();
        List<Vehicle> vehicles = createVehicles();

        this.powerPlant = new PowerPlant(
                config.getDouble("powerplant.initialEnergy", 100.0),
                config.getDouble("powerplant.consumptionPerStep", 1.5),
                config.getDouble("powerplant.failureProbability", 0.05),
                config.getDouble("powerplant.maintenanceCost", 200.0),
                config.getDouble("powerplant.repairCost", 500.0));

        this.state = new ParkState(tourists, dinosaurs, vehicles, powerPlant, db, rng);

        this.arrivalZone = new ArrivalZone(
                config.getInt("arrival.maxCapacity", 30),
                config.getDouble("arrival.ticketPrice", 25.0));

        this.centralHub = new CentralHub(
                config.getDouble("hub.souvenirPrice", 15.0),
                config.getDouble("hub.souvenirPurchaseProbability", 0.4));

        this.bathroomZone = new BathroomZone(
                config.getInt("bathroom.maxCapacity", 10),
                config.getInt("bathroom.useDurationSteps", 3),
                config.getDouble("bathroom.spaPrice", 20.0),
                config.getDouble("bathroom.spaPurchaseProbability", 0.2));

        this.enclosures = createEnclosures();
        this.guards = createGuards();
        this.technicians = createTechnicians();
        this.allEvents = createEvents();

        for (Tourist tourist : tourists) {
            arrivalZone.addToQueue(tourist);
        }
    }

    public void run() {
        System.out.println("=== Inicio de la simulacion del Parque de Dinosaurios ===");
        System.out.println("Steps totales: " + totalSteps);
        System.out.println();

        for (int step = 0; step < totalSteps; step++) {
            state.clearActiveEvents();

            // A. LLEGADAS
            arrivalZone.processBatch(arrivalBatchSize, state.getDb());

            // B. MOVIMIENTO
            moveTourists();

            // C. TICKS ZONAS
            bathroomZone.tick();
            powerPlant.tick(state.getRng(), state.getDb());
            for (Vehicle vehicle : state.getVehicles()) {
                vehicle.tick();
            }

            // D. EVENTOS
            checkAndFireEvents();

            // E. WORKERS
            processWorkers();

            // F. MONITOREO
            if (step % monitoringInterval == 0) {
                ParkMonitor.displaySnapshot(state);
            }

            state.incrementStep();
        }

        printFinalSummary();
        state.getDb().close();
    }

    private void moveTourists() {
        Random rng = state.getRng();
        double discount = state.getCurrentDiscount();

        for (Tourist tourist : state.getTourists()) {
            if (tourist.getStatus() != TouristStatus.IN_PARK) {
                continue;
            }

            double choice = rng.nextDouble();
            if (choice < 0.4) {
                centralHub.visit(tourist, rng, state.getDb(), discount);
            } else if (choice < 0.6) {
                bathroomZone.tryEnter(tourist, rng, state.getDb(), discount);
            } else {
                visitRandomEnclosure(tourist, rng, discount);
            }
        }
    }

    private void visitRandomEnclosure(Tourist tourist, Random rng, double discount) {
        if (enclosures.isEmpty()) {
            return;
        }
        ObservationEnclosure enclosure = enclosures.get(rng.nextInt(enclosures.size()));
        enclosure.visit(tourist, rng, state.getDb(), discount);
    }

    private void checkAndFireEvents() {
        for (SimulationEvent event : allEvents) {
            if (state.getRng().nextDouble() < event.getProbability()) {
                event.execute(state, state.getRng());
                state.addActiveEvent(event.getName());
            }
        }
    }

    private void processWorkers() {
        for (Guard guard : guards) {
            guard.recaptureEscapedDinosaurs(state.getDinosaurs());
        }

        for (Technician tech : technicians) {
            if (!powerPlant.isOperational()) {
                boolean repaired = tech.repairIfNeeded(false, state.getVehicles());
                if (repaired) {
                    powerPlant.repair();
                }
            }
        }

        double totalSalary = (guards.size() + technicians.size())
                * config.getDouble("workers.dailySalary", 150.0);
        state.addExpense(totalSalary);
        state.getDb().appendExpense(new ExpenseRecord(0, "SALARIOS", totalSalary,
                "Salarios diarios de trabajadores", LocalDateTime.now()));
    }

    private void printFinalSummary() {
        System.out.println();
        System.out.println("=== Resumen final de la simulacion ===");
        System.out.println("Steps ejecutados: " + totalSteps);
        System.out.println("Ingresos totales: $" + String.format("%.2f", state.getTotalRevenue()));
        System.out.println("Gastos totales: $" + String.format("%.2f", state.getTotalExpenses()));
        System.out.println("Balance: $" + String.format("%.2f",
                state.getTotalRevenue() - state.getTotalExpenses()));
        System.out.println("Turistas activos al final: " + state.countActiveTourists());
        System.out.println("Dinosaurios en recinto: " + state.countDinosaursInEnclosure());
    }

    private List<Tourist> createTourists() {
        int count = config.getInt("tourists", 50);
        List<Tourist> tourists = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            tourists.add(new Tourist(i, "Turista-" + i));
        }
        return tourists;
    }

    private List<Dinosaur> createDinosaurs() {
        List<Dinosaur> dinosaurs = new ArrayList<>();
        int carnivores = config.getInt("dinosaurs.carnivores", 5);
        int herbivores = config.getInt("dinosaurs.herbivores", 15);

        for (int i = 1; i <= carnivores; i++) {
            dinosaurs.add(new CarnivoreDinosaur(i, "Carnivoro-" + i, "T-Rex"));
        }
        for (int i = 1; i <= herbivores; i++) {
            dinosaurs.add(new HerbivoreDinosaur(carnivores + i, "Herbivoro-" + i, "Triceratops"));
        }
        return dinosaurs;
    }

    private List<Vehicle> createVehicles() {
        int count = config.getInt("vehicles.count", 4);
        int repairSteps = config.getInt("vehicles.repairSteps", 5);
        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            vehicles.add(new Vehicle(i, repairSteps));
        }
        return vehicles;
    }

    private List<ObservationEnclosure> createEnclosures() {
        List<ObservationEnclosure> list = new ArrayList<>();
        list.add(new ObservationEnclosure("Recinto Basico", ExperienceType.BASIC,
                config.getInt("enclosure.basic.maxVisitors", 20),
                config.getDouble("enclosure.basic.entryFee", 10.0)));
        list.add(new ObservationEnclosure("Recinto Premium", ExperienceType.PREMIUM,
                config.getInt("enclosure.premium.maxVisitors", 12),
                config.getDouble("enclosure.premium.entryFee", 30.0)));
        list.add(new ObservationEnclosure("Recinto VIP", ExperienceType.VIP,
                config.getInt("enclosure.vip.maxVisitors", 5),
                config.getDouble("enclosure.vip.entryFee", 75.0)));
        return list;
    }

    private List<Guard> createGuards() {
        int count = config.getInt("workers.guards", 3);
        double salary = config.getDouble("workers.dailySalary", 150.0);
        List<Guard> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(new Guard(i, "Guardia-" + i, salary));
        }
        return list;
    }

    private List<Technician> createTechnicians() {
        int count = config.getInt("workers.technicians", 2);
        double salary = config.getDouble("workers.dailySalary", 150.0);
        List<Technician> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(new Technician(i, "Tecnico-" + i, salary));
        }
        return list;
    }

    private List<SimulationEvent> createEvents() {
        List<SimulationEvent> events = new ArrayList<>();
        events.add(new DinosaurEscapeEvent(config.getDouble("event.escape.probability", 0.05)));
        events.add(new BlackoutEvent(config.getDouble("event.blackout.probability", 0.03)));
        events.add(new StormEvent(config.getDouble("event.storm.probability", 0.04)));
        events.add(new DealsHourEvent(config.getDouble("event.deals.probability", 0.08)));
        events.add(new VehicleFailureEvent(config.getDouble("event.vehicleFailure.probability", 0.06)));
        return events;
    }

    public ParkState getState() {
        return state;
    }
}
