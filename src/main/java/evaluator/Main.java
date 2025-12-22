package main.java.evaluator;

public class Main {
    public static void main(String[] args) {
        try {
            PromptLoader loader = new PromptLoader("prompts.json");
            System.out.println("Role-play prompts: " + loader.getRolePlayPrompts());
            System.out.println("Instuction Overloading prompts: " + loader.getInstructionOverloadPrompts());
            System.out.println("Ethical Framing prompts: " + loader.getEthicalFramingPrompts());

            String apiURl = System.getenv("TARGET_API_URL");
            String apiKey = System.getenv("TARGET_API_KEY");
            TargetModelClient targetModelClient = new TargetModelClient(apiKey,apiURl);

            for (String prompt : loader.getInstructionOverloadPrompts()){
                String response = targetModelClient.sendPrompt(prompt);
                System.out.println("prompt : " + prompt);
                System.out.println("response : " + response);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}