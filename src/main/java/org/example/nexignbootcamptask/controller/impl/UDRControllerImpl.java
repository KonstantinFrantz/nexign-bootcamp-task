package org.example.nexignbootcamptask.controller.impl;

import org.example.nexignbootcamptask.controller.UDRController;
import org.example.nexignbootcamptask.dto.UDRResponse;
import org.example.nexignbootcamptask.service.UDRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/udr")
public class UDRControllerImpl implements UDRController {

    private final UDRService udrService;

    @Autowired
    public UDRControllerImpl(UDRService udrService) {
        this.udrService = udrService;
    }

    @GetMapping("/subscriber/{msisdn}")
    public ResponseEntity<UDRResponse> getUDRForSubscriber(
            @PathVariable("msisdn") String msisdn,
            @RequestParam(required = false) Integer month) {
        UDRResponse udrResponse = udrService.getUDRForSubscriber(msisdn, month);
        return ResponseEntity.ok(udrResponse);
    }

    @GetMapping("/month/{month}")
    public ResponseEntity<List<UDRResponse>> getAllUDRsForMonth(
            @PathVariable Integer month) {
        List<UDRResponse> udrResponses = udrService.getAllUDRsForMonth(month);
        return ResponseEntity.ok(udrResponses);
    }
}
