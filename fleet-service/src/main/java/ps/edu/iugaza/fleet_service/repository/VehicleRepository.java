package ps.edu.iugaza.fleet_service.repository;

import ps.edu.iugaza.fleet_service.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVehicleId(String vehicleId);
    List<Vehicle> findByStatus(Vehicle.VehicleStatus status);
    Optional<Vehicle> findByLicensePlate(String licensePlate);
}
