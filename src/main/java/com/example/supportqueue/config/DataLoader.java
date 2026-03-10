package com.example.supportqueue.config;
import com.example.supportqueue.domain.model.Ticket;
import com.example.supportqueue.dto.TicketCreateRequest;
import com.example.supportqueue.service.TicketService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private final TicketService ticketService;
    private final ObjectMapper objectMapper;

    @Bean
    CommandLineRunner initDataLoader() {
        return args -> {
            try {
                loadSampleTickets();
            } catch (Exception e) {
                log.error("Failed to load sample tickets", e);
            }
        };
    }

    private void loadSampleTickets() throws IOException {
        ClassPathResource resource = new ClassPathResource("tickets.sample.json");

        if (!resource.exists()) {
            log.info("Sample tickets file not found, skipping data loading");
            return;
        }

        log.info("Loading sample tickets from tickets.sample.json");

        List<TicketCreateRequest> ticketRequests = objectMapper.readValue(
                resource.getInputStream(),
                new TypeReference<List<TicketCreateRequest>>() {}
        );

        for (TicketCreateRequest request : ticketRequests) {
            try {
                Ticket createdTicket = ticketService.createTicket(request);
                log.info("Loaded sample ticket: {} - {}",
                        createdTicket.getId(), createdTicket.getSummary());
            } catch (Exception e) {
                log.error("Failed to create ticket: {}", request.getSummary(), e);
            }
        }

        log.info("Successfully loaded {} sample tickets", ticketRequests.size());
    }
}
