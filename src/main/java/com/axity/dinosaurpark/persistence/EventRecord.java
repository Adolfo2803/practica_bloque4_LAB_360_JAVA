package com.axity.dinosaurpark.persistence;

import java.time.LocalDateTime;

public record EventRecord(long step, String eventName, String description, String affectedEntities,
                           LocalDateTime timestamp) {
}
