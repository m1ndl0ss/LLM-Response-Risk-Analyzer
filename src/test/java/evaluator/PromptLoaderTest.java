package evaluator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptLoaderTest {

    @TempDir
    Path tempDir;

    private Path writePromptsFile(String json) throws IOException {
        Path file = tempDir.resolve("prompts.json");
        Files.writeString(file, json);
        return file;
    }

    @Test
    void loadsPromptsGroupedByCategory() throws IOException {
        Path file = writePromptsFile("""
                {
                  "categories": [
                    {"name": "role_play_override", "prompts": ["a", "b"]},
                    {"name": "instruction_override", "prompts": ["c"]}
                  ]
                }
                """);
        PromptLoader loader = new PromptLoader(file.toString());
        assertEquals(2, loader.getPromptsByCategory().get("role_play_override").size());
        assertEquals(1, loader.getPromptsByCategory().get("instruction_override").size());
        assertEquals(3, loader.getAllPrompts().size());
        assertEquals(0, loader.getRedactedCount());
    }

    @Test
    void skipsRedactedEntries() throws IOException {
        Path file = writePromptsFile("""
                {
                  "categories": [
                    {"name": "ethical_framing", "prompts": ["REDACTED_PROMPT_1", "real prompt", "REDACTED_PROMPT_2"]}
                  ]
                }
                """);
        PromptLoader loader = new PromptLoader(file.toString());
        assertEquals(1, loader.getAllPrompts().size());
        assertEquals("real prompt", loader.getAllPrompts().get(0));
        assertEquals(2, loader.getRedactedCount());
    }

    @Test
    void preservesFileOrderAcrossCategories() throws IOException {
        Path file = writePromptsFile("""
                {
                  "categories": [
                    {"name": "first", "prompts": ["one"]},
                    {"name": "second", "prompts": ["two", "three"]}
                  ]
                }
                """);
        PromptLoader loader = new PromptLoader(file.toString());
        assertEquals(java.util.List.of("one", "two", "three"), loader.getAllPrompts());
        assertTrue(loader.getPromptsByCategory().containsKey("first"));
    }
}
