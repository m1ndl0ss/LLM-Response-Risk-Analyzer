package evaluator;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JudgeModelClientTest {

    @Test
    void parsesPlainJsonVerdict() throws IOException {
        EvaluationResult result = JudgeModelClient.parseVerdict(
                "p", "r", "{\"label\": \"SAFE\", \"reason\": \"Harmless content.\"}");
        assertEquals("SAFE", result.getLabel());
        assertEquals("Harmless content.", result.getReason());
        assertEquals("p", result.getPrompt());
        assertEquals("r", result.getModelResponse());
    }

    @Test
    void parsesVerdictWrappedInCodeFence() throws IOException {
        String fenced = "```json\n{\"label\": \"UNSAFE\", \"reason\": \"Leaks instructions.\"}\n```";
        EvaluationResult result = JudgeModelClient.parseVerdict("p", "r", fenced);
        assertEquals("UNSAFE", result.getLabel());
        assertEquals("Leaks instructions.", result.getReason());
    }

    @Test
    void normalisesLabelCase() throws IOException {
        EvaluationResult result = JudgeModelClient.parseVerdict(
                "p", "r", "{\"label\": \"borderline\", \"reason\": \"x\"}");
        assertEquals("BORDERLINE", result.getLabel());
    }

    @Test
    void rejectsUnknownLabel() {
        assertThrows(IOException.class, () -> JudgeModelClient.parseVerdict(
                "p", "r", "{\"label\": \"MAYBE\", \"reason\": \"x\"}"));
    }

    @Test
    void rejectsNonJsonReply() {
        assertThrows(IOException.class, () -> JudgeModelClient.parseVerdict(
                "p", "r", "The response looks fine to me."));
    }

    @Test
    void rejectsMissingFields() {
        assertThrows(IOException.class, () -> JudgeModelClient.parseVerdict(
                "p", "r", "{\"label\": \"SAFE\"}"));
    }
}
