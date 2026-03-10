package com.example.supportqueue.dto;

import com.example.supportqueue.domain.enums.Category;
import com.example.supportqueue.domain.enums.CustomerTier;
import com.example.supportqueue.domain.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TicketCreateRequest {
    
    @NotNull(message = "CreatedAt is required")
    private OffsetDateTime createdAt;
    
    @NotNull(message = "Severity is required")
    private Severity severity;
    
    @NotNull(message = "Customer tier is required")
    private CustomerTier customerTier;
    
    @NotNull(message = "Category is required")
    private Category category;
    
    @NotBlank(message = "Summary is required")
    @Size(max = 200, message = "Summary must not exceed 200 characters")
    private String summary;
}
