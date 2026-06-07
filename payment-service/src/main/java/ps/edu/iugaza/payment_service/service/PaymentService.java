package ps.edu.iugaza.payment_service.service;

import ps.edu.iugaza.payment_service.entity.Payment;
import ps.edu.iugaza.payment_service.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    public Payment processPayment(Payment payment) {
        logger.info("Processing payment for booking: {}", payment.getBookingId());

        payment.setPaymentId("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setStatus(Payment.PaymentStatus.PENDING);

        try {
            // Simulate payment processing
            Thread.sleep(500);

            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setCompletedAt(LocalDateTime.now());
            payment.setTransactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());

            Payment savedPayment = paymentRepository.save(payment);
            logger.info("Payment completed: {}", savedPayment.getPaymentId());

            // Publish event
            if (kafkaTemplate != null) {
                try {
                    kafkaTemplate.send("payment-completed", "Payment completed: " + savedPayment.getPaymentId());
                } catch (Exception kafkaEx) {
                    logger.warn("Kafka event failed: {}", kafkaEx.getMessage());
                }
            }

            return savedPayment;
        } catch (Exception e) {
            logger.error("Payment processing failed", e);
            payment.setStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);

            if (kafkaTemplate != null) {
                try {
                    kafkaTemplate.send("payment-failed", "Payment failed: " + payment.getBookingId());
                } catch (Exception kafkaEx) {
                    logger.warn("Kafka event failed: {}", kafkaEx.getMessage());
                }
            }
            throw new RuntimeException("Payment processing failed", e);
        }
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public Optional<Payment> getPaymentByPaymentId(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId);
    }

    public Optional<Payment> getPaymentByBookingId(String bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    public List<Payment> getPaymentsByCustomerId(String customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public void refundPayment(String paymentId) {
        Optional<Payment> payment = paymentRepository.findByPaymentId(paymentId);
        if (payment.isPresent()) {
            Payment p = payment.get();
            if (p.getStatus() == Payment.PaymentStatus.COMPLETED) {
                p.setStatus(Payment.PaymentStatus.REFUNDED);
                paymentRepository.save(p);
                logger.info("Payment refunded: {}", paymentId);

                if (kafkaTemplate != null) {
                    try {
                        kafkaTemplate.send("payment-refunded", "Payment refunded: " + paymentId);
                    } catch (Exception kafkaEx) {
                        logger.warn("Kafka event failed: {}", kafkaEx.getMessage());
                    }
                }
            }
        }
    }

}
