package uk.bit1.mcpprototype.mcp;

import uk.bit1.mcpprototype.customer.Customer;

import java.util.List;

public record LookupCustomerResponse(List<CustomerMatch> matches) {

    static LookupCustomerResponse from(List<Customer> customers) {
        return new LookupCustomerResponse(customers.stream()
                .map(CustomerMatch::from)
                .toList());
    }

    public record CustomerMatch(String customerId, String name, String email, String status, String riskLevel) {

        static CustomerMatch from(Customer customer) {
            return new CustomerMatch(customer.id(), customer.name(), customer.email(), customer.status(), customer.riskLevel());
        }
    }
}
