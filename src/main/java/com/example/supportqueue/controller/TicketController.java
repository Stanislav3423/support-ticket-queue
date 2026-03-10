package com.example.supportqueue.controller;

import com.example.supportqueue.domain.model.Ticket;
import com.example.supportqueue.dto.TicketCreateRequest;
import com.example.supportqueue.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Ticket Management", description = "API for managing support tickets")
public class TicketController {
    
    private final TicketService ticketService;
    
    @PostMapping("/tickets")
    @Operation(summary = "Create a new ticket")
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody TicketCreateRequest request) {
        Ticket createdTicket = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }
    
    @GetMapping("/tickets/{id}")
    @Operation(summary = "Get ticket by ID")
    public ResponseEntity<Ticket> getTicket(@PathVariable UUID id) {
        return ticketService.getTicket(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/queue")
    @Operation(summary = "Get prioritized queue of tickets")
    public ResponseEntity<List<Ticket>> getQueue(@RequestParam(defaultValue = "50") int limit) {
        List<Ticket> queue = ticketService.getQueue(limit);
        return ResponseEntity.ok(queue);
    }
}
