package uk.bit1.mcpprototype.support;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import uk.bit1.mcpprototype.PostgresIntegrationTest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SupportWorkspaceControllerTests extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rendersWorkspace() throws Exception {
        mockMvc.perform(get("/support"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("MCP Support Desk")));
    }

    @Test
    void rendersCustomerOrdersAndEligibility() throws Exception {
        mockMvc.perform(get("/support")
                        .param("email", "sam@example.com")
                        .param("customerId", "cus_eligible")
                        .param("orderId", "ord_eligible"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sam Rivera")))
                .andExpect(content().string(containsString("ord_eligible")))
                .andExpect(content().string(containsString("Create refund draft")));
    }
}
