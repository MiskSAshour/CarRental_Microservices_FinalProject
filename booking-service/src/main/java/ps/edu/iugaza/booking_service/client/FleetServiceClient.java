package ps.edu.iugaza.booking_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "fleet-service")
public interface FleetServiceClient {

    @GetMapping("/api/v1/vehicles/check/{vehicleId}")
    Boolean isVehicleAvailable(@PathVariable String vehicleId);

    @PostMapping("/api/v1/vehicles/reserve/{vehicleId}")
    Boolean reserveVehicle(@PathVariable String vehicleId);

    @PostMapping("/api/v1/vehicles/release/{vehicleId}")
    Boolean releaseVehicle(@PathVariable String vehicleId);

}
