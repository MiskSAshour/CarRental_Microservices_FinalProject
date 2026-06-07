package ps.edu.iugaza.booking_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerServiceClient {

    @GetMapping("/api/v1/customers/validate/{customerId}")
    Boolean isCustomerActive(@PathVariable("customerId") String customerId);

}
