package uk.bit1.mcpprototype.customer;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepository {

    private final JdbcClient jdbcClient;

    CustomerRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Customer> findByEmail(String email, int limit) {
        return jdbcClient.sql("""
                        select id, name, email, status, risk_level
                        from customers
                        where email = :email
                        order by id
                        limit :limit
                        """)
                .param("email", email)
                .param("limit", limit)
                .query(Customer.class)
                .list();
    }

    public Optional<Customer> findById(String id) {
        return jdbcClient.sql("""
                        select id, name, email, status, risk_level
                        from customers
                        where id = :id
                        """)
                .param("id", id)
                .query(Customer.class)
                .optional();
    }
}
