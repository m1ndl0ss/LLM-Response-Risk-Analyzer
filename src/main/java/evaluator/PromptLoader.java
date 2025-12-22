package main.java.evaluator;

import com.google.gson.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
    

public class PromptLoader {
    private final List<String> rolePlayPrompts = new ArrayList<>();
    private final List<String> instructionOverloadPrompts = new ArrayList<>();
    private final List<String> ethicalFramingPrompts = new ArrayList<>();

    public PromptLoader(String filepath) throws IOException{
        loadPrompts(filepath);
    }

    private void loadPrompts(String filepath) throws IOException{
        JsonObject root = JsonParser.parseReader(new FileReader(filepath)).getAsJsonObject();
        JsonArray categories = root.getAsJsonArray("categories");

        for (JsonElement categoryElement : categories){
            JsonObject category = categoryElement.getAsJsonObject();
            String name = category.get("name").getAsString();
            JsonArray prompts = category.getAsJsonArray("prompts");

            for (JsonElement promptElement : prompts){
                String prompt = promptElement.getAsString();

                switch (name){
                    case "role_play_override":
                        rolePlayPrompts.add(prompt);
                        break;
                    case "instruction_override":
                        instructionOverloadPrompts.add(prompt);
                        break;
                    case "ethical_framing":
                        ethicalFramingPrompts.add(prompt);
                        break;
                    default:
                        System.out.println("unknown category");
                        break;
                }
            }
        }
    }
    public List<String> getRolePlayPrompts(){return rolePlayPrompts;}
    public List<String> getInstructionOverloadPrompts(){return instructionOverloadPrompts;}
    public List<String> getEthicalFramingPrompts(){return ethicalFramingPrompts;}
}