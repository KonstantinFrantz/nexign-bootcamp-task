package org.example.nexignbootcamptask.service;

import org.example.nexignbootcamptask.dto.CDRGenerationRequest;
import org.example.nexignbootcamptask.dto.CDRGenerationResponse;
import org.example.nexignbootcamptask.entity.CDRecord;
import org.example.nexignbootcamptask.exception.ResourceNotFoundException;
import org.example.nexignbootcamptask.exception.ServiceException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for CDR Service.
 * Provides operations for managing subscribers, generating CDR records,
 * retrieving call data, and creating reports.
 */
public interface CDRService {
    /**
     * Initializes the system with default subscribers if none exist.
     */
    void initializeSubscribers();

    /**
     * Generates random CDR records for testing and development purposes.
     * Creates between 500-1000 random call records distributed among subscribers.
     * @throws IllegalStateException if no subscribers exist.
     */
    void generateCDRecords();

    /**
     * Retrieves call records for a specific subscriber within a date range.
     *
     * @param msisdn The subscriber's mobile number
     * @param startDate The start date and time for filtering records
     * @param endDate The end date and time for filtering records
     * @return List of CDRecord entities matching the criteria
     * @throws ResourceNotFoundException if no records are found
     */
    List<CDRecord> getCDRecordsBySubscriberAndDateRange(String msisdn, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Retrieves outgoing call records for a specific subscriber.
     *
     * @param msisdn The subscriber's mobile number
     * @param month Optional month filter (1-12)
     * @return List of CDRecord entities representing outgoing calls
     */
    List<CDRecord> getOutgoingCallsBySubscriber(String msisdn, Integer month);

    /**
     * Retrieves incoming call records for a specific subscriber.
     *
     * @param msisdn The subscriber's mobile number
     * @param month Optional month filter (1-12)
     * @return List of CDRecord entities representing incoming calls
     */
    List<CDRecord> getIncomingCallsBySubscriber(String msisdn, Integer month);

    /**
     * Generates a CDR report for a subscriber based on provided criteria.
     *
     * @param request The request containing MSISDN and date range for report generation
     * @return CDRGenerationResponse containing the unique UUID for the generated report
     * @throws ResourceNotFoundException if the subscriber does not exist
     * @throws ServiceException if report generation fails
     */
    CDRGenerationResponse generateCDRReport(CDRGenerationRequest request);
}

