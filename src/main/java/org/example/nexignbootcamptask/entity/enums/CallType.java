package org.example.nexignbootcamptask.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CallType {
    OUTCOMING("01"),
    INCOMING("02");

    private final String index;

    @Override
    public String toString() {
        return this.getIndex();
    }
}
