package uk.bit1.mcpprototype.customer;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CustomerLookupService {

    private static final int DEFAULT_CANDIDATE_LIMIT = 10;

    private final CustomerRepository customerRepository;

    CustomerLookupService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> lookupByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return List.of();
        }
        return customerRepository.findByEmail(email.trim(), DEFAULT_CANDIDATE_LIMIT);
    }
}
