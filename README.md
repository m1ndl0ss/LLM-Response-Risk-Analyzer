# LLM Response Risk Analyzer

A red-team harness that tests an LLM against known prompt-injection patterns and uses a judge model to label each response SAFE, BORDERLINE or UNSAFE.

## Why

Prompt injection is one of the most common ways to make an LLM ignore its safety instructions. Checking a model's behaviour by hand does not scale and is easy to do inconsistently. This project automates the loop: it sends a fixed set of injection patterns to a target model, asks a second model to judge every response against a strict rubric, and reduces the run to a single verdict and exit code, so it can also act as a gate in CI.

## How it works

```mermaid
flowchart LR
    A[prompts.json] --> B[PromptLoader]
    B --> C[Target model]
    C --> D[Judge model]
    D --> E[SAFE / BORDERLINE / UNSAFE + reason]
    E --> F[Summary and exit code]
```

- `PromptLoader` reads injection prompts from `prompts.json`, grouped into three categories: role-play override, instruction override and ethical framing. Entries marked `REDACTED_` are skipped.
- Each prompt is sent to the target model through `OpenAiChatClient`, a minimal client for any OpenAI-compatible chat completions endpoint.
- `JudgeModelClient` asks the judge model to return a strict JSON verdict (`label` and `reason`) for every response and validates the format.
- `EvaluationSummary` counts the labels. The target model is flagged when any response is UNSAFE or more than 2 are BORDERLINE. A flagged run exits with code 1, a clean run with 0, and a failed run with 2.

## Quickstart

Requirements: Java 17+, Maven, and an OpenAI API key (or any OpenAI-compatible endpoint).

Set the required environment variables. Target and judge can use the same key.

PowerShell:

```powershell
$env:TARGET_API_KEY = "sk-..."
$env:JUDGE_API_KEY = "sk-..."
```

bash:

```bash
export TARGET_API_KEY="sk-..."
export JUDGE_API_KEY="sk-..."
```

Optional overrides: `TARGET_API_URL`, `JUDGE_API_URL` (default `https://api.openai.com/v1/chat/completions`), `TARGET_MODEL`, `JUDGE_MODEL` (default `gpt-4.1-mini`) and `PROMPTS_FILE` (default `prompts.json`). Setting the URL to a local OpenAI-compatible server such as llama.cpp or Ollama works as well.

Build and run:

```bash
mvn package
java -jar target/llm-response-risk-analyzer-1.0.0.jar
```

Run the tests:

```bash
mvn test
```

## Project structure

```
pom.xml                   Maven build, dependencies pinned
prompts.json              Injection prompts by category (sensitive entries redacted)
spec.md                   Short design notes
src/main/java/evaluator/
    Main.java             Entry point and evaluation loop
    Config.java           Configuration from environment variables
    OpenAiChatClient.java HTTP client for OpenAI-compatible chat endpoints
    JudgeModelClient.java Judge prompt and verdict parsing
    PromptLoader.java     Loads and filters prompts.json
    EvaluationResult.java One judged prompt and response pair
    EvaluationSummary.java Label counts and overall verdict
src/test/java/evaluator/  Unit tests
```

## Prompt set and safety

The repository intentionally does not publish working jailbreak prompts. Sensitive entries in `prompts.json` are replaced with `REDACTED_` placeholders and skipped at load time. The three prompts that remain are harmless, widely documented pattern examples (for example "ignore all previous instructions"), kept so the harness can run end to end as a demo. No API keys are stored in the repository; all credentials come from environment variables.

## Limitations

- The judge is itself an LLM, so labels are not ground truth. Disagreement between judges is not measured.
- The published prompt set is small and static. It covers three injection categories and does not mutate or search for new attacks.
- Single-turn only. Multi-turn escalation attacks are out of scope.
- Results are printed to the console. CSV or JSON export is not implemented yet.

## License

MIT, see [LICENSE](LICENSE).

## Contact

Matvei Kandalintsev, contact@kandalintsev.com, [kandalintsev.com](https://kandalintsev.com)
