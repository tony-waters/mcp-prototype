package uk.bit1.mcpprototype.freetext;

import java.util.Map;

public record FreeTextToolTrace(
        String toolName,
        Map<String, String> arguments,
        String status,
        String resultSummary
) {
}
