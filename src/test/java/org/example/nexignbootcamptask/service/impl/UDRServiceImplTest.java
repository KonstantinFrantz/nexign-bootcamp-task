package org.example.nexignbootcamptask.service.impl;

import org.example.nexignbootcamptask.dto.UDRResponse;
import org.example.nexignbootcamptask.entity.CDRecord;
import org.example.nexignbootcamptask.entity.Subscriber;
import org.example.nexignbootcamptask.exception.ResourceNotFoundException;
import org.example.nexignbootcamptask.repository.SubscriberRepository;
import org.example.nexignbootcamptask.service.CDRService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UDRServiceImplTest {

    @Mock
    private CDRService cdrService;

    @Mock
    private SubscriberRepository subscriberRepository;

    @InjectMocks
    private UDRServiceImpl udrService;

    private String msisdn;
    private Integer month;
    private CDRecord incomingCall;
    private CDRecord outgoingCall;

    @BeforeEach
    void setUp() {
        msisdn = "79001112233";
        month = 5;

        Subscriber subscriber1 = Subscriber.builder().msisdn(msisdn).build();
        Subscriber subscriber2 = Subscriber.builder().msisdn("79002223344").build();

        LocalDateTime now = LocalDateTime.now();

        incomingCall = CDRecord.builder()
                .callingSubscriber(subscriber2)
                .receivingSubscriber(subscriber1)
                .callStart(now.minusHours(1))
                .callEnd(now.minusHours(1).plusMinutes(2))
                .build();

        outgoingCall = CDRecord.builder()
                .callingSubscriber(subscriber1)
                .receivingSubscriber(subscriber2)
                .callStart(now.minusHours(2))
                .callEnd(now.minusHours(2).plusMinutes(5))
                .build();
    }

    @Test
    void getUDRForSubscriber_Success() {
        when(subscriberRepository.existsById(msisdn)).thenReturn(true);
        when(cdrService.getIncomingCallsBySubscriber(msisdn, month)).thenReturn(Collections.singletonList(incomingCall));
        when(cdrService.getOutgoingCallsBySubscriber(msisdn, month)).thenReturn(Collections.singletonList(outgoingCall));

        UDRResponse response = udrService.getUDRForSubscriber(msisdn, month);

        assertNotNull(response);
        assertEquals(msisdn, response.getMsisdn());
        assertEquals("00:02:00", response.getIncomingCall().getTotalTime());
        assertEquals("00:05:00", response.getOutcomingCall().getTotalTime());

        verify(subscriberRepository).existsById(msisdn);
        verify(cdrService).getIncomingCallsBySubscriber(msisdn, month);
        verify(cdrService).getOutgoingCallsBySubscriber(msisdn, month);
    }

    @Test
    void getUDRForSubscriber_WithNullMonth() {
        when(subscriberRepository.existsById(msisdn)).thenReturn(true);
        when(cdrService.getIncomingCallsBySubscriber(msisdn, null)).thenReturn(Collections.singletonList(incomingCall));
        when(cdrService.getOutgoingCallsBySubscriber(msisdn, null)).thenReturn(Collections.singletonList(outgoingCall));

        UDRResponse response = udrService.getUDRForSubscriber(msisdn, null);

        assertNotNull(response);
        verify(subscriberRepository).existsById(msisdn);
        verify(cdrService).getIncomingCallsBySubscriber(msisdn, null);
        verify(cdrService).getOutgoingCallsBySubscriber(msisdn, null);
    }

    @Test
    void getUDRForSubscriber_SubscriberNotFound() {
        when(subscriberRepository.existsById(msisdn)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> udrService.getUDRForSubscriber(msisdn, month));

        verify(subscriberRepository).existsById(msisdn);
        verify(cdrService, never()).getIncomingCallsBySubscriber(any(), any());
        verify(cdrService, never()).getOutgoingCallsBySubscriber(any(), any());
    }

    @Test
    void getUDRForSubscriber_NoCallRecords() {
        when(subscriberRepository.existsById(msisdn)).thenReturn(true);
        when(cdrService.getIncomingCallsBySubscriber(msisdn, month)).thenReturn(Collections.emptyList());
        when(cdrService.getOutgoingCallsBySubscriber(msisdn, month)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> udrService.getUDRForSubscriber(msisdn, month));

        verify(subscriberRepository).existsById(msisdn);
        verify(cdrService).getIncomingCallsBySubscriber(msisdn, month);
        verify(cdrService).getOutgoingCallsBySubscriber(msisdn, month);
    }

    @Test
    void getAllUDRsForMonth_NoCallRecords() {
        List<Subscriber> subscribers = Collections.singletonList(
                Subscriber.builder().msisdn(msisdn).build());

        when(subscriberRepository.findAll()).thenReturn(subscribers);

        List<UDRResponse> responses = udrService.getAllUDRsForMonth(month);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(subscriberRepository).findAll();
    }

    @Test
    void formatDuration_Test() {
        try {
            java.lang.reflect.Method method = UDRServiceImpl.class.getDeclaredMethod("formatDuration", long.class);
            method.setAccessible(true);

            assertEquals("00:00:30", method.invoke(udrService, 30L));
            assertEquals("00:01:00", method.invoke(udrService, 60L));
            assertEquals("01:30:45", method.invoke(udrService, 5445L));
            assertEquals("12:34:56", method.invoke(udrService, 45296L));

        } catch (Exception e) {
            fail("Exception thrown while testing formatDuration: " + e.getMessage());
        }
    }
}
