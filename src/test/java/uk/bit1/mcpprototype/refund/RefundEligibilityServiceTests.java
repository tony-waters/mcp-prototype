package uk.bit1.mcpprototype.refund;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import uk.bit1.mcpprototype.PostgresIntegrationTest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RefundEligibilityServiceTests extends PostgresIntegrationTest {

    @Autowired
    private RefundEligibilityService refundEligibilityService;

    @Test
    void eligibleRefundScenarioPasses() {
        RefundEligibilityResult result = refundEligibilityService.checkEligibility("cus_eligible", "ord_eligible");

        assertThat(result.eligible()).isTrue();
        assertThat(result.maxRefundMinor()).isEqualTo(7999);
        assertThat(result.currency()).isEqualTo("GBP");
    }

    @Test
    void alreadyRefundedOrderIsIneligible() {
        RefundEligibilityResult result = refundEligibilityService.checkEligibility("cus_refunded", "ord_refunded");

        assertThat(result.eligible()).isFalse();
        assertThat(result.checks()).anySatisfy(check -> {
            assertThat(check.name()).isEqualTo("not_already_refunded");
            assertThat(check.passed()).isFalse();
        });
    }

    @Test
    void outOfWindowOrderIsIneligible() {
        RefundEligibilityResult result = refundEligibilityService.checkEligibility("cus_out_of_window", "ord_out_of_window");

        assertThat(result.eligible()).isFalse();
        assertThat(result.checks()).anySatisfy(check -> {
            assertThat(check.name()).isEqualTo("within_refund_window");
            assertThat(check.passed()).isFalse();
        });
    }

    @Test
    void blockedCustomerIsIneligible() {
        RefundEligibilityResult result = refundEligibilityService.checkEligibility("cus_blocked", "ord_blocked_customer");

        assertThat(result.eligible()).isFalse();
        assertThat(result.checks()).anySatisfy(check -> {
            assertThat(check.name()).isEqualTo("customer_risk");
            assertThat(check.severity()).isEqualTo("blocking");
        });
    }

    @Test
    void watchCustomerCanBeEligibleWithWarning() {
        RefundEligibilityResult result = refundEligibilityService.checkEligibility("cus_watch", "ord_watch_customer");

        assertThat(result.eligible()).isTrue();
        assertThat(result.checks()).anySatisfy(check -> {
            assertThat(check.name()).isEqualTo("customer_risk");
            assertThat(check.severity()).isEqualTo("warning");
        });
    }

    @Test
    void customerAndOrderIdsAreRequired() {
        RefundEligibilityResult result = refundEligibilityService.checkEligibility("", "");

        assertThat(result.eligible()).isFalse();
        assertThat(result.reason()).isEqualTo("Customer ID and Order ID are required.");
    }

    @Test
    void orderMustBelongToCustomer() {
        RefundEligibilityResult result = refundEligibilityService.checkEligibility("cus_eligible", "ord_watch_customer");

        assertThat(result.eligible()).isFalse();
        assertThat(result.checks()).anySatisfy(check -> {
            assertThat(check.name()).isEqualTo("order_belongs_to_customer");
            assertThat(check.passed()).isFalse();
        });
    }

    @TestConfiguration
    static class FixedClockConfiguration {

        @Bean
        @Primary
        Clock testClock() {
            return Clock.fixed(Instant.parse("2026-09-08T12:00:00Z"), ZoneOffset.UTC);
        }
    }
}
