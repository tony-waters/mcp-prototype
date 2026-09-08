package uk.bit1.mcpprototype.support;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SupportWorkspaceController.class)
class SupportWorkspaceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rendersWorkspacePlaceholder() throws Exception {
        mockMvc.perform(get("/support"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("MCP Support Desk")));
    }
}
