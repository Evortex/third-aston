package com.example.paymentservice.kafka;

import com.example.paymentservice.model.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class KafkaConsumerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Payment> kafkaTemplate;

    public KafkaConsumerService(PaymentRepository paymentRepository, KafkaTemplate<String, Payment> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-events", groupId = "payment-group")
    public void processOrder(ConsumerRecord<String, String> record) {
        LOGGER.info("Получен заказ из Kafka: {}", record.value());

        boolean paymentSuccess = new Random().nextBoolean();

        Payment payment = new Payment();
        payment.setOrderId(Long.parseLong(record.key()));
        payment.setAmount(Double.parseDouble(record.value()));
        payment.setStatus(paymentSuccess ? "SUCCESS" : "FAILED");

        paymentRepository.save(payment);

        kafkaTemplate.send("payment-events", payment);
        LOGGER.info("Отправлен статус платежа: {}", payment);
    }
}
