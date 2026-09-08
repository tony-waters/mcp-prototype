package uk.bit1.mcpprototype.refund;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.bit1.mcpprototype.customer.Customer;
import uk.bit1.mcpprototype.customer.CustomerRepository;
import uk.bit1.mcpprototype.order.CustomerOrder;
import uk.bit1.mcpprototype.order.CustomerOrderRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RefundEligibilityService {

    private final CustomerRepository customerRepository;
    private final CustomerOrderRepository orderRepository;
    private final Clock clock;
    private final int refundWindowDays;

    RefundEligibilityService(
            CustomerRepository customerRepository,
            CustomerOrderRepository orderRepository,
            Clock clock,
            @Value("${refund.window-days:30}") int refundWindowDays
    ) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.clock = clock;
        this.refundWindowDays = refundWindowDays;
    }

    public RefundEligibilityResult checkEligibility(String customerId, String orderId) {
        List<RefundCheck> checks = new ArrayList<>();

        if (!StringUtils.hasText(customerId) || !StringUtils.hasText(orderId)) {
            checks.add(blocking("required_ids", "Customer ID and Order ID are required."));
            return ineligible(customerId, orderId, "Customer ID and Order ID are required.", null, null, checks);
        }

        String trimmedCustomerId = customerId.trim();
        String trimmedOrderId = orderId.trim();

        Optional<Customer> maybeCustomer = customerRepository.findById(trimmedCustomerId);
        if (maybeCustomer.isEmpty()) {
            checks.add(blocking("customer_exists", "Customer was not found."));
            return ineligible(trimmedCustomerId, trimmedOrderId, "Customer was not found.", null, null, checks);
        }
        Customer customer = maybeCustomer.get();
        checks.add(passed("customer_exists", "Customer exists."));

        Optional<CustomerOrder> maybeOrder = orderRepository.findById(trimmedOrderId);
        if (maybeOrder.isEmpty()) {
            checks.add(blocking("order_exists", "Order was not found."));
            return ineligible(trimmedCustomerId, trimmedOrderId, "Order was not found.", null, null, checks);
        }
        CustomerOrder order = maybeOrder.get();
        checks.add(passed("order_exists", "Order exists."));

        if (!order.customerId().equals(customer.id())) {
            checks.add(blocking("order_belongs_to_customer", "Order does not belong to the selected Customer."));
            return ineligible(customer.id(), order.id(), "Order does not belong to the selected Customer.", null, order.currency(), checks);
        }
        checks.add(passed("order_belongs_to_customer", "Order belongs to the selected Customer."));

        if (!"active".equals(customer.status())) {
            checks.add(blocking("customer_active", "Customer is not active."));
            return ineligible(customer.id(), order.id(), "Customer is not active.", null, order.currency(), checks);
        }
        checks.add(passed("customer_active", "Customer is active."));

        if ("blocked".equals(customer.riskLevel())) {
            checks.add(blocking("customer_risk", "Customer risk level blocks this refund workflow."));
            return ineligible(customer.id(), order.id(), "Customer risk level blocks this refund workflow.", null, order.currency(), checks);
        }
        if ("watch".equals(customer.riskLevel())) {
            checks.add(new RefundCheck("customer_risk", true, "warning", "Customer is on the watch list; review before drafting a refund."));
        } else {
            checks.add(passed("customer_risk", "Customer risk level does not block this refund workflow."));
        }

        if (order.refundedAt() != null) {
            checks.add(blocking("not_already_refunded", "Order has already been refunded."));
            return ineligible(customer.id(), order.id(), "Order has already been refunded.", null, order.currency(), checks);
        }
        checks.add(passed("not_already_refunded", "Order has not already been refunded."));

        if (!"delivered".equals(order.status())) {
            checks.add(blocking("order_delivered", "Order status is not delivered."));
            return ineligible(customer.id(), order.id(), "Order must be delivered before this refund workflow can continue.", null, order.currency(), checks);
        }
        checks.add(passed("order_delivered", "Order status is delivered."));

        if (order.deliveredAt() == null) {
            checks.add(blocking("delivered_at_present", "Order has no delivered time."));
            return ineligible(customer.id(), order.id(), "Order has no delivered time.", null, order.currency(), checks);
        }
        checks.add(passed("delivered_at_present", "Order has a delivered time."));

        Instant refundWindowStart = clock.instant().minus(Duration.ofDays(refundWindowDays));
        if (order.deliveredAt().isBefore(refundWindowStart)) {
            checks.add(blocking("within_refund_window", "Order was delivered outside the " + refundWindowDays + " day refund window."));
            return ineligible(customer.id(), order.id(), "Order was delivered outside the refund window.", null, order.currency(), checks);
        }
        checks.add(passed("within_refund_window", "Order was delivered within the " + refundWindowDays + " day refund window."));

        return new RefundEligibilityResult(
                customer.id(),
                order.id(),
                true,
                "Order was delivered within the refund window and has no previous refund.",
                order.totalMinor(),
                order.currency(),
                List.copyOf(checks)
        );
    }

    private RefundEligibilityResult ineligible(
            String customerId,
            String orderId,
            String reason,
            Long maxRefundMinor,
            String currency,
            List<RefundCheck> checks
    ) {
        return new RefundEligibilityResult(customerId, orderId, false, reason, maxRefundMinor, currency, List.copyOf(checks));
    }

    private RefundCheck passed(String name, String message) {
        return new RefundCheck(name, true, "info", message);
    }

    private RefundCheck blocking(String name, String message) {
        return new RefundCheck(name, false, "blocking", message);
    }
}
