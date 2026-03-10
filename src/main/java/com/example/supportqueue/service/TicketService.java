package com.example.supportqueue.service;

import com.example.supportqueue.domain.enums.CustomerTier;
import com.example.supportqueue.domain.model.Ticket;
import com.example.supportqueue.dto.TicketCreateRequest;
import com.example.supportqueue.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final SlaCalculatorService slaCalculatorService;
    
    public Ticket createTicket(TicketCreateRequest request) {
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setCreatedAt(request.getCreatedAt());
        ticket.setSeverity(request.getSeverity());
        ticket.setCustomerTier(request.getCustomerTier());
        ticket.setCategory(request.getCategory());
        ticket.setSummary(request.getSummary());

        ticket.setDueAt(slaCalculatorService.calculateDueDate(
            request.getCreatedAt(), 
            request.getSeverity(), 
            request.getCustomerTier()
        ));

        ticket.setPriorityKey(computePriorityKey(ticket));
        return ticketRepository.save(ticket);
    }
    
    public Optional<Ticket> getTicket(UUID id) {
        return ticketRepository.findById(id);
    }
    
    public List<Ticket> getQueue(int limit) {
        List<Ticket> allTickets = ticketRepository.findAll();
        
        // Sort by priority rules:
        // 1. earliest dueAt first
        // 2. higher severity first (1 before 2 before 3...)
        // 3. higher tier first (ENTERPRISE > PRO > FREE)
        // 4. older createdAt first
        // Disagree
        List<Ticket> sortedQueue = allTickets.stream()
                .sorted(Comparator
                        .comparing(Ticket::getDueAt)
                        .thenComparing(ticket -> ticket.getSeverity().getValue())
                        .thenComparing(ticket -> ticket.getCustomerTier().getValue()) // Ai mistake
                        .thenComparing(Ticket::getCreatedAt)
                )
                .limit(limit)
                .toList();

        return sortedQueue;
    }

    private String computePriorityKey(Ticket ticket) {
        long dueEpoch = ticket.getDueAt().toEpochSecond();
        int severity = ticket.getSeverity().getValue();
        int invertedTier = ticket.getCustomerTier().getValue();  // Ai mistake
        long createdEpoch = ticket.getCreatedAt().toEpochSecond();
        return String.format("%012d-%d-%d-%012d", dueEpoch, severity, invertedTier, createdEpoch);
    }
}
