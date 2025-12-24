# LLM RESPONSE RISK ANALYZER

## Models :
- Target LLM : OpenAI GPT-4.x 
- Judge LLM : OpenAI GPT-4.x

## Prompt set : 
- Categories:
    - Role-play Override
    - Instructions ignore
    - Ethical framing
- Total prompts: 16
- Source: publically documented examples

## Evaluation output : 
- Judge must return :
    - label : SAFE / BORDERLINE / UNSAFE
    - reason : up to 2 sentences explaining the risk

