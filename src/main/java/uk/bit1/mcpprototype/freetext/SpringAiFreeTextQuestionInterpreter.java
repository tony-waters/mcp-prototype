package uk.bit1.mcpprototype.freetext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@ConditionalOnBean(ChatModel.class)
class SpringAiFreeTextQuestionInterpreter implements FreeTextQuestionInterpreter {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();

    SpringAiFreeTextQuestionInterpreter(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public boolean isConfigured() {
        return true;
    }

    @Override
    public FreeTextQuestionIntent interpret(String question) {
        String response = chatModel.call("""
                Extract a refund eligibility question into strict JSON.
                Return only JSON with fields:
                - email: exact customer email address, or null when absent
                - orderId: exact order id, or null when absent
                - latestDelivered: true when the question asks about latest/recent/current order without an explicit order id

                The app can only look up customers by exact email. Do not infer email from a name.

                Question:
                %s
                """.formatted(question));
        try {
            JsonNode root = objectMapper.readTree(stripCodeFence(response));
            String email = textOrNull(root.get("email"));
            String orderId = textOrNull(root.get("orderId"));
            boolean latestDelivered = root.path("latestDelivered").asBoolean(orderId == null);
            return new FreeTextQuestionIntent(email, Optional.ofNullable(orderId), latestDelivered);
        } catch (Exception exception) {
            return new FreeTextQuestionIntent(null, Optional.empty(), true);
        }
    }

    private String textOrNull(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        String value = node.asText();
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String stripCodeFence(String response) {
        String trimmed = response == null ? "" : response.trim();
        if (trimmed.startsWith("```")) {
            int firstLineEnd = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstLineEnd >= 0 && lastFence > firstLineEnd) {
                return trimmed.substring(firstLineEnd + 1, lastFence).trim();
            }
        }
        return trimmed;
    }
}
