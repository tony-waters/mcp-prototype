package uk.bit1.mcpprototype.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.bit1.mcpprototype.PostgresIntegrationTest;
import uk.bit1.mcpprototype.refund.RefundEligibilityResult;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SupportDeskToolsTests extends PostgresIntegrationTest {

    @Autowired
    private SupportDeskTools supportDeskTools;

    @Test
    void lookupCustomerIsWiredAndCallable() {
        LookupCustomerResponse response = supportDeskTools.lookupCustomer("sam@example.com");

        assertThat(response.matches())
                .extracting(LookupCustomerResponse.CustomerMatch::customerId)
                .containsExactly("cus_eligible");
    }

    @Test
    void getRecentOrdersIsWiredAndCallable() {
        RecentOrdersResponse response = supportDeskTools.getRecentOrders("cus_eligible", 5);

        assertThat(response.orders())
                .extracting(RecentOrdersResponse.OrderSummary::orderId)
                .contains("ord_eligible");
    }

    @Test
    void checkRefundEligibilityIsWiredAndCallable() {
        RefundEligibilityResult response = supportDeskTools.checkRefundEligibility("cus_eligible", "ord_eligible");

        assertThat(response.eligible()).isTrue();
        assertThat(response.maxRefundMinor()).isEqualTo(7999);
    }

    @Test
    void checkRefundEligibilityRequiresCustomerAndOrderIds() {
        RefundEligibilityResult response = supportDeskTools.checkRefundEligibility("", "");

        assertThat(response.eligible()).isFalse();
        assertThat(response.reason()).isEqualTo("Customer ID and Order ID are required.");
    }
}
