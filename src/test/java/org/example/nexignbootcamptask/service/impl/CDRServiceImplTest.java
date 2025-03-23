package org.example.nexignbootcamptask.service.impl;

import org.example.nexignbootcamptask.dto.CDRGenerationRequest;
import org.example.nexignbootcamptask.entity.CDRecord;
import org.example.nexignbootcamptask.entity.Subscriber;
import org.example.nexignbootcamptask.entity.enums.CallType;
import org.example.nexignbootcamptask.exception.ResourceNotFoundException;
import org.example.nexignbootcamptask.repository.CDRecordRepository;
import org.example.nexignbootcamptask.repository.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CDRServiceImplTest {

    @Mock
    private CDRecordRepository cdRecordRepository;

    @Mock
    private SubscriberRepository subscriberRepository;

    @InjectMocks
    private CDRServiceImpl cdrService;

    private Subscriber testSubscriber1;
    private Subscriber testSubscriber2;
    private CDRecord testRecord1;
    private CDRecord testRecord2;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.now().minusMonths(1);
        endDate = LocalDateTime.now();

        testSubscriber1 = Subscriber.builder().msisdn("79001112233").build();
        testSubscriber2 = Subscriber.builder().msisdn("79002223344").build();

        testRecord1 = CDRecord.builder()
                .callType(CallType.OUTCOMING)
                .callingSubscriber(testSubscriber1)
                .receivingSubscriber(testSubscriber2)
                .callStart(startDate.plusDays(5))
                .callEnd(startDate.plusDays(5).plusMinutes(5))
                .build();

        testRecord2 = CDRecord.builder()
                .callType(CallType.INCOMING)
                .callingSubscriber(testSubscriber2)
                .receivingSubscriber(testSubscriber1)
                .callStart(startDate.plusDays(10))
                .callEnd(startDate.plusDays(10).plusMinutes(10))
                .build();
    }

    @Test
    void initializeSubscribers_WhenNoSubscribers_ShouldSaveThem() {
        when(subscriberRepository.count()).thenReturn(0L);

        cdrService.initializeSubscribers();

        verify(subscriberRepository).count();
        ArgumentCaptor<List<Subscriber>> subscriberCaptor = ArgumentCaptor.forClass(List.class);
        verify(subscriberRepository).saveAll(subscriberCaptor.capture());

        List<Subscriber> savedSubscribers = subscriberCaptor.getValue();
        assertEquals(10, savedSubscribers.size());
    }

    @Test
    void initializeSubscribers_WhenSubscribersExist_ShouldNotSaveThem() {
        when(subscriberRepository.count()).thenReturn(10L);

        cdrService.initializeSubscribers();

        verify(subscriberRepository).count();
        verify(subscriberRepository, never()).saveAll(any());
    }

    @Test
    void generateCDRecords_Success() {
        List<Subscriber> subscribers = Arrays.asList(testSubscriber1, testSubscriber2);
        when(subscriberRepository.findAll()).thenReturn(subscribers);

        cdrService.generateCDRecords();

        verify(subscriberRepository).findAll();
        verify(cdRecordRepository).saveAll(any());
    }

    @Test
    void generateCDRecords_NoSubscribers_ShouldThrowException() {
        when(subscriberRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(IllegalStateException.class, () -> cdrService.generateCDRecords());
        verify(subscriberRepository).findAll();
        verify(cdRecordRepository, never()).saveAll(any());
    }

    @Test
    void getCDRecordsBySubscriberAndDateRange_Success() {
        String msisdn = "79001112233";
        List<CDRecord> expectedRecords = Arrays.asList(testRecord1, testRecord2);
        when(cdRecordRepository.findAllBySubscriberMsisdnAndDateRange(msisdn, startDate, endDate))
                .thenReturn(expectedRecords);

        List<CDRecord> result = cdrService.getCDRecordsBySubscriberAndDateRange(msisdn, startDate, endDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cdRecordRepository).findAllBySubscriberMsisdnAndDateRange(msisdn, startDate, endDate);
    }

    @Test
    void getCDRecordsBySubscriberAndDateRange_NoRecords_ShouldThrowException() {
        String msisdn = "79001112233";
        when(cdRecordRepository.findAllBySubscriberMsisdnAndDateRange(msisdn, startDate, endDate))
                .thenReturn(new ArrayList<>());

        assertThrows(ResourceNotFoundException.class, () ->
                cdrService.getCDRecordsBySubscriberAndDateRange(msisdn, startDate, endDate));
    }

    @Test
    void getOutgoingCallsBySubscriber_WithoutMonth() {
        String msisdn = "79001112233";
        List<CDRecord> expectedRecords = Arrays.asList(testRecord1);
        when(cdRecordRepository.findByCallingSubscriber_MsisdnAndCallType(msisdn, CallType.OUTCOMING))
                .thenReturn(expectedRecords);

        List<CDRecord> result = cdrService.getOutgoingCallsBySubscriber(msisdn, null);

        assertEquals(1, result.size());
        verify(cdRecordRepository).findByCallingSubscriber_MsisdnAndCallType(msisdn, CallType.OUTCOMING);
        verify(cdRecordRepository, never()).findByCallingSubscriber_MsisdnAndCallTypeAndMonth(any(), any(), any());
    }

    @Test
    void getOutgoingCallsBySubscriber_WithMonth() {
        String msisdn = "79001112233";
        Integer month = 5;
        List<CDRecord> expectedRecords = Arrays.asList(testRecord1);
        when(cdRecordRepository.findByCallingSubscriber_MsisdnAndCallTypeAndMonth(msisdn, CallType.OUTCOMING, month))
                .thenReturn(expectedRecords);

        List<CDRecord> result = cdrService.getOutgoingCallsBySubscriber(msisdn, month);

        assertEquals(1, result.size());
        verify(cdRecordRepository).findByCallingSubscriber_MsisdnAndCallTypeAndMonth(msisdn, CallType.OUTCOMING, month);
        verify(cdRecordRepository, never()).findByCallingSubscriber_MsisdnAndCallType(any(), any());
    }

    @Test
    void getIncomingCallsBySubscriber_WithoutMonth() {
        String msisdn = "79001112233";
        List<CDRecord> expectedRecords = Arrays.asList(testRecord2);
        when(cdRecordRepository.findByReceivingSubscriber_MsisdnAndCallType(msisdn, CallType.INCOMING))
                .thenReturn(expectedRecords);

        List<CDRecord> result = cdrService.getIncomingCallsBySubscriber(msisdn, null);

        assertEquals(1, result.size());
        verify(cdRecordRepository).findByReceivingSubscriber_MsisdnAndCallType(msisdn, CallType.INCOMING);
        verify(cdRecordRepository, never()).findByReceivingSubscriber_MsisdnAndCallTypeAndMonth(any(), any(), any());
    }

    @Test
    void getIncomingCallsBySubscriber_WithMonth() {
        String msisdn = "79001112233";
        Integer month = 5;
        List<CDRecord> expectedRecords = Arrays.asList(testRecord2);
        when(cdRecordRepository.findByReceivingSubscriber_MsisdnAndCallTypeAndMonth(msisdn, CallType.INCOMING, month))
                .thenReturn(expectedRecords);

        List<CDRecord> result = cdrService.getIncomingCallsBySubscriber(msisdn, month);

        assertEquals(1, result.size());
        verify(cdRecordRepository).findByReceivingSubscriber_MsisdnAndCallTypeAndMonth(msisdn, CallType.INCOMING, month);
        verify(cdRecordRepository, never()).findByReceivingSubscriber_MsisdnAndCallType(any(), any());
    }

    @Test
    void generateCDRReport_SubscriberNotFound_ShouldThrowException() {
        String msisdn = "79001112233";
        CDRGenerationRequest request = new CDRGenerationRequest();
        request.setMsisdn(msisdn);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        when(subscriberRepository.existsById(msisdn)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> cdrService.generateCDRReport(request));
        verify(subscriberRepository).existsById(msisdn);
        verify(cdRecordRepository, never()).findAllBySubscriberMsisdnAndDateRange(any(), any(), any());
    }
}
