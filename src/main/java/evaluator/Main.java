package main.java.evaluator;

public class Main {
    public static void main(String[] args) {
        try {
            PromptLoader loader = new PromptLoader("prompts.json");
            System.out.println("Role-play prompts: " + loader.getRolePlayPrompts());
            System.out.println("Instuction Overloading prompts: " + loader.getInstructionOverloadPrompts());
            System.out.println("Ethical Framing prompts: " + loader.getEthicalFramingPrompts());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}