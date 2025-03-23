package org.example.nexignbootcamptask.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CDRGenerationRequest {
    private String msisdn;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
