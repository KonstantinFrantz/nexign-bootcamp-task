package org.example.nexignbootcamptask.controller.impl;

import org.example.nexignbootcamptask.controller.CDRController;
import org.example.nexignbootcamptask.dto.CDRGenerationRequest;
import org.example.nexignbootcamptask.dto.CDRGenerationResponse;
import org.example.nexignbootcamptask.service.CDRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cdr")
public class CDRControllerImpl implements CDRController {

    private final CDRService cdrService;

    @Autowired
    public CDRControllerImpl(CDRService cdrService) {
        this.cdrService = cdrService;
    }

    @PostMapping("/generate")
    public ResponseEntity<CDRGenerationResponse> generateCDRReport(@RequestBody CDRGenerationRequest request) {
        CDRGenerationResponse response = cdrService.generateCDRReport(request);
        return ResponseEntity.ok(response);
    }
}
