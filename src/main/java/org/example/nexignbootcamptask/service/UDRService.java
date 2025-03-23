package org.example.nexignbootcamptask.service;

import org.example.nexignbootcamptask.dto.UDRResponse;
import org.example.nexignbootcamptask.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Interface for UDR Service.
 * Provides operations for calculating and retrieving call usage statistics
 * for subscribers.
 */
public interface UDRService {
    /**
     * Retrieves call usage statistics for a specific subscriber.
     * Calculates total durations for incoming and outgoing calls.
     *
     * @param msisdn The subscriber's mobile number
     * @param month Optional month filter (1-12)
     * @return UDRResponse containing formatted call statistics
     * @throws ResourceNotFoundException if subscriber or records are not found
     */
    UDRResponse getUDRForSubscriber(String msisdn, Integer month);

    /**
     * Retrieves call usage statistics for all subscribers for a specific month.
     * Aggregates call data and formats durations.
     *
     * @param month The month (1-12) to retrieve statistics for
     * @return List of UDRResponse objects with usage statistics
     */
    List<UDRResponse> getAllUDRsForMonth(Integer month);
}
