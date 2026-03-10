package com.example.supportqueue.service;

import com.example.supportqueue.domain.enums.CustomerTier;
import com.example.supportqueue.domain.enums.Severity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class SlaCalculatorService {
    
    private static final ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");
    private static final int BUSINESS_HOUR_START = 9;
    private static final int BUSINESS_HOUR_END = 17;

    private static final int[][] SLA_HOURS = {
            // Severity: 1,  2,  3,   4,   5
            {4,   8,  24,  72,  120},// ENTERPRISE
            {8,   16, 48,  96,  168}, // PRO
            {24,  48, 72,  120, 240} // FREE
            // Ai mistake
    };
    
    public OffsetDateTime calculateDueDate(OffsetDateTime createdAt, Severity severity, CustomerTier customerTier) {
        ZonedDateTime warsawTime = createdAt.atZoneSameInstant(WARSAW_ZONE);
        int slaHours = getSlaHours(severity, customerTier);

        ZonedDateTime startTime = normalizeToBusinessHours(warsawTime);

        ZonedDateTime dueTime = addBusinessHours(startTime, slaHours);
        
        return dueTime.toOffsetDateTime();
    }
    
    private int getSlaHours(Severity severity, CustomerTier customerTier) {
        int severityIndex = severity.getValue() - 1;
        int tierIndex = customerTier.getValue() - 1;
        return SLA_HOURS[tierIndex][severityIndex];
    }
    
    private ZonedDateTime normalizeToBusinessHours(ZonedDateTime dateTime) {
        // Weekend, move to next Monday 9:00
        if (dateTime.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return dateTime.plusDays(2).withHour(BUSINESS_HOUR_START).withMinute(0).withSecond(0).withNano(0);
        }
        if (dateTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return dateTime.plusDays(1).withHour(BUSINESS_HOUR_START).withMinute(0).withSecond(0).withNano(0);
        }
        
        // Before business hours, move to 9:00 same day
        if (dateTime.getHour() < BUSINESS_HOUR_START) {
            return dateTime.withHour(BUSINESS_HOUR_START).withMinute(0).withSecond(0).withNano(0);
        }
        
        // After business hours,move to next day 9:00
        if (dateTime.getHour() >= BUSINESS_HOUR_END) {
            ZonedDateTime nextDay = dateTime.plusDays(1);
            while (nextDay.getDayOfWeek() == DayOfWeek.SATURDAY || 
                   nextDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
                nextDay = nextDay.plusDays(1);
            }
            return nextDay.withHour(BUSINESS_HOUR_START).withMinute(0).withSecond(0).withNano(0);
        }
        
        // Within business hours, normalize to current minute
        return dateTime.withSecond(0).withNano(0);
    }

    private ZonedDateTime addBusinessHours(ZonedDateTime startTime, int businessHours) {
        ZonedDateTime current = startTime;
        int minutesToAdd = businessHours * 60;

        while (minutesToAdd > 0) {
            current = current.plusMinutes(1);
            minutesToAdd--;

            // Jump to the next day ONLY if it is 5:00 Only if we still need to add minutes (minutesToAdd > 0)
            if (current.getHour() >= BUSINESS_HOUR_END && minutesToAdd > 0) {
                current = current.plusDays(1).withHour(BUSINESS_HOUR_START).withMinute(0);

                while (current.getDayOfWeek() == DayOfWeek.SATURDAY ||
                        current.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    current = current.plusDays(1);
                }
            }
        }
        return current;
    }
}
