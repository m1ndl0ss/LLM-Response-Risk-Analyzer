package evaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entry point. Loads prompts, sends each to the target model, has the judge
 * model label every response, and prints a summary.
 *
 * Exit codes: 0 when the target model is not flagged, 1 when it is flagged
 * as unsafe, 2 on configuration or I/O errors.
 */
public class Main {

    public static void main(String[] args) {
        try {
            Config config = Config.fromEnvironment();
            PromptLoader loader = new PromptLoader(config.getPromptsFile());

            System.out.println("Loaded prompts from " + config.getPromptsFile()
                    + " (" + loader.getRedactedCount() + " redacted entries skipped):");
            for (Map.Entry<String, List<String>> entry : loader.getPromptsByCategory().entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue().size());
            }
            System.out.println();

            OpenAiChatClient target = new OpenAiChatClient(
                    config.getTargetApiKey(), config.getTargetApiUrl(), config.getTargetModel());
            JudgeModelClient judge = new JudgeModelClient(new OpenAiChatClient(
                    config.getJudgeApiKey(), config.getJudgeApiUrl(), config.getJudgeModel()));

            List<EvaluationResult> results = new ArrayList<>();
            for (String prompt : loader.getAllPrompts()) {
                try {
                    String response = target.complete(prompt);
                    EvaluationResult result = judge.evaluate(prompt, response);
                    results.add(result);
                    System.out.println("Prompt:   " + prompt);
                    System.out.println("Response: " + response);
                    System.out.println("Verdict:  " + result);
                    System.out.println("--------------------------------");
                } catch (IOException e) {
                    System.err.println("Evaluation failed for prompt: " + prompt);
                    System.err.println("  " + e.getMessage());
                }
            }

            EvaluationSummary summary = EvaluationSummary.from(results);
            System.out.println();
            System.out.println(summary.report());
            if (results.isEmpty()) {
                System.err.println("No prompt was evaluated successfully.");
                System.exit(2);
            }
            if (summary.isModelFlagged()) {
                System.exit(1);
            }
        } catch (IOException | IllegalStateException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(2);
        }
    }
}
