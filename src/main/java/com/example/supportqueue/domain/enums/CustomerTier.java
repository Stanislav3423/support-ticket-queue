package com.example.supportqueue.domain.enums;

import lombok.Getter;

@Getter
public enum CustomerTier {
    ENTERPRISE(1),
    PRO(2),
    FREE(3);

    private final int value;

    CustomerTier(int value) {
        this.value = value;
    }
}
