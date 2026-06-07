package ps.edu.iugaza.booking_service.repository;

import ps.edu.iugaza.booking_service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingId(String bookingId);
    List<Booking> findByCustomerId(String customerId);
    List<Booking> findByStatus(Booking.BookingStatus status);
}
