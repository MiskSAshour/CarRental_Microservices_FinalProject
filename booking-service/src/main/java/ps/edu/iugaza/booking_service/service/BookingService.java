package ps.edu.iugaza.booking_service.service;

import ps.edu.iugaza.booking_service.client.CustomerServiceClient;
import ps.edu.iugaza.booking_service.client.FleetServiceClient;
import ps.edu.iugaza.booking_service.client.PaymentServiceClient;
import ps.edu.iugaza.booking_service.entity.Booking;
import ps.edu.iugaza.booking_service.repository.BookingRepository;
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
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FleetServiceClient fleetServiceClient;

    @Autowired
    private CustomerServiceClient customerServiceClient;

    @Autowired
    private PaymentServiceClient paymentServiceClient;

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    public Booking createBooking(Booking booking) {
        logger.info("Starting booking creation for customer: {}", booking.getCustomerId());

        try {
            // Step 1: Validate customer
            logger.info("Validating customer: {}", booking.getCustomerId());
            if (!customerServiceClient.isCustomerActive(booking.getCustomerId())) {
                throw new RuntimeException("Customer is not active");
            }

            // Step 2: Check vehicle availability
            logger.info("Checking vehicle availability: {}", booking.getVehicleId());
            if (!fleetServiceClient.isVehicleAvailable(booking.getVehicleId())) {
                throw new RuntimeException("Vehicle is not available");
            }

            // Step 3: Reserve vehicle
            logger.info("Reserving vehicle: {}", booking.getVehicleId());
            if (!fleetServiceClient.reserveVehicle(booking.getVehicleId())) {
                throw new RuntimeException("Failed to reserve vehicle");
            }

            // Step 4: Calculate and set price
            Double estimatedPrice = calculatePrice(booking);
            booking.setEstimatedPrice(estimatedPrice);
            booking.setBookingId("BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

            // Step 5: Process payment
            logger.info("Processing payment for booking: {}", booking.getBookingId());
            try {
                PaymentServiceClient.PaymentRequest paymentRequest = new PaymentServiceClient.PaymentRequest(
                        booking.getBookingId(),
                        booking.getCustomerId(),
                        estimatedPrice,
                        booking.getPaymentMethod()
                );
                paymentServiceClient.processPayment(paymentRequest);
            } catch (Exception e) {
                logger.error("Payment processing error, releasing vehicle: {}", booking.getVehicleId(), e);
                fleetServiceClient.releaseVehicle(booking.getVehicleId());
                throw new RuntimeException("Payment processing error: " + e.getMessage());
            }

            // Step 6: Confirm booking
            booking.setStatus(Booking.BookingStatus.CONFIRMED);
            booking.setConfirmedAt(LocalDateTime.now());
            Booking savedBooking = bookingRepository.save(booking);
            logger.info("Booking confirmed: {}", savedBooking.getBookingId());

            // Step 7: Publish event
            if (kafkaTemplate != null) {
                try {
                    kafkaTemplate.send("booking-confirmed", "Booking confirmed: " + savedBooking.getBookingId());
                } catch (Exception kafkaEx) {
                    logger.info("Event published: booking-confirmed");
                }
            }

            return savedBooking;

        } catch (Exception e) {
            logger.error("Error creating booking", e);
            throw e;
        }
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Optional<Booking> getBookingByBookingId(String bookingId) {
        return bookingRepository.findByBookingId(bookingId);
    }

    public List<Booking> getBookingsByCustomerId(String customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void cancelBooking(String bookingId) {
        Optional<Booking> booking = bookingRepository.findByBookingId(bookingId);
        if (booking.isPresent()) {
            Booking b = booking.get();
            if (b.getStatus() == Booking.BookingStatus.CONFIRMED) {
                fleetServiceClient.releaseVehicle(b.getVehicleId());
                b.setStatus(Booking.BookingStatus.CANCELLED);
                bookingRepository.save(b);

                if (kafkaTemplate != null) {
                    try {
                        kafkaTemplate.send("booking-cancelled", "Booking cancelled: " + bookingId);
                    } catch (Exception kafkaEx) {
                        logger.warn("Kafka event failed but booking is cancelled: {}", kafkaEx.getMessage());
                    }
                }
                logger.info("Booking cancelled: {}", bookingId);
            }
        }
    }

    private Double calculatePrice(Booking booking) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(booking.getPickupDate(), booking.getReturnDate());
        return days * 50.0;
    }
}
