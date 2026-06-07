package ps.edu.iugaza.fleet_service.service;

import ps.edu.iugaza.fleet_service.entity.Vehicle;
import ps.edu.iugaza.fleet_service.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FleetService {

    private static final Logger logger = LoggerFactory.getLogger(FleetService.class);

    @Autowired
    private VehicleRepository vehicleRepository;

    public Vehicle addVehicle(Vehicle vehicle) {
        vehicle.setVehicleId("VEH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        logger.info("Adding vehicle: {}", vehicle.getVehicleId());
        return vehicleRepository.save(vehicle);
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Optional<Vehicle> getVehicleByVehicleId(String vehicleId) {
        return vehicleRepository.findByVehicleId(vehicleId);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByStatus(Vehicle.VehicleStatus.AVAILABLE);
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicleDetails) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isPresent()) {
            Vehicle existingVehicle = vehicle.get();
            existingVehicle.setMake(vehicleDetails.getMake());
            existingVehicle.setModel(vehicleDetails.getModel());
            existingVehicle.setYear(vehicleDetails.getYear());
            existingVehicle.setColor(vehicleDetails.getColor());
            existingVehicle.setDailyRate(vehicleDetails.getDailyRate());
            existingVehicle.setMileage(vehicleDetails.getMileage());
            logger.info("Vehicle updated: {}", id);
            return vehicleRepository.save(existingVehicle);
        }
        return null;
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
        logger.info("Vehicle deleted: {}", id);
    }

    public boolean reserveVehicle(String vehicleId) {
        Optional<Vehicle> vehicle = vehicleRepository.findByVehicleId(vehicleId);
        if (vehicle.isPresent() && vehicle.get().getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            Vehicle v = vehicle.get();
            v.setStatus(Vehicle.VehicleStatus.RESERVED);
            vehicleRepository.save(v);
            logger.info("Vehicle reserved: {}", vehicleId);
            return true;
        }
        logger.warn("Failed to reserve vehicle: {}", vehicleId);
        return false;
    }

    public boolean releaseVehicle(String vehicleId) {
        Optional<Vehicle> vehicle = vehicleRepository.findByVehicleId(vehicleId);
        if (vehicle.isPresent()) {
            Vehicle v = vehicle.get();
            v.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            vehicleRepository.save(v);
            logger.info("Vehicle released: {}", vehicleId);
            return true;
        }
        return false;
    }

    public boolean isVehicleAvailable(String vehicleId) {
        Optional<Vehicle> vehicle = vehicleRepository.findByVehicleId(vehicleId);
        boolean available = vehicle.isPresent() && vehicle.get().getStatus() == Vehicle.VehicleStatus.AVAILABLE;
        logger.debug("Vehicle availability check: {} - {}", vehicleId, available);
        return available;
    }

}
