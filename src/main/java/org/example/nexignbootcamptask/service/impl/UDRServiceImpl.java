package org.example.nexignbootcamptask.service.impl;

import org.example.nexignbootcamptask.dto.UDRResponse;
import org.example.nexignbootcamptask.entity.CDRecord;
import org.example.nexignbootcamptask.entity.Subscriber;
import org.example.nexignbootcamptask.exception.ResourceNotFoundException;
import org.example.nexignbootcamptask.repository.SubscriberRepository;
import org.example.nexignbootcamptask.service.CDRService;
import org.example.nexignbootcamptask.service.UDRService;
import org.example.nexignbootcamptask.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UDRServiceImpl implements UDRService {
    private final CDRService cdrService;
    private final SubscriberRepository subscriberRepository;

    @Autowired
    public UDRServiceImpl(CDRService cdrService,
                          SubscriberRepository subscriberRepository) {
        this.cdrService = cdrService;
        this.subscriberRepository = subscriberRepository;
    }

    public UDRResponse getUDRForSubscriber(String msisdn, Integer month) {
        ValidationUtil.validateMsisdn(msisdn);
        if (month != null) {
            ValidationUtil.validateMonth(month);
        }

        if (!subscriberRepository.existsById(msisdn)) {
            throw new ResourceNotFoundException("Subscriber", "msisdn", msisdn);
        }

        List<CDRecord> incomingCalls = cdrService.getIncomingCallsBySubscriber(msisdn, month);
        List<CDRecord> outgoingCalls = cdrService.getOutgoingCallsBySubscriber(msisdn, month);

        if (incomingCalls.isEmpty() && outgoingCalls.isEmpty()) {
            throw new ResourceNotFoundException("CDR records", "subscriber", msisdn);
        }

        long incomingSeconds = calculateTotalCallTime(incomingCalls);
        String incomingFormatted = formatDuration(incomingSeconds);

        long outgoingSeconds = calculateTotalCallTime(outgoingCalls);
        String outgoingFormatted = formatDuration(outgoingSeconds);

        return UDRResponse.builder()
                .msisdn(msisdn)
                .incomingCall(UDRResponse.CallStats.builder().totalTime(incomingFormatted).build())
                .outcomingCall(UDRResponse.CallStats.builder().totalTime(outgoingFormatted).build())
                .build();
    }

    public List<UDRResponse> getAllUDRsForMonth(Integer month) {
        ValidationUtil.validateMonth(month);

        List<Subscriber> allSubscribers = subscriberRepository.findAll();
        List<UDRResponse> udrResponses = new ArrayList<>();

        for (Subscriber subscriber : allSubscribers) {
            try {
                UDRResponse udr = getUDRForSubscriber(subscriber.getMsisdn(), month);
                udrResponses.add(udr);
            } catch (ResourceNotFoundException ignored) {}
        }

        return udrResponses;
    }

    private long calculateTotalCallTime(List<CDRecord> records) {
        return records.stream()
                .mapToLong(CDRecord::getDurationInSeconds)
                .sum();
    }

    private String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}