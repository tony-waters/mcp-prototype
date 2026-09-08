package uk.bit1.mcpprototype.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.bit1.mcpprototype.customer.Customer;
import uk.bit1.mcpprototype.customer.CustomerLookupService;
import uk.bit1.mcpprototype.order.CustomerOrder;
import uk.bit1.mcpprototype.order.RecentOrderService;
import uk.bit1.mcpprototype.refund.RefundEligibilityResult;
import uk.bit1.mcpprototype.refund.RefundEligibilityService;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = SupportDeskTools.class)
class SupportDeskToolsTests {

    @Autowired
    private SupportDeskTools supportDeskTools;

    @MockitoBean
    private CustomerLookupService customerLookupService;

    @MockitoBean
    private RecentOrderService recentOrderService;

    @MockitoBean
    private RefundEligibilityService refundEligibilityService;

    @Test
    void lookupCustomerDelegatesToSharedService() {
        when(customerLookupService.lookupByEmail("sam@example.com"))
                .thenReturn(List.of(new Customer("cus_eligible", "Sam Rivera", "sam@example.com", "active", "normal")));

        LookupCustomerResponse response = supportDeskTools.lookupCustomer("sam@example.com");

        assertThat(response.matches())
                .extracting(LookupCustomerResponse.CustomerMatch::customerId)
                .containsExactly("cus_eligible");
        verify(customerLookupService).lookupByEmail("sam@example.com");
    }

    @Test
    void getRecentOrdersDelegatesToSharedService() {
        when(recentOrderService.getRecentOrders("cus_eligible", 5))
                .thenReturn(List.of(new CustomerOrder(
                        "ord_eligible",
                        "cus_eligible",
                        Instant.parse("2026-09-01T10:15:00Z"),
                        Instant.parse("2026-09-03T10:15:00Z"),
                        "delivered",
                        7999,
                        "GBP",
                        null
                )));

        RecentOrdersResponse response = supportDeskTools.getRecentOrders("cus_eligible", 5);

        assertThat(response.orders())
                .extracting(RecentOrdersResponse.OrderSummary::orderId)
                .contains("ord_eligible");
        verify(recentOrderService).getRecentOrders("cus_eligible", 5);
    }

    @Test
    void checkRefundEligibilityDelegatesToSharedService() {
        when(refundEligibilityService.checkEligibility("cus_eligible", "ord_eligible"))
                .thenReturn(new RefundEligibilityResult(
                        "cus_eligible",
                        "ord_eligible",
                        true,
                        "Order was delivered within the refund window and has no previous refund.",
                        7999L,
                        "GBP",
                        List.of()
                ));

        RefundEligibilityResult response = supportDeskTools.checkRefundEligibility("cus_eligible", "ord_eligible");

        assertThat(response.eligible()).isTrue();
        assertThat(response.maxRefundMinor()).isEqualTo(7999);
        verify(refundEligibilityService).checkEligibility("cus_eligible", "ord_eligible");
    }

    @Test
    void checkRefundEligibilityRequiresCustomerAndOrderIds() {
        when(refundEligibilityService.checkEligibility("", ""))
                .thenReturn(new RefundEligibilityResult(
                        "",
                        "",
                        false,
                        "Customer ID and Order ID are required.",
                        null,
                        null,
                        List.of()
                ));

        RefundEligibilityResult response = supportDeskTools.checkRefundEligibility("", "");

        assertThat(response.eligible()).isFalse();
        assertThat(response.reason()).isEqualTo("Customer ID and Order ID are required.");
        verify(refundEligibilityService).checkEligibility("", "");
    }
}
