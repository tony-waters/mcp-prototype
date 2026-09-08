package uk.bit1.mcpprototype.freetext;

import uk.bit1.mcpprototype.mcp.LookupCustomerResponse;
import uk.bit1.mcpprototype.mcp.RecentOrdersResponse;
import uk.bit1.mcpprototype.refund.RefundEligibilityResult;

public interface FreeTextMcpClient {

    LookupCustomerResponse lookupCustomer(String email);

    RecentOrdersResponse getRecentOrders(String customerId, int limit);

    RefundEligibilityResult checkRefundEligibility(String customerId, String orderId);
}
