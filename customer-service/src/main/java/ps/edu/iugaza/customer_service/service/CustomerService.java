package ps.edu.iugaza.customer_service.service;

import ps.edu.iugaza.customer_service.entity.Customer;
import ps.edu.iugaza.customer_service.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        customer.setCustomerId("CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer created: {}", savedCustomer.getCustomerId());
        return savedCustomer;
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> getCustomerByCustomerId(String customerId) {
        return customerRepository.findByCustomerId(customerId);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            Customer existingCustomer = customer.get();
            existingCustomer.setName(customerDetails.getName());
            existingCustomer.setEmail(customerDetails.getEmail());
            existingCustomer.setPhone(customerDetails.getPhone());
            existingCustomer.setAddress(customerDetails.getAddress());
            existingCustomer.setCity(customerDetails.getCity());
            existingCustomer.setCountry(customerDetails.getCountry());
            logger.info("Customer updated: {}", id);
            return customerRepository.save(existingCustomer);
        }
        return null;
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
        logger.info("Customer deleted: {}", id);
    }

    public boolean isCustomerActive(String customerId) {
        Optional<Customer> customer = customerRepository.findByCustomerId(customerId);
        boolean active = customer.isPresent() && customer.get().getIsActive();
        logger.debug("Customer active check: {} - {}", customerId, active);
        return active;
    }
}
