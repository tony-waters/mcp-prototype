package uk.bit1.mcpprototype.freetext;

import java.util.Optional;

public record FreeTextQuestionIntent(
        String email,
        Optional<String> orderId,
        boolean latestDelivered
) {
}
