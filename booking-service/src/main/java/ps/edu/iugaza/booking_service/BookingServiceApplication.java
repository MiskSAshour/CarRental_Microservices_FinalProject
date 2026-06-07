package ps.edu.iugaza.booking_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@EnableFeignClients(basePackages = "ps.edu.iugaza.booking_service.client")
@EnableDiscoveryClient
public class BookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }

    @Controller
    public class StatusGraphQLController {

        @QueryMapping
        public String checkStatus() {
            return "Bonus Implementation: GraphQL is Active!";
        }
    }

}
