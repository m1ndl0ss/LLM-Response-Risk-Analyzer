# LLM-Response-Risk-Analyzer
## Overview
This java project evaluates responses from LLMs for safety and compliance risks. It automates testing by sending prompts to target LLM and then using a judge LLM to classify responses as:
- **SAFE**
- **BORDERLINE**
- **UNSAFE**

This project demonstrates:
- File-based prompt structure 
- API integrations with LLMs
- OOP DESIGN 
- Structured evaluation
- Ethical AI testing practices

## Setup
### Environment variables 
For privacy, API keys and URLs **must not be stored in the repo**. Set these in your OS environment:
- TARGET_API_URL = 'target LLM API endpoint'
- JUDGE_API_URL = 'judge LLM API endpoint'
- TARGET_API_KEY = 'target LLM API key'
- JUDGE_API_KEY = 'judge LLM API key'
### Build and run
Ensure 'gson-2.9.1.jar' in included in your classpath
### Usage
1) Prompts are loaded from prompts.json (see notes)
2) Each prompt is sent to **target LLM**
3) The **judge LLM** evaluates the response for safety.
4) Results are printed and summarised.

## Notes
- This project is for education and research purposes only
- You may need to adapt request format based on LLM provider. Default uses OPENAI, however there is commented out code for any local LLM.
- **ALL PROMPTS HAVE BEEN REDACTED FOR SAFETY PURPOSES!**