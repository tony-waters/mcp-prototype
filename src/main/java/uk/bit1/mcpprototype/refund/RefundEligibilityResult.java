package uk.bit1.mcpprototype.refund;

import java.util.List;

public record RefundEligibilityResult(
        String customerId,
        String orderId,
        boolean eligible,
        String reason,
        Long maxRefundMinor,
        String currency,
        List<RefundCheck> checks
) {
}
