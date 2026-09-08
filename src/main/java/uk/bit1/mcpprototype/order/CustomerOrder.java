package uk.bit1.mcpprototype.order;

import java.time.Instant;

public record CustomerOrder(
        String id,
        String customerId,
        Instant placedAt,
        Instant deliveredAt,
        String status,
        long totalMinor,
        String currency,
        Instant refundedAt
) {
}
