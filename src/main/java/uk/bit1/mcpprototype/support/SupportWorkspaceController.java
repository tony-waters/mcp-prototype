package uk.bit1.mcpprototype.support;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.bit1.mcpprototype.customer.Customer;
import uk.bit1.mcpprototype.customer.CustomerLookupService;
import uk.bit1.mcpprototype.customer.CustomerRepository;
import uk.bit1.mcpprototype.order.CustomerOrder;
import uk.bit1.mcpprototype.order.RecentOrderService;
import uk.bit1.mcpprototype.refund.RefundEligibilityResult;
import uk.bit1.mcpprototype.refund.RefundEligibilityService;

import java.util.List;

@Controller
class SupportWorkspaceController {

    private final CustomerLookupService customerLookupService;
    private final CustomerRepository customerRepository;
    private final RecentOrderService recentOrderService;
    private final RefundEligibilityService refundEligibilityService;

    SupportWorkspaceController(
            CustomerLookupService customerLookupService,
            CustomerRepository customerRepository,
            RecentOrderService recentOrderService,
            RefundEligibilityService refundEligibilityService
    ) {
        this.customerLookupService = customerLookupService;
        this.customerRepository = customerRepository;
        this.recentOrderService = recentOrderService;
        this.refundEligibilityService = refundEligibilityService;
    }

    @GetMapping({"/", "/support"})
    String showWorkspace(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String orderId,
            Model model
    ) {
        model.addAttribute("email", email);

        List<Customer> customers = email == null ? List.of() : customerLookupService.lookupByEmail(email);
        model.addAttribute("customers", customers);

        Customer selectedCustomer = null;
        List<CustomerOrder> orders = List.of();
        if (customerId != null && !customerId.isBlank()) {
            selectedCustomer = customerRepository.findById(customerId.trim()).orElse(null);
            orders = recentOrderService.getRecentOrders(customerId);
        }
        model.addAttribute("selectedCustomer", selectedCustomer);
        model.addAttribute("orders", orders);

        RefundEligibilityResult eligibility = null;
        if (selectedCustomer != null && orderId != null && !orderId.isBlank()) {
            eligibility = refundEligibilityService.checkEligibility(selectedCustomer.id(), orderId);
        }
        model.addAttribute("selectedOrderId", orderId);
        model.addAttribute("eligibility", eligibility);

        return "support/workspace";
    }
}
