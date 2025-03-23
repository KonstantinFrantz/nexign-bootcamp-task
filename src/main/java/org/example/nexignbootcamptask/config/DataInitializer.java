package org.example.nexignbootcamptask.config;

import org.example.nexignbootcamptask.service.CDRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final CDRService cdrService;

    @Autowired
    DataInitializer(CDRService cdrService){
        this.cdrService = cdrService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initDatabase() {
        cdrService.initializeSubscribers();
        cdrService.generateCDRecords();
    }
}
