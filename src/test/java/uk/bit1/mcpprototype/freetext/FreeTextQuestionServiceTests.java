package uk.bit1.mcpprototype.freetext;

import org.junit.jupiter.api.Test;
import uk.bit1.mcpprototype.mcp.LookupCustomerResponse;
import uk.bit1.mcpprototype.mcp.RecentOrdersResponse;
import uk.bit1.mcpprototype.refund.RefundCheck;
import uk.bit1.mcpprototype.refund.RefundEligibilityResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FreeTextQuestionServiceTests {

    @Test
    void blankQuestionReturnsValidationError() {
        FreeTextQuestionService service = new FreeTextQuestionService(new FakeInterpreter(false), new RecordingMcpClient());

        FreeTextQuestionResponse response = service.answer(" ");

        assertThat(response.status()).isEqualTo(FreeTextQuestionStatus.VALIDATION_ERROR);
        assertThat(response.answer()).contains("Enter a refund eligibility question");
        assertThat(response.trace()).isEmpty();
    }

    @Test
    void missingModelConfigurationReturnsUnavailable() {
        FreeTextQuestionService service = new FreeTextQuestionService(new FakeInterpreter(false), new RecordingMcpClient());

        FreeTextQuestionResponse response = service.answer("Can sam@example.com be refunded?");

        assertThat(response.status()).isEqualTo(FreeTextQuestionStatus.UNAVAILABLE);
        assertThat(response.answer()).contains("Free text AI is unavailable");
        assertThat(response.trace()).isEmpty();
    }

    @Test
    void eligibleQuestionCallsMcpToolsAndReturnsTrace() {
        RecordingMcpClient mcpClient = new RecordingMcpClient();
        mcpClient.customers = new LookupCustomerResponse(List.of(
                new LookupCustomerResponse.CustomerMatch("cus_eligible", "Sam Rivera", "sam@example.com", "active", "normal")
        ));
        mcpClient.orders = new RecentOrdersResponse(List.of(
                new RecentOrdersResponse.OrderSummary(
                        "ord_eligible",
                        Instant.parse("2026-09-01T10:15:00Z"),
                        Instant.parse("2026-09-03T10:15:00Z"),
                        "delivered",
                        7999,
                        "GBP"
                )
        ));
        mcpClient.eligibility = new RefundEligibilityResult(
                "cus_eligible",
                "ord_eligible",
                true,
                "Order was delivered within the refund window and has no previous refund.",
                7999L,
                "GBP",
                List.of(new RefundCheck("within_refund_window", true, "info", "Order was delivered within the 30 day refund window."))
        );
        FreeTextQuestionService service = new FreeTextQuestionService(
                new FakeInterpreter(true, new FreeTextQuestionIntent("sam@example.com", Optional.empty(), true)),
                mcpClient
        );

        FreeTextQuestionResponse response = service.answer("Can sam@example.com's latest delivered order be refunded?");

        assertThat(response.status()).isEqualTo(FreeTextQuestionStatus.ANSWERED);
        assertThat(response.answer()).contains("Eligible");
        assertThat(response.answer()).contains("7999 GBP");
        assertThat(mcpClient.calls).containsExactly(
                "lookupCustomer:sam@example.com",
                "getRecentOrders:cus_eligible:5",
                "checkRefundEligibility:cus_eligible:ord_eligible"
        );
        assertThat(response.trace())
                .extracting(FreeTextToolTrace::toolName)
                .containsExactly("lookupCustomer", "getRecentOrders", "checkRefundEligibility");
    }

    @Test
    void duplicateCustomerMatchesAreNotSilentlyCollapsed() {
        RecordingMcpClient mcpClient = new RecordingMcpClient();
        mcpClient.customers = new LookupCustomerResponse(List.of(
                new LookupCustomerResponse.CustomerMatch("cus_duplicate_a", "Taylor One", "duplicate@example.com", "active", "normal"),
                new LookupCustomerResponse.CustomerMatch("cus_duplicate_b", "Taylor Two", "duplicate@example.com", "active", "normal")
        ));
        FreeTextQuestionService service = new FreeTextQuestionService(
                new FakeInterpreter(true, new FreeTextQuestionIntent("duplicate@example.com", Optional.empty(), true)),
                mcpClient
        );

        FreeTextQuestionResponse response = service.answer("Can duplicate@example.com's latest order be refunded?");

        assertThat(response.status()).isEqualTo(FreeTextQuestionStatus.NEEDS_INFO);
        assertThat(response.answer()).contains("multiple Customers");
        assertThat(mcpClient.calls).containsExactly("lookupCustomer:duplicate@example.com");
        assertThat(response.trace()).hasSize(1);
    }

    private static class FakeInterpreter implements FreeTextQuestionInterpreter {

        private final boolean configured;
        private final FreeTextQuestionIntent intent;

        FakeInterpreter(boolean configured) {
            this(configured, new FreeTextQuestionIntent("sam@example.com", Optional.empty(), true));
        }

        FakeInterpreter(boolean configured, FreeTextQuestionIntent intent) {
            this.configured = configured;
            this.intent = intent;
        }

        @Override
        public boolean isConfigured() {
            return configured;
        }

        @Override
        public FreeTextQuestionIntent interpret(String question) {
            return intent;
        }
    }

    private static class RecordingMcpClient implements FreeTextMcpClient {

        private final List<String> calls = new ArrayList<>();
        private LookupCustomerResponse customers = new LookupCustomerResponse(List.of());
        private RecentOrdersResponse orders = new RecentOrdersResponse(List.of());
        private RefundEligibilityResult eligibility;

        @Override
        public LookupCustomerResponse lookupCustomer(String email) {
            calls.add("lookupCustomer:" + email);
            return customers;
        }

        @Override
        public RecentOrdersResponse getRecentOrders(String customerId, int limit) {
            calls.add("getRecentOrders:" + customerId + ":" + limit);
            return orders;
        }

        @Override
        public RefundEligibilityResult checkRefundEligibility(String customerId, String orderId) {
            calls.add("checkRefundEligibility:" + customerId + ":" + orderId);
            return eligibility;
        }
    }
}
