package uk.bit1.mcpprototype.order;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerOrderRepository {

    private final JdbcClient jdbcClient;

    CustomerOrderRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<CustomerOrder> findRecentByCustomerId(String customerId, int limit) {
        return jdbcClient.sql("""
                        select id, customer_id, placed_at, delivered_at, status, total_minor, currency, refunded_at
                        from customer_orders
                        where customer_id = :customerId
                        order by placed_at desc
                        limit :limit
                        """)
                .param("customerId", customerId)
                .param("limit", limit)
                .query(CustomerOrder.class)
                .list();
    }

    public Optional<CustomerOrder> findById(String id) {
        return jdbcClient.sql("""
                        select id, customer_id, placed_at, delivered_at, status, total_minor, currency, refunded_at
                        from customer_orders
                        where id = :id
                        """)
                .param("id", id)
                .query(CustomerOrder.class)
                .optional();
    }
}
