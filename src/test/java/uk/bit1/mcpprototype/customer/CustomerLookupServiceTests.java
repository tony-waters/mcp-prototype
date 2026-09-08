package uk.bit1.mcpprototype.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.bit1.mcpprototype.PostgresIntegrationTest;
import uk.bit1.mcpprototype.order.CustomerOrder;
import uk.bit1.mcpprototype.order.RecentOrderService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CustomerLookupServiceTests extends PostgresIntegrationTest {

    @Autowired
    private CustomerLookupService customerLookupService;

    @Autowired
    private RecentOrderService recentOrderService;

    @Test
    void lookupByExactEmailReturnsCandidatesWithoutChoosingBetweenDuplicates() {
        List<Customer> customers = customerLookupService.lookupByEmail("duplicate@example.com");

        assertThat(customers)
                .extracting(Customer::id)
                .containsExactly("cus_duplicate_a", "cus_duplicate_b");
    }

    @Test
    void recentOrdersIncludeRefundCaseFields() {
        List<CustomerOrder> orders = recentOrderService.getRecentOrders("cus_eligible", 5);

        assertThat(orders).extracting(CustomerOrder::id).contains("ord_eligible");
        CustomerOrder order = orders.stream()
                .filter(candidate -> candidate.id().equals("ord_eligible"))
                .findFirst()
                .orElseThrow();

        assertThat(order.placedAt()).isNotNull();
        assertThat(order.deliveredAt()).isNotNull();
        assertThat(order.status()).isEqualTo("delivered");
        assertThat(order.totalMinor()).isEqualTo(7999);
        assertThat(order.currency()).isEqualTo("GBP");
    }
}
