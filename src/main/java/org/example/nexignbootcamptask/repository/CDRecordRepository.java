package org.example.nexignbootcamptask.repository;

import org.example.nexignbootcamptask.entity.CDRecord;
import org.example.nexignbootcamptask.entity.enums.CallType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for CDRecord entity operations.
 */
@Repository
public interface CDRecordRepository extends JpaRepository<CDRecord, Long> {

    /**
     * Retrieves all call records for a specific subscriber within a date range.
     * Finds records where the subscriber is either the caller or receiver.
     *
     * @param msisdn The subscriber's mobile number
     * @param startDate The start date and time for filtering records
     * @param endDate The end date and time for filtering records
     * @return List of CDRecord entities ordered by call start time
     */
    @Query("SELECT r FROM CDRecord r WHERE (r.callingSubscriber.msisdn = :msisdn OR r.receivingSubscriber.msisdn = :msisdn) " +
            "AND r.callStart >= :startDate AND r.callStart <= :endDate ORDER BY r.callStart")
    List<CDRecord> findAllBySubscriberMsisdnAndDateRange(
            @Param("msisdn") String msisdn,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Finds all outgoing calls made by a specific subscriber.
     *
     * @param msisdn The caller's mobile number
     * @param callType The call type (should be OUTCOMING)
     * @return List of CDRecord entities representing outgoing calls
     */
    List<CDRecord> findByCallingSubscriber_MsisdnAndCallType(String msisdn, CallType callType);

    /**
     * Finds all incoming calls received by a specific subscriber.
     *
     * @param msisdn The receiver's mobile number
     * @param callType The call type (should be INCOMING)
     * @return List of CDRecord entities representing incoming calls
     */
    List<CDRecord> findByReceivingSubscriber_MsisdnAndCallType(String msisdn, CallType callType);

    /**
     * Finds all outgoing calls made by a specific subscriber during a particular month.
     *
     * @param msisdn The caller's mobile number
     * @param callType The call type (should be OUTCOMING)
     * @param month The month (1-12) to filter calls by
     * @return List of CDRecord entities representing outgoing calls for the specified month
     */
    @Query("SELECT r FROM CDRecord r WHERE r.callingSubscriber.msisdn = :msisdn " +
            "AND r.callType = :callType AND MONTH(r.callStart) = :month")
    List<CDRecord> findByCallingSubscriber_MsisdnAndCallTypeAndMonth(
            @Param("msisdn") String msisdn,
            @Param("callType") CallType callType,
            @Param("month") Integer month);

    /**
     * Finds all incoming calls received by a specific subscriber during a particular month.
     *
     * @param msisdn The receiver's mobile number
     * @param callType The call type (should be INCOMING)
     * @param month The month (1-12) to filter calls by
     * @return List of CDRecord entities representing incoming calls for the specified month
     */
    @Query("SELECT r FROM CDRecord r WHERE r.receivingSubscriber.msisdn = :msisdn " +
            "AND r.callType = :callType AND MONTH(r.callStart) = :month")
    List<CDRecord> findByReceivingSubscriber_MsisdnAndCallTypeAndMonth(
            @Param("msisdn") String msisdn,
            @Param("callType") CallType callType,
            @Param("month") Integer month);
}
