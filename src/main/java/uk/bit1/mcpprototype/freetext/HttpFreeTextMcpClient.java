package uk.bit1.mcpprototype.freetext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import uk.bit1.mcpprototype.mcp.LookupCustomerResponse;
import uk.bit1.mcpprototype.mcp.RecentOrdersResponse;
import uk.bit1.mcpprototype.refund.RefundEligibilityResult;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
class HttpFreeTextMcpClient implements FreeTextMcpClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final AtomicLong requestId = new AtomicLong(1);
    private volatile String sessionId;

    HttpFreeTextMcpClient(
            @Value("${support.free-text.mcp-url:http://localhost:8080/mcp}") String mcpUrl
    ) {
        this.restClient = RestClient.create(mcpUrl);
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Override
    public LookupCustomerResponse lookupCustomer(String email) {
        return callTool("lookupCustomer", Map.of("email", email), LookupCustomerResponse.class);
    }

    @Override
    public RecentOrdersResponse getRecentOrders(String customerId, int limit) {
        return callTool("getRecentOrders", Map.of("customerId", customerId, "limit", limit), RecentOrdersResponse.class);
    }

    @Override
    public RefundEligibilityResult checkRefundEligibility(String customerId, String orderId) {
        return callTool("checkRefundEligibility", Map.of("customerId", customerId, "orderId", orderId), RefundEligibilityResult.class);
    }

    private <T> T callTool(String name, Map<String, ?> arguments, Class<T> responseType) {
        ensureInitialized();
        Map<String, Object> response = request("tools/call", Map.of(
                "name", name,
                "arguments", arguments
        ), true);
        try {
            JsonNode result = objectMapper.valueToTree(response).path("result");
            JsonNode payload = toolPayload(result);
            return objectMapper.treeToValue(payload, responseType);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not read MCP response for " + name, exception);
        }
    }

    private synchronized void ensureInitialized() {
        if (sessionId != null) {
            return;
        }
        request("initialize", Map.of(
                "protocolVersion", "2025-03-26",
                "capabilities", Map.of(),
                "clientInfo", Map.of("name", "mcp-support-desk-free-text", "version", "0.0.1")
        ), false);
        request("notifications/initialized", Map.of(), true);
    }

    private Map<String, Object> request(String method, Map<String, ?> params, boolean includeSession) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("jsonrpc", "2.0");
        if (!method.startsWith("notifications/")) {
            body.put("id", requestId.getAndIncrement());
        }
        body.put("method", method);
        body.put("params", params);

        ResponseEntity<String> response = restClient.post()
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set(HttpHeaders.ACCEPT, "application/json, text/event-stream");
                    if (includeSession && sessionId != null) {
                        headers.set("Mcp-Session-Id", sessionId);
                    }
                })
                .body(body)
                .retrieve()
                .toEntity(String.class);

        String returnedSessionId = response.getHeaders().getFirst("Mcp-Session-Id");
        if (returnedSessionId != null && !returnedSessionId.isBlank()) {
            sessionId = returnedSessionId;
        }
        if (response.getBody() == null || response.getBody().isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(extractJson(response.getBody()), Map.class);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not parse MCP response for " + method, exception);
        }
    }

    private JsonNode toolPayload(JsonNode result) throws Exception {
        if (result.has("structuredContent")) {
            return result.get("structuredContent");
        }
        JsonNode content = result.path("content");
        if (content.isArray() && !content.isEmpty()) {
            JsonNode text = content.get(0).path("text");
            if (text.isTextual()) {
                return objectMapper.readTree(text.asText());
            }
        }
        return result;
    }

    private String extractJson(String body) {
        String trimmed = body.trim();
        if (!trimmed.contains("data:")) {
            return trimmed;
        }
        StringBuilder data = new StringBuilder();
        for (String line : trimmed.split("\\R")) {
            if (line.startsWith("data:")) {
                String value = line.substring("data:".length()).trim();
                if (!"[DONE]".equals(value)) {
                    data.append(value);
                }
            }
        }
        return data.toString();
    }
}
