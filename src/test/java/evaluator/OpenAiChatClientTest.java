package evaluator;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OpenAiChatClientTest {

    @Test
    void extractsContentFromChatCompletionResponse() throws IOException {
        String body = """
                {
                  "id": "chatcmpl-123",
                  "choices": [
                    {"index": 0, "message": {"role": "assistant", "content": "Hello there."}}
                  ]
                }
                """;
        assertEquals("Hello there.", OpenAiChatClient.extractContent(body));
    }

    @Test
    void rejectsResponseWithoutChoices() {
        assertThrows(IOException.class, () -> OpenAiChatClient.extractContent("{\"choices\": []}"));
    }

    @Test
    void rejectsNonJsonResponse() {
        assertThrows(IOException.class, () -> OpenAiChatClient.extractContent("<html>gateway error</html>"));
    }
}
