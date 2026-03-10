package com.example.supportqueue.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Severity {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    @JsonValue
    private final int value;

    Severity(int value) {
        this.value = value;
    }

    @JsonCreator
    public static Severity fromValue(int value) {
        for (Severity severity : values()) {
            if (severity.value == value) {
                return severity;
            }
        }
        throw new IllegalArgumentException("Invalid severity value: " + value);
    }
}