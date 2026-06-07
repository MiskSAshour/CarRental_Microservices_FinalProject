package ps.edu.iugaza.payment_service.repository;

import ps.edu.iugaza.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);
    Optional<Payment> findByBookingId(String bookingId);
    List<Payment> findByCustomerId(String customerId);
    List<Payment> findByStatus(Payment.PaymentStatus status);
}
