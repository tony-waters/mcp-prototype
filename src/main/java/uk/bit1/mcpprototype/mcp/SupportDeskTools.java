package uk.bit1.mcpprototype.mcp;

import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;
import uk.bit1.mcpprototype.customer.CustomerLookupService;
import uk.bit1.mcpprototype.order.RecentOrderService;
import uk.bit1.mcpprototype.refund.RefundEligibilityResult;
import uk.bit1.mcpprototype.refund.RefundEligibilityService;

@Component
public class SupportDeskTools {

    private final CustomerLookupService customerLookupService;
    private final RecentOrderService recentOrderService;
    private final RefundEligibilityService refundEligibilityService;

    SupportDeskTools(
            CustomerLookupService customerLookupService,
            RecentOrderService recentOrderService,
            RefundEligibilityService refundEligibilityService
    ) {
        this.customerLookupService = customerLookupService;
        this.recentOrderService = recentOrderService;
        this.refundEligibilityService = refundEligibilityService;
    }

    @McpTool(
            name = "lookupCustomer",
            description = "Find customers by exact email address.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, openWorldHint = false)
    )
    public LookupCustomerResponse lookupCustomer(
            @McpArg(name = "email", description = "Exact customer email address.", required = true) String email
    ) {
        return LookupCustomerResponse.from(customerLookupService.lookupByEmail(email));
    }

    @McpTool(
            name = "getRecentOrders",
            description = "Return recent orders for a customer.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, openWorldHint = false)
    )
    public RecentOrdersResponse getRecentOrders(
            @McpArg(name = "customerId", description = "Customer ID.", required = true) String customerId,
            @McpArg(name = "limit", description = "Maximum number of recent orders to return.", required = true) int limit
    ) {
        return RecentOrdersResponse.from(recentOrderService.getRecentOrders(customerId, limit));
    }

    @McpTool(
            name = "checkRefundEligibility",
            description = "Check whether a customer's order is eligible for this refund workflow.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, openWorldHint = false)
    )
    public RefundEligibilityResult checkRefundEligibility(
            @McpArg(name = "customerId", description = "Customer ID.", required = true) String customerId,
            @McpArg(name = "orderId", description = "Order ID.", required = true) String orderId
    ) {
        return refundEligibilityService.checkEligibility(customerId, orderId);
    }
}
