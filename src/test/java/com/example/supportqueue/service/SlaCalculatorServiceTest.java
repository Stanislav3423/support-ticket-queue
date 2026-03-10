package com.example.supportqueue.service;

import com.example.supportqueue.domain.enums.CustomerTier;
import com.example.supportqueue.domain.enums.Severity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SlaCalculatorServiceTest {

    private final SlaCalculatorService calculator = new SlaCalculatorService();

    @Test
    @DisplayName("Case 1: Wednesday 10:00 + 4h SLA => Wednesday 14:00")
    void testStandardBusinessHours() {
        // Wednesday, 10:00 Warsaw time
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-03-11T10:00:00+01:00");

        // ENTERPRISE + Severity 1 = 4h SLA
        OffsetDateTime dueAt = calculator.calculateDueDate(createdAt, Severity.ONE, CustomerTier.ENTERPRISE);

        assertEquals(14, dueAt.atZoneSameInstant(ZoneId.of("Europe/Warsaw")).getHour());
        assertEquals(0, dueAt.getMinute());
    }

    @Test
    @DisplayName("Case 2: Wednesday 16:30 + 2h SLA => Thursday 10:30 (Overnight shift)")
    void testOvernightShift() {
        // Wednesday, 16:30
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-03-11T16:30:00+01:00");

        OffsetDateTime dueAt = calculator.calculateDueDate(createdAt, Severity.ONE, CustomerTier.ENTERPRISE);

        // 16:30 + 4h => 30min (until 17:00) + 3h 30min (next day) = 12:30
        assertEquals(12, dueAt.atZoneSameInstant(ZoneId.of("Europe/Warsaw")).getHour());
        assertEquals(30, dueAt.getMinute());
    }

    @Test
    @DisplayName("Case 3: Saturday 11:00 + 8h SLA => Monday 17:00 (Weekend shift)")
    void testWeekendShift() {
        // Saturday, 11:00
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-03-14T11:00:00+01:00");

        // PRO + Severity 1 = 8h SLA
        OffsetDateTime dueAt = calculator.calculateDueDate(createdAt, Severity.ONE, CustomerTier.PRO);

        var warsawDue = dueAt.atZoneSameInstant(ZoneId.of("Europe/Warsaw"));
        assertEquals(17, warsawDue.getHour());
        assertEquals(0, warsawDue.getMinute());
        assertEquals(16, warsawDue.getDayOfMonth());
    }
}
