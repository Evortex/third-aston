package com.example.ticketservice.kafka;

import com.example.ticketservice.model.Ticket;
import com.example.ticketservice.repository.TicketRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final TicketRepository ticketRepository;

    public KafkaConsumerService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @KafkaListener(topics = "order-events", groupId = "ticket-group")
    public void processOrder(ConsumerRecord<String, String> record) {
        LOGGER.info("Получен заказ на билет из Kafka: {}", record.value());

        Ticket ticket = new Ticket();
        ticket.setOrderId(Long.parseLong(record.key()));
        ticket.setSeatNumber("A" + (int)(Math.random() * 50)); // Рандомное место
        ticket.setStatus("BOOKED");

        ticketRepository.save(ticket);
        LOGGER.info("Билет создан: {}", ticket);
    }
}
