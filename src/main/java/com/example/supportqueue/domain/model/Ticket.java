package com.example.supportqueue.domain.model;

import com.example.supportqueue.domain.enums.Category;
import com.example.supportqueue.domain.enums.CustomerTier;
import com.example.supportqueue.domain.enums.Severity;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class Ticket {
    private UUID id;
    private OffsetDateTime createdAt;
    private Severity severity;
    private CustomerTier customerTier;
    private Category category;
    private String summary;

    private OffsetDateTime dueAt;
    // Only for example
    private String priorityKey;
}
