package com.example.supportqueue.controller;

import com.example.supportqueue.dto.TicketCreateRequest;
import com.example.supportqueue.domain.enums.CustomerTier;
import com.example.supportqueue.domain.enums.Severity;
import com.example.supportqueue.domain.enums.Category;
import com.example.supportqueue.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        ticketRepository.clear();
    }

    @Test
    @DisplayName("Should sort queue by: dueAt -> severity -> tier -> createdAt")
    void shouldReturnCorrectSortedQueue() throws Exception {
        createTicketRequest(OffsetDateTime.now(), Severity.FIVE, CustomerTier.FREE, "Low priority task");
        createTicketRequest(OffsetDateTime.now(), Severity.ONE, CustomerTier.ENTERPRISE, "Critical Enterprise Bug");
        createTicketRequest(OffsetDateTime.now(), Severity.ONE, CustomerTier.PRO, "Critical Pro Bug");
        createTicketRequest(OffsetDateTime.now().plusMinutes(1), Severity.ONE, CustomerTier.ENTERPRISE, "Another Critical Enterprise Bug");

        mockMvc.perform(get("/queue").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].summary").value("Critical Enterprise Bug"))
                .andExpect(jsonPath("$[1].summary").value("Another Critical Enterprise Bug"))
                .andExpect(jsonPath("$[2].summary").value("Critical Pro Bug"))
                .andExpect(jsonPath("$[3].summary").value("Low priority task"));
    }

    private void createTicketRequest(OffsetDateTime created, Severity sev, CustomerTier tier, String summary) throws Exception {
        TicketCreateRequest request = new TicketCreateRequest();
        request.setCreatedAt(created);
        request.setSeverity(sev);
        request.setCustomerTier(tier);
        request.setCategory(Category.OTHER);
        request.setSummary(summary);

        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return 400 when summary > 200")
    void shouldReturn400WhenSummaryIsTooLong() throws Exception {
        TicketCreateRequest request = new TicketCreateRequest();
        request.setCreatedAt(OffsetDateTime.now());
        request.setSeverity(Severity.ONE);
        request.setCustomerTier(CustomerTier.FREE);
        request.setCategory(Category.OTHER);
        request.setSummary("A".repeat(201));

        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.summary").exists());
    }
}
