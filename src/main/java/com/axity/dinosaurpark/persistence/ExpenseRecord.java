package com.axity.dinosaurpark.persistence;

import java.time.LocalDateTime;

public record ExpenseRecord(long id, String type, double amount, String description,
                             LocalDateTime timestamp) {
}
