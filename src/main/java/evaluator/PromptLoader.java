package evaluator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads prompt-injection test prompts from a JSON file, grouped by category.
 *
 * Entries whose text starts with "REDACTED_" are placeholders for prompts
 * that are intentionally not published; they are skipped at load time.
 */
public class PromptLoader {

    static final String REDACTED_PREFIX = "REDACTED_";

    private final Map<String, List<String>> promptsByCategory = new LinkedHashMap<>();
    private int redactedCount = 0;

    public PromptLoader(String filepath) throws IOException {
        load(filepath);
    }

    private void load(String filepath) throws IOException {
        JsonObject root;
        try (FileReader reader = new FileReader(filepath, StandardCharsets.UTF_8)) {
            root = JsonParser.parseReader(reader).getAsJsonObject();
        }
        JsonArray categories = root.getAsJsonArray("categories");
        for (JsonElement categoryElement : categories) {
            JsonObject category = categoryElement.getAsJsonObject();
            String name = category.get("name").getAsString();
            List<String> prompts = promptsByCategory.computeIfAbsent(name, k -> new ArrayList<>());
            for (JsonElement promptElement : category.getAsJsonArray("prompts")) {
                String prompt = promptElement.getAsString();
                if (prompt.startsWith(REDACTED_PREFIX)) {
                    redactedCount++;
                } else {
                    prompts.add(prompt);
                }
            }
        }
    }

    /** Loaded prompts grouped by category name, in file order. Redacted entries are excluded. */
    public Map<String, List<String>> getPromptsByCategory() {
        return Collections.unmodifiableMap(promptsByCategory);
    }

    /** All loaded prompts across categories, in file order. Redacted entries are excluded. */
    public List<String> getAllPrompts() {
        List<String> all = new ArrayList<>();
        promptsByCategory.values().forEach(all::addAll);
        return all;
    }

    /** Number of redacted placeholder entries that were skipped. */
    public int getRedactedCount() {
        return redactedCount;
    }
}
