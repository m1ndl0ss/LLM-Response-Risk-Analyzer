package evaluator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Minimal client for an OpenAI-compatible chat completions endpoint.
 *
 * Works with the OpenAI API and with local servers that expose the same
 * interface (for example llama.cpp or Ollama in OpenAI-compatible mode).
 */
public class OpenAiChatClient {

    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 120_000;

    private final String apiKey;
    private final String apiUrl;
    private final String model;

    public OpenAiChatClient(String apiKey, String apiUrl, String model) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    /**
     * Sends a single user message and returns the assistant's reply text.
     *
     * @param userMessage the message to send
     * @return the content of the first choice in the response
     * @throws IOException if the request fails or the response is not in the expected format
     */
    public String complete(String userMessage) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
        connection.setReadTimeout(READ_TIMEOUT_MS);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);

        try (OutputStream out = connection.getOutputStream()) {
            out.write(buildRequestBody(userMessage).getBytes(StandardCharsets.UTF_8));
        }

        int status = connection.getResponseCode();
        boolean ok = status >= 200 && status < 300;
        String body = readStream(ok ? connection.getInputStream() : connection.getErrorStream());
        if (!ok) {
            throw new IOException("API request to " + apiUrl + " failed with status " + status + ": " + body);
        }
        return extractContent(body);
    }

    private String buildRequestBody(String userMessage) {
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", userMessage);

        JsonArray messages = new JsonArray();
        messages.add(message);

        JsonObject body = new JsonObject();
        body.addProperty("model", model);
        body.add("messages", messages);
        return body.toString();
    }

    /**
     * Extracts choices[0].message.content from a chat completions response body.
     */
    static String extractContent(String responseBody) throws IOException {
        try {
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray choices = root.getAsJsonArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new IOException("Response contains no choices: " + responseBody);
            }
            return choices.get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        } catch (JsonSyntaxException | IllegalStateException | NullPointerException e) {
            throw new IOException("Unexpected response format: " + responseBody, e);
        }
    }

    private static String readStream(InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }
}
