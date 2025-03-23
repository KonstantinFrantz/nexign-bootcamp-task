package org.example.nexignbootcamptask.util;

import java.time.LocalDateTime;

public class ValidationUtil {

    public static void validateMsisdn(String msisdn) {
        if (msisdn == null || msisdn.isEmpty()) {
            throw new IllegalArgumentException("MSISDN cannot be null or empty");
        }
        if (!msisdn.matches("^7\\d{10}$")) {
            throw new IllegalArgumentException("MSISDN should be in format 7XXXXXXXXXX");
        }
    }

    public static void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        if (endDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("End date cannot be in the future");
        }
    }

    public static void validateMonth(Integer month) {
        if (month != null && (month < 1 || month > 12)) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
    }
}
