package org.example.nexignbootcamptask.controller.impl;

import org.example.nexignbootcamptask.dto.UDRResponse;
import org.example.nexignbootcamptask.exception.ResourceNotFoundException;
import org.example.nexignbootcamptask.service.UDRService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UDRControllerImpl.class)
public class UDRControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UDRService udrService;

    @Test
    void getUDRForSubscriber_Success() throws Exception {
        String msisdn = "79001112233";

        UDRResponse response = UDRResponse.builder()
                .msisdn(msisdn)
                .incomingCall(UDRResponse.CallStats.builder().totalTime("00:10:00").build())
                .outcomingCall(UDRResponse.CallStats.builder().totalTime("00:15:00").build())
                .build();

        when(udrService.getUDRForSubscriber(eq(msisdn), any())).thenReturn(response);

        mockMvc.perform(get("/api/udr/subscriber/{msisdn}", msisdn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value(msisdn))
                .andExpect(jsonPath("$.incomingCall.totalTime").value("00:10:00"))
                .andExpect(jsonPath("$.outcomingCall.totalTime").value("00:15:00"));
    }

    @Test
    void getUDRForSubscriber_WithMonth() throws Exception {
        String msisdn = "79001112233";
        Integer month = 5;

        UDRResponse response = UDRResponse.builder()
                .msisdn(msisdn)
                .incomingCall(UDRResponse.CallStats.builder().totalTime("00:05:00").build())
                .outcomingCall(UDRResponse.CallStats.builder().totalTime("00:08:00").build())
                .build();

        when(udrService.getUDRForSubscriber(eq(msisdn), eq(month))).thenReturn(response);

        mockMvc.perform(get("/api/udr/subscriber/{msisdn}", msisdn)
                        .param("month", month.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value(msisdn))
                .andExpect(jsonPath("$.incomingCall.totalTime").value("00:05:00"))
                .andExpect(jsonPath("$.outcomingCall.totalTime").value("00:08:00"));
    }

    @Test
    void getUDRForSubscriber_NotFound() throws Exception {
        String msisdn = "79999999999";

        when(udrService.getUDRForSubscriber(eq(msisdn), any()))
                .thenThrow(new ResourceNotFoundException("Subscriber", "msisdn", msisdn));

        mockMvc.perform(get("/api/udr/subscriber/{msisdn}", msisdn))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUDRsForMonth_Success() throws Exception {
        Integer month = 5;

        UDRResponse response1 = UDRResponse.builder()
                .msisdn("79001112233")
                .incomingCall(UDRResponse.CallStats.builder().totalTime("00:10:00").build())
                .outcomingCall(UDRResponse.CallStats.builder().totalTime("00:15:00").build())
                .build();

        UDRResponse response2 = UDRResponse.builder()
                .msisdn("79002223344")
                .incomingCall(UDRResponse.CallStats.builder().totalTime("00:05:00").build())
                .outcomingCall(UDRResponse.CallStats.builder().totalTime("00:08:00").build())
                .build();

        List<UDRResponse> responses = Arrays.asList(response1, response2);

        when(udrService.getAllUDRsForMonth(month)).thenReturn(responses);

        mockMvc.perform(get("/api/udr/month/{month}", month))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].msisdn").value("79001112233"))
                .andExpect(jsonPath("$[1].msisdn").value("79002223344"))
                .andExpect(jsonPath("$[0].incomingCall.totalTime").value("00:10:00"))
                .andExpect(jsonPath("$[1].incomingCall.totalTime").value("00:05:00"));
    }

    @Test
    void getAllUDRsForMonth_EmptyList() throws Exception {
        Integer month = 5;

        when(udrService.getAllUDRsForMonth(month)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/udr/month/{month}", month))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
