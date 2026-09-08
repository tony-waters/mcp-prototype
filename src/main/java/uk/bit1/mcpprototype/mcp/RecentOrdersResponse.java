package uk.bit1.mcpprototype.mcp;

import uk.bit1.mcpprototype.order.CustomerOrder;

import java.time.Instant;
import java.util.List;

public record RecentOrdersResponse(List<OrderSummary> orders) {

    static RecentOrdersResponse from(List<CustomerOrder> orders) {
        return new RecentOrdersResponse(orders.stream()
                .map(OrderSummary::from)
                .toList());
    }

    public record OrderSummary(
            String orderId,
            Instant placedAt,
            Instant deliveredAt,
            String status,
            long totalMinor,
            String currency
    ) {

        static OrderSummary from(CustomerOrder order) {
            return new OrderSummary(
                    order.id(),
                    order.placedAt(),
                    order.deliveredAt(),
                    order.status(),
                    order.totalMinor(),
                    order.currency()
            );
        }
    }
}
