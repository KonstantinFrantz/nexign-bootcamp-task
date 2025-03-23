package org.example.nexignbootcamptask.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.nexignbootcamptask.dto.CDRGenerationRequest;
import org.example.nexignbootcamptask.dto.CDRGenerationResponse;
import org.example.nexignbootcamptask.exception.ResourceNotFoundException;
import org.example.nexignbootcamptask.service.CDRService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CDRControllerImpl.class)
public class CDRControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CDRService cdrService;

    @Test
    void generateCDRReport_Success() throws Exception {
        String msisdn = "79001112233";
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now();

        CDRGenerationRequest request = new CDRGenerationRequest();
        request.setMsisdn(msisdn);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        UUID requestId = UUID.randomUUID();
        CDRGenerationResponse response = CDRGenerationResponse.builder()
                .requestId(requestId)
                .build();

        when(cdrService.generateCDRReport(any(CDRGenerationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/cdr/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId.toString()));
    }

    @Test
    void generateCDRReport_SubscriberNotFound() throws Exception {
        String msisdn = "79001112233";
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now();

        CDRGenerationRequest request = new CDRGenerationRequest();
        request.setMsisdn(msisdn);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        when(cdrService.generateCDRReport(any(CDRGenerationRequest.class)))
                .thenThrow(new ResourceNotFoundException("Subscriber", "msisdn", msisdn));

        mockMvc.perform(post("/api/cdr/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
