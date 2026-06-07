package ps.edu.iugaza.notification_service.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookingEventListener {

    private static final Logger logger = LoggerFactory.getLogger(BookingEventListener.class);

    @KafkaListener(topics = "booking-confirmed", groupId = "notification-group")
    public void handleBookingConfirmed(String message) {
        logger.info("Booking confirmed event received: {}", message);
        sendEmailNotification("booking_confirmed", message);
    }

    @KafkaListener(topics = "booking-cancelled", groupId = "notification-group")
    public void handleBookingCancelled(String message) {
        logger.info("Booking cancelled event received: {}", message);
        sendEmailNotification("booking_cancelled", message);
    }

    @KafkaListener(topics = "payment-completed", groupId = "notification-group")
    public void handlePaymentCompleted(String message) {
        logger.info("Payment completed event received: {}", message);
        sendEmailNotification("payment_completed", message);
    }

    @KafkaListener(topics = "payment-failed", groupId = "notification-group")
    public void handlePaymentFailed(String message) {
        logger.info("Payment failed event received: {}", message);
        sendEmailNotification("payment_failed", message);
    }

    @KafkaListener(topics = "customer-registered", groupId = "notification-group")
    public void handleCustomerRegistered(String message) {
        logger.info("Customer registered event received: {}", message);
        sendEmailNotification("customer_registered", message);
    }

    private void sendEmailNotification(String type, String message) {
        // Simulate sending email
        logger.info("Sending {} notification: {}", type, message);
        // In production, integrate with email service (e.g., SendGrid, AWS SES)
    }

}
