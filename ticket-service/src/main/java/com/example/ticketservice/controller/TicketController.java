package com.example.ticketservice.controller;

import com.example.ticketservice.model.Ticket;
import com.example.ticketservice.repository.TicketRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Ticket> getTicketByOrderId(@PathVariable Long orderId) {
        return ticketRepository.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Ticket> cancelTicket(@PathVariable Long orderId) {
        return ticketRepository.findByOrderId(orderId).map(ticket -> {
            ticket.setStatus("CANCELED");
            ticketRepository.save(ticket);
            return ResponseEntity.ok(ticket);
        }).orElse(ResponseEntity.notFound().build());
    }
}
