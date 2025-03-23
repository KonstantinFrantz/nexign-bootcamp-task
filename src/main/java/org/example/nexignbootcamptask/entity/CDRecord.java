package org.example.nexignbootcamptask.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.nexignbootcamptask.entity.enums.CallType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CDRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CallType callType;

    @ManyToOne
    @JoinColumn(name = "calling_subscriber_msisdn")
    private Subscriber callingSubscriber;

    @ManyToOne
    @JoinColumn(name = "receiving_subscriber_msisdn")
    private Subscriber receivingSubscriber;

    private LocalDateTime callStart;
    private LocalDateTime callEnd;

    public long getDurationInSeconds() {
        return ChronoUnit.SECONDS.between(callStart, callEnd);
    }
}
