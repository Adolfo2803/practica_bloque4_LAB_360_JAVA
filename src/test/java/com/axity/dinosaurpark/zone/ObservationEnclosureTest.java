package com.axity.dinosaurpark.zone;

import com.axity.dinosaurpark.model.Tourist;
import com.axity.dinosaurpark.model.TouristStatus;
import com.axity.dinosaurpark.persistence.DatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class ObservationEnclosureTest {

    private ObservationEnclosure basicEnclosure;
    private ObservationEnclosure vipEnclosure;
    private DatabaseService db;

    @BeforeEach
    void setUp() {
        basicEnclosure = new ObservationEnclosure("Recinto Basico", ExperienceType.BASIC, 20, 10.0);
        vipEnclosure = new ObservationEnclosure("Recinto VIP", ExperienceType.VIP, 5, 75.0);
        db = Mockito.mock(DatabaseService.class);
    }

    @Test
    void visit_chargesEntryFee() {
        Tourist tourist = new Tourist(1, "Ana");
        tourist.setStatus(TouristStatus.IN_PARK);
        Random rng = new Random(42);

        basicEnclosure.visit(tourist, rng, db, 0.0);

        assertEquals(10.0, tourist.getMoneySpent());
        verify(db).appendRevenue(any());
    }

    @Test
    void visit_appliesDiscount() {
        Tourist tourist = new Tourist(1, "Ana");
        tourist.setStatus(TouristStatus.IN_PARK);
        Random rng = new Random(42);

        vipEnclosure.visit(tourist, rng, db, 0.30);

        assertEquals(52.5, tourist.getMoneySpent(), 0.01);
    }

    @Test
    void visit_failsWhenFull() {
        Random rng = new Random(42);
        ObservationEnclosure tiny = new ObservationEnclosure("Tiny", ExperienceType.BASIC, 1, 10.0);

        Tourist t1 = new Tourist(1, "A");
        t1.setStatus(TouristStatus.IN_PARK);
        tiny.enter(t1);

        Tourist t2 = new Tourist(2, "B");
        t2.setStatus(TouristStatus.IN_PARK);
        boolean result = tiny.visit(t2, rng, db, 0.0);

        assertFalse(result);
    }

    @Test
    void conductSurvey_returnsScoreInRange() {
        Tourist tourist = new Tourist(1, "Ana");
        Random rng = new Random(42);

        for (int i = 0; i < 100; i++) {
            int score = basicEnclosure.conductSurvey(tourist, rng);
            assertTrue(score >= 1 && score <= 3);
        }
    }

    @Test
    void conductSurvey_vipReturnsHigherRange() {
        Tourist tourist = new Tourist(1, "Ana");
        Random rng = new Random(42);

        for (int i = 0; i < 100; i++) {
            int score = vipEnclosure.conductSurvey(tourist, rng);
            assertTrue(score >= 3 && score <= 5);
        }
    }

    @Test
    void getName_returnsConfiguredName() {
        assertEquals("Recinto Basico", basicEnclosure.getName());
        assertEquals("Recinto VIP", vipEnclosure.getName());
    }

    @Test
    void getMaxCapacity_returnsConfiguredValue() {
        assertEquals(20, basicEnclosure.getMaxCapacity());
        assertEquals(5, vipEnclosure.getMaxCapacity());
    }

    @Test
    void getExperienceType_returnsCorrectType() {
        assertEquals(ExperienceType.BASIC, basicEnclosure.getExperienceType());
        assertEquals(ExperienceType.VIP, vipEnclosure.getExperienceType());
    }

    @Test
    void getEntryFee_returnsConfiguredValue() {
        assertEquals(10.0, basicEnclosure.getEntryFee());
        assertEquals(75.0, vipEnclosure.getEntryFee());
    }

    @Test
    void exit_removesVisitor() {
        Tourist tourist = new Tourist(1, "Ana");
        basicEnclosure.enter(tourist);
        assertEquals(1, basicEnclosure.getCurrentOccupancy());
        basicEnclosure.exit(tourist);
        assertEquals(0, basicEnclosure.getCurrentOccupancy());
    }

    @Test
    void conductSurvey_premiumReturnsCorrectRange() {
        ObservationEnclosure premium = new ObservationEnclosure("Premium", ExperienceType.PREMIUM, 12, 30.0);
        Tourist tourist = new Tourist(1, "Ana");
        Random rng = new Random(42);

        for (int i = 0; i < 100; i++) {
            int score = premium.conductSurvey(tourist, rng);
            assertTrue(score >= 2 && score <= 4);
        }
    }
}
