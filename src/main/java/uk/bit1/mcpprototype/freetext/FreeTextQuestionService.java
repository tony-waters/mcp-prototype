package uk.bit1.mcpprototype.freetext;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.bit1.mcpprototype.mcp.LookupCustomerResponse;
import uk.bit1.mcpprototype.mcp.RecentOrdersResponse;
import uk.bit1.mcpprototype.refund.RefundCheck;
import uk.bit1.mcpprototype.refund.RefundEligibilityResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class FreeTextQuestionService {

    private static final int RECENT_ORDER_LIMIT = 5;

    private final FreeTextQuestionInterpreter interpreter;
    private final FreeTextMcpClient mcpClient;

    public FreeTextQuestionService(FreeTextQuestionInterpreter interpreter, FreeTextMcpClient mcpClient) {
        this.interpreter = interpreter;
        this.mcpClient = mcpClient;
    }

    public boolean isAvailable() {
        return interpreter.isConfigured();
    }

    public FreeTextQuestionResponse answer(String question) {
        if (!StringUtils.hasText(question)) {
            return new FreeTextQuestionResponse(
                    FreeTextQuestionStatus.VALIDATION_ERROR,
                    "Enter a refund eligibility question.",
                    List.of()
            );
        }
        if (!interpreter.isConfigured()) {
            return new FreeTextQuestionResponse(
                    FreeTextQuestionStatus.UNAVAILABLE,
                    "Free text AI is unavailable because no model provider credentials are configured.",
                    List.of()
            );
        }

        try {
            return answerConfiguredQuestion(question.trim());
        } catch (Exception exception) {
            return new FreeTextQuestionResponse(
                    FreeTextQuestionStatus.TOOL_ERROR,
                    "The assistant could not complete the MCP tool flow. " + exception.getMessage(),
                    List.of()
            );
        }
    }

    private FreeTextQuestionResponse answerConfiguredQuestion(String question) {
        FreeTextQuestionIntent intent = interpreter.interpret(question);
        if (!StringUtils.hasText(intent.email())) {
            return new FreeTextQuestionResponse(
                    FreeTextQuestionStatus.NEEDS_INFO,
                    "Please include the Customer's exact email address. This prototype does not infer Customers from names.",
                    List.of()
            );
        }

        List<FreeTextToolTrace> trace = new ArrayList<>();
        LookupCustomerResponse customers = mcpClient.lookupCustomer(intent.email());
        trace.add(new FreeTextToolTrace(
                "lookupCustomer",
                Map.of("email", intent.email()),
                "success",
                customers.matches().size() + " Customer matched"
        ));

        if (customers.matches().isEmpty()) {
            return new FreeTextQuestionResponse(
                    FreeTextQuestionStatus.NEEDS_INFO,
                    "No Customers matched " + intent.email() + ". Check the exact email address and try again.",
                    List.copyOf(trace)
            );
        }
        if (customers.matches().size() > 1) {
            return new FreeTextQuestionResponse(
                    FreeTextQuestionStatus.NEEDS_INFO,
                    "That email matched multiple Customers. Select a specific Customer in the guided workflow before checking eligibility.",
                    List.copyOf(trace)
            );
        }

        String customerId = customers.matches().getFirst().customerId();
        String orderId = intent.orderId().orElse(null);
        if (!StringUtils.hasText(orderId)) {
            RecentOrdersResponse orders = mcpClient.getRecentOrders(customerId, RECENT_ORDER_LIMIT);
            trace.add(new FreeTextToolTrace(
                    "getRecentOrders",
                    Map.of("customerId", customerId, "limit", String.valueOf(RECENT_ORDER_LIMIT)),
                    "success",
                    orders.orders().size() + " recent Order returned"
            ));
            orderId = orders.orders().stream()
                    .filter(order -> "delivered".equals(order.status()))
                    .max(Comparator.comparing(RecentOrdersResponse.OrderSummary::placedAt))
                    .map(RecentOrdersResponse.OrderSummary::orderId)
                    .orElse(null);
            if (!StringUtils.hasText(orderId)) {
                return new FreeTextQuestionResponse(
                        FreeTextQuestionStatus.NEEDS_INFO,
                        "No delivered recent Order was found for " + intent.email() + ".",
                        List.copyOf(trace)
                );
            }
        }

        RefundEligibilityResult eligibility = mcpClient.checkRefundEligibility(customerId, orderId);
        trace.add(new FreeTextToolTrace(
                "checkRefundEligibility",
                Map.of("customerId", customerId, "orderId", orderId),
                "success",
                eligibility.eligible() ? "eligible" : "ineligible"
        ));

        return new FreeTextQuestionResponse(
                FreeTextQuestionStatus.ANSWERED,
                formatAnswer(eligibility),
                List.copyOf(trace)
        );
    }

    private String formatAnswer(RefundEligibilityResult eligibility) {
        StringBuilder answer = new StringBuilder();
        answer.append(eligibility.eligible() ? "Eligible. " : "Ineligible. ");
        answer.append(eligibility.reason());
        if (eligibility.maxRefundMinor() != null && StringUtils.hasText(eligibility.currency())) {
            answer.append(" Maximum refund is ")
                    .append(eligibility.maxRefundMinor())
                    .append(' ')
                    .append(eligibility.currency())
                    .append('.');
        }

        List<RefundCheck> importantChecks = eligibility.checks().stream()
                .filter(check -> !"info".equals(check.severity()) || !check.passed())
                .toList();
        if (!importantChecks.isEmpty()) {
            answer.append(" Review checks: ");
            answer.append(String.join("; ", importantChecks.stream()
                    .map(check -> check.name() + ": " + check.message())
                    .toList()));
            answer.append('.');
        }
        return answer.toString();
    }
}
