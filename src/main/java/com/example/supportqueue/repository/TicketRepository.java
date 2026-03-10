package com.example.supportqueue.repository;

import com.example.supportqueue.domain.model.Ticket;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TicketRepository {
    
    private final ConcurrentHashMap<UUID, Ticket> tickets = new ConcurrentHashMap<>();
    
    public Ticket save(Ticket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(UUID.randomUUID());
        }
        tickets.put(ticket.getId(), ticket);
        return ticket;
    }
    
    public Optional<Ticket> findById(UUID id) {
        return Optional.ofNullable(tickets.get(id));
    }
    
    public List<Ticket> findAll() {
        return new ArrayList<>(tickets.values());
    }

    public void deleteById(UUID id) {
        tickets.remove(id);
    }
    
    public boolean existsById(UUID id) {
        return tickets.containsKey(id);
    }
    
    public long count() {
        return tickets.size();
    }
    
    public void clear() {
        tickets.clear();
    }
}
