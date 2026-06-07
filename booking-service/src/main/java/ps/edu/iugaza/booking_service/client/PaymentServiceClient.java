package ps.edu.iugaza.booking_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.fasterxml.jackson.annotation.JsonProperty;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/v1/payments/process")
    Object processPayment(@RequestBody PaymentRequest paymentRequest);

    class PaymentRequest {

        @JsonProperty("bookingId")
        public String bookingId;

        @JsonProperty("customerId")
        public String customerId;

        @JsonProperty("amount")
        public Double amount;

        @JsonProperty("method")
        public String method;

        public PaymentRequest() {}

        public PaymentRequest(String bookingId, String customerId, Double amount, String method) {
            this.bookingId = bookingId;
            this.customerId = customerId;
            this.amount = amount;
            this.method = method;
        }
    }
}