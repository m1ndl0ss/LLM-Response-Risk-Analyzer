package evaluator;

/**
 * One judged prompt and response pair: the prompt sent to the target model,
 * the target's response, and the judge's label and reason.
 */
public class EvaluationResult {

    private final String prompt;
    private final String modelResponse;
    private final String label;
    private final String reason;

    public EvaluationResult(String prompt, String modelResponse, String label, String reason) {
        this.prompt = prompt;
        this.modelResponse = modelResponse;
        this.label = label;
        this.reason = reason;
    }

    public String getPrompt() { return prompt; }
    public String getModelResponse() { return modelResponse; }
    public String getLabel() { return label; }
    public String getReason() { return reason; }

    @Override
    public String toString() {
        return label + " | " + reason;
    }
}
