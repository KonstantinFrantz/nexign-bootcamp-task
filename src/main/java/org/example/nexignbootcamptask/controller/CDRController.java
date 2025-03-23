package org.example.nexignbootcamptask.controller;

import org.example.nexignbootcamptask.dto.CDRGenerationRequest;
import org.example.nexignbootcamptask.dto.CDRGenerationResponse;
import org.springframework.http.ResponseEntity;

/**
 * Interface for the CDR Controller.
 */
public interface CDRController {
    /**
     * Generates a CDR report for a subscriber based on provided criteria.
     *
     * @param request The request containing MSISDN and date range for report generation
     * @return ResponseEntity containing CDRGenerationResponse with request ID
     */
    ResponseEntity<CDRGenerationResponse> generateCDRReport(CDRGenerationRequest request);
}
