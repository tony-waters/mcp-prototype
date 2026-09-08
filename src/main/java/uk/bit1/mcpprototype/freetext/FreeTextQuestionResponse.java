package uk.bit1.mcpprototype.freetext;

import java.util.List;

public record FreeTextQuestionResponse(
        FreeTextQuestionStatus status,
        String answer,
        List<FreeTextToolTrace> trace
) {
}
