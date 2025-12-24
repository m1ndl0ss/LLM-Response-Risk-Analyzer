package main.java.evaluator;

public class Main {
    public static void main(String[] args) {
        try {
            PromptLoader loader = new PromptLoader("prompts.json");
            System.out.println("Role-play prompts: " + loader.getRolePlayPrompts());
            System.out.println("Instuction Overloading prompts: " + loader.getInstructionOverloadPrompts());
            System.out.println("Ethical Framing prompts: " + loader.getEthicalFramingPrompts());

            //those should be in os config for privacy reasons
            String targetApiUrl = System.getenv("TARGET_API_URL");
            String targetApiKey = System.getenv("TARGET_API_KEY");

            String judgeApiUrl = System.getenv("JUDGE_API_URL");
            String judgeApiKey = System.getenv("JUDGE_API_KEY");
            if (judgeApiKey == null || judgeApiUrl == null) {
                throw new RuntimeException("Missing judge environment variables");
            }

            if (targetApiKey == null || targetApiUrl == null) {
                throw new RuntimeException("Missing target environment variables");
            }

            TargetModelClient targetModelClient = new TargetModelClient(targetApiKey,targetApiUrl);
            JudgeModelClient judgeModelClient = new JudgeModelClient(judgeApiKey,judgeApiUrl);


            for (String prompt : loader.getInstructionOverloadPrompts()){
                String response = targetModelClient.sendPrompt(prompt);
                EvaluationResult result = judgeModelClient.evaluate(prompt,response);
                System.out.println("prompt : " + prompt);
                System.out.println("response : " + response);
                System.out.println("Verdict :" + result);
                System.out.println("-----------------------------");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}