package org.example.nexignbootcamptask.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {"79001112233", "79999999999"})
    void validateMsisdn_ValidMsisdn_NoException(String msisdn) {
        assertDoesNotThrow(() -> ValidationUtil.validateMsisdn(msisdn));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "7900111223", "790011122334", "7900111223a", "89001112233"})
    void validateMsisdn_InvalidMsisdn_ThrowsException(String msisdn) {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtil.validateMsisdn(msisdn));

        assertEquals("MSISDN should be in format 7XXXXXXXXXX", exception.getMessage());
    }

    @Test
    void validateMsisdn_NullMsisdn_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtil.validateMsisdn(null));

        assertEquals("MSISDN cannot be null or empty", exception.getMessage());
    }

    @Test
    void validateDateRange_ValidDateRange_NoException() {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);

        assertDoesNotThrow(() -> ValidationUtil.validateDateRange(startDate, endDate));
    }

    @Test
    void validateDateRange_NullStartDate_ThrowsException() {
        LocalDateTime endDate = LocalDateTime.now();

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtil.validateDateRange(null, endDate));

        assertEquals("Start date and end date cannot be null", exception.getMessage());
    }

    @Test
    void validateDateRange_NullEndDate_ThrowsException() {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtil.validateDateRange(startDate, null));

        assertEquals("Start date and end date cannot be null", exception.getMessage());
    }

    @Test
    void validateDateRange_EndDateBeforeStartDate_ThrowsException() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusMonths(1);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtil.validateDateRange(startDate, endDate));

        assertEquals("Start date cannot be after end date", exception.getMessage());
    }

    @Test
    void validateDateRange_EndDateInFuture_ThrowsException() {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtil.validateDateRange(startDate, endDate));

        assertEquals("End date cannot be in the future", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 6, 12})
    void validateMonth_ValidMonth_NoException(int month) {
        assertDoesNotThrow(() -> ValidationUtil.validateMonth(month));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 13, -1, 100})
    void validateMonth_InvalidMonth_ThrowsException(int month) {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtil.validateMonth(month));

        assertEquals("Month must be between 1 and 12", exception.getMessage());
    }

    @Test
    void validateMonth_NullMonth_NoException() {
        assertDoesNotThrow(() -> ValidationUtil.validateMonth(null));
    }
}
