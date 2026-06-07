package ps.edu.iugaza.fleet_service.controller;

import ps.edu.iugaza.fleet_service.entity.Vehicle;
import ps.edu.iugaza.fleet_service.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/vehicles")
public class FleetController {

    @Autowired
    private FleetService fleetService;

    @PostMapping
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        Vehicle addedVehicle = fleetService.addVehicle(vehicle);
        return new ResponseEntity<>(addedVehicle, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        Optional<Vehicle> vehicle = fleetService.getVehicleById(id);
        return vehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/vehicleId/{vehicleId}")
    public ResponseEntity<Vehicle> getVehicleByVehicleId(@PathVariable String vehicleId) {
        Optional<Vehicle> vehicle = fleetService.getVehicleByVehicleId(vehicleId);
        return vehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = fleetService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Vehicle>> getAvailableVehicles() {
        List<Vehicle> vehicles = fleetService.getAvailableVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicleDetails) {
        Vehicle updatedVehicle = fleetService.updateVehicle(id, vehicleDetails);
        if (updatedVehicle != null) {
            return ResponseEntity.ok(updatedVehicle);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        fleetService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reserve/{vehicleId}")
    public ResponseEntity<Boolean> reserveVehicle(@PathVariable String vehicleId) {
        boolean reserved = fleetService.reserveVehicle(vehicleId);
        return ResponseEntity.ok(reserved);
    }

    @PostMapping("/release/{vehicleId}")
    public ResponseEntity<Boolean> releaseVehicle(@PathVariable String vehicleId) {
        boolean released = fleetService.releaseVehicle(vehicleId);
        return ResponseEntity.ok(released);
    }

    @GetMapping("/check/{vehicleId}")
    public ResponseEntity<Boolean> isVehicleAvailable(@PathVariable String vehicleId) {
        boolean available = fleetService.isVehicleAvailable(vehicleId);
        return ResponseEntity.ok(available);
    }

}
