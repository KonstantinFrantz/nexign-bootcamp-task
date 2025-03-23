package org.example.nexignbootcamptask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UDRResponse {
    private String msisdn;
    private CallStats incomingCall;
    private CallStats outcomingCall;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CallStats {
        private String totalTime;
    }
}
