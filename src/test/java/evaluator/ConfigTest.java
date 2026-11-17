package evaluator;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigTest {

    @Test
    void appliesDefaultsWhenOnlyKeysAreSet() {
        Config config = Config.fromEnvironment(Map.of(
                "TARGET_API_KEY", "target-key",
                "JUDGE_API_KEY", "judge-key"));
        assertEquals(Config.DEFAULT_API_URL, config.getTargetApiUrl());
        assertEquals(Config.DEFAULT_API_URL, config.getJudgeApiUrl());
        assertEquals(Config.DEFAULT_MODEL, config.getTargetModel());
        assertEquals(Config.DEFAULT_MODEL, config.getJudgeModel());
        assertEquals(Config.DEFAULT_PROMPTS_FILE, config.getPromptsFile());
    }

    @Test
    void overridesDefaultsFromEnvironment() {
        Config config = Config.fromEnvironment(Map.of(
                "TARGET_API_KEY", "target-key",
                "JUDGE_API_KEY", "judge-key",
                "TARGET_API_URL", "http://localhost:8080/v1/chat/completions",
                "TARGET_MODEL", "local-model",
                "PROMPTS_FILE", "other.json"));
        assertEquals("http://localhost:8080/v1/chat/completions", config.getTargetApiUrl());
        assertEquals("local-model", config.getTargetModel());
        assertEquals("other.json", config.getPromptsFile());
    }

    @Test
    void rejectsMissingTargetKey() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> Config.fromEnvironment(Map.of("JUDGE_API_KEY", "judge-key")));
        assertEquals("Missing required environment variable TARGET_API_KEY", e.getMessage());
    }

    @Test
    void rejectsMissingJudgeKey() {
        assertThrows(IllegalStateException.class,
                () -> Config.fromEnvironment(Map.of("TARGET_API_KEY", "target-key")));
    }
}
