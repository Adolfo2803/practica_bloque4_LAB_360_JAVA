package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Ticket;
import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.model.TouristStatus;
import com.axity.dinosaurpark.persistence.DatabaseService;
import com.axity.dinosaurpark.persistence.RevenueRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArrivalZone implements ParkZone {

    private final int maxCapacity;
    private final double ticketPrice;
    private final List<Tourist> waitingQueue;
    private long ticketCounter;

    public ArrivalZone(int maxCapacity, double ticketPrice) {
        this.maxCapacity = maxCapacity;
        this.ticketPrice = ticketPrice;
        this.waitingQueue = new ArrayList<>();
        this.ticketCounter = 0;
    }

    public void addToQueue(Tourist tourist) {
        waitingQueue.add(tourist);
    }

    public List<Ticket> processBatch(int batchSize, DatabaseService db) {
        List<Ticket> tickets = new ArrayList<>();
        int count = Math.min(batchSize, waitingQueue.size());

        for (int i = 0; i < count; i++) {
            Tourist tourist = waitingQueue.remove(0);
            ticketCounter++;
            Ticket ticket = new Ticket(ticketCounter, tourist.getId(), ticketPrice, "GENERAL",
                    LocalDateTime.now());
            tourist.setStatus(TouristStatus.IN_PARK);
            tourist.spend(ticketPrice);
            tourist.recordVisit(getName());
            tickets.add(ticket);

            db.appendRevenue(new RevenueRecord(ticketCounter, "BOLETO", ticketPrice,
                    tourist.getId(), getName(), LocalDateTime.now()));
        }
        return tickets;
    }

    @Override
    public String getName() {
        return "Zona de Llegada";
    }

    @Override
    public boolean hasCapacity() {
        return waitingQueue.size() < maxCapacity;
    }

    @Override
    public int getCurrentOccupancy() {
        return waitingQueue.size();
    }

    @Override
    public int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public void enter(Tourist tourist) {
        addToQueue(tourist);
    }

    @Override
    public void exit(Tourist tourist) {
        waitingQueue.remove(tourist);
    }
}
