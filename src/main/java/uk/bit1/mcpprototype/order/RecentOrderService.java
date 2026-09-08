package uk.bit1.mcpprototype.order;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RecentOrderService {

    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 10;

    private final CustomerOrderRepository orderRepository;

    RecentOrderService(CustomerOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<CustomerOrder> getRecentOrders(String customerId) {
        return getRecentOrders(customerId, DEFAULT_LIMIT);
    }

    public List<CustomerOrder> getRecentOrders(String customerId, int limit) {
        if (!StringUtils.hasText(customerId)) {
            return List.of();
        }
        int boundedLimit = Math.clamp(limit, 1, MAX_LIMIT);
        return orderRepository.findRecentByCustomerId(customerId.trim(), boundedLimit);
    }
}
