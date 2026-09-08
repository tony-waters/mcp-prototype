package uk.bit1.mcpprototype.support;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.bit1.mcpprototype.customer.Customer;
import uk.bit1.mcpprototype.customer.CustomerLookupService;
import uk.bit1.mcpprototype.customer.CustomerRepository;
import uk.bit1.mcpprototype.freetext.FreeTextQuestionResponse;
import uk.bit1.mcpprototype.freetext.FreeTextQuestionService;
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
    private final FreeTextQuestionService freeTextQuestionService;

    SupportWorkspaceController(
            CustomerLookupService customerLookupService,
            CustomerRepository customerRepository,
            RecentOrderService recentOrderService,
            RefundEligibilityService refundEligibilityService,
            FreeTextQuestionService freeTextQuestionService
    ) {
        this.customerLookupService = customerLookupService;
        this.customerRepository = customerRepository;
        this.recentOrderService = recentOrderService;
        this.refundEligibilityService = refundEligibilityService;
        this.freeTextQuestionService = freeTextQuestionService;
    }

    @GetMapping({"/", "/support"})
    String showWorkspace(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String freeTextError,
            Model model
    ) {
        addFreeTextAttributes(model, null, null, freeTextError);
        addGuidedWorkflowAttributes(email, customerId, orderId, model);

        return "support/workspace";
    }

    @PostMapping("/support/free-text")
    String askFreeText(
            @RequestParam String question,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (question == null || question.isBlank()) {
            redirectAttributes.addAttribute("freeTextError", "Enter a refund eligibility question.");
            return "redirect:/support";
        }

        FreeTextQuestionResponse response = freeTextQuestionService.answer(question);
        addFreeTextAttributes(model, question, response, null);
        addGuidedWorkflowAttributes(null, null, null, model);
        return "support/workspace";
    }

    private void addFreeTextAttributes(
            Model model,
            String question,
            FreeTextQuestionResponse response,
            String freeTextError
    ) {
        model.addAttribute("freeTextAvailable", freeTextQuestionService.isAvailable());
        model.addAttribute("freeTextQuestion", question);
        model.addAttribute("freeTextResponse", response);
        model.addAttribute("freeTextError", freeTextError);
    }

    private void addGuidedWorkflowAttributes(String email, String customerId, String orderId, Model model) {
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
    }
}
