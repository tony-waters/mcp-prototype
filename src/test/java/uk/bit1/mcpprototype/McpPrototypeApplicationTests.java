package uk.bit1.mcpprototype;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.bit1.mcpprototype.freetext.FreeTextQuestionService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class McpPrototypeApplicationTests extends PostgresIntegrationTest {

    @Autowired
    private FreeTextQuestionService freeTextQuestionService;

    @Test
    void startsWithFreeTextUnavailableWhenNoChatModelIsConfigured() {
        assertThat(freeTextQuestionService.isAvailable()).isFalse();
    }

}
