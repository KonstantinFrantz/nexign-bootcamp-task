package org.example.nexignbootcamptask.controller;

import org.example.nexignbootcamptask.dto.UDRResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Interface for the UDR Controller.
 */
public interface UDRController {
    /**
     * Retrieves call usage statistics for a specific subscriber.
     *
     * @param msisdn The subscriber's mobile number
     * @param month Optional month filter (1-12)
     * @return ResponseEntity containing UDRResponse with usage statistics
     */
    ResponseEntity<UDRResponse> getUDRForSubscriber(String msisdn, Integer month);

    /**
     * Retrieves call usage statistics for all subscribers for a specific month.
     *
     * @param month The month (1-12) to retrieve statistics for
     * @return ResponseEntity containing a list of UDRResponse objects with usage statistics
     */
    ResponseEntity<List<UDRResponse>> getAllUDRsForMonth(Integer month);
}
