package uk.bit1.mcpprototype.support;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.bit1.mcpprototype.PostgresIntegrationTest;
import uk.bit1.mcpprototype.freetext.FreeTextQuestionResponse;
import uk.bit1.mcpprototype.freetext.FreeTextQuestionService;
import uk.bit1.mcpprototype.freetext.FreeTextQuestionStatus;
import uk.bit1.mcpprototype.freetext.FreeTextToolTrace;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SupportWorkspaceFreeTextTests extends PostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FreeTextQuestionService freeTextQuestionService;

    @Test
    void rendersFreeTextFormAndDisabledState() throws Exception {
        when(freeTextQuestionService.isAvailable()).thenReturn(false);

        mockMvc.perform(get("/support"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ask with AI")))
                .andExpect(content().string(containsString("Free text AI is unavailable")));
    }

    @Test
    void blankQuestionRedirectsWithValidationFeedback() throws Exception {
        mockMvc.perform(post("/support/free-text")
                .param("question", " "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/support?freeTextError=Enter+a+refund+eligibility+question."));
    }

    @Test
    void rendersFreeTextAnswerAndToolTrace() throws Exception {
        when(freeTextQuestionService.isAvailable()).thenReturn(true);
        when(freeTextQuestionService.answer(anyString())).thenReturn(new FreeTextQuestionResponse(
                FreeTextQuestionStatus.ANSWERED,
                "Eligible. Maximum refund is 7999 GBP.",
                List.of(new FreeTextToolTrace("lookupCustomer", Map.of("email", "sam@example.com"), "success", "1 Customer matched"))
        ));

        mockMvc.perform(post("/support/free-text")
                        .param("question", "Can sam@example.com's latest delivered order be refunded?"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Eligible. Maximum refund is 7999 GBP.")))
                .andExpect(content().string(containsString("lookupCustomer")))
                .andExpect(content().string(containsString("sam@example.com")));
    }
}
