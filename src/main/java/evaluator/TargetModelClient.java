package main.java.evaluator;

import com.google.gson.JsonObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class TargetModelClient {
    private final String apiURL;
    private final String apiKey;
    public TargetModelClient(String apiKey, String apiURL){
        this.apiKey=apiKey;
        this.apiURL=apiURL;
    }

    public String sendPrompt(String prompt){
        try {
            URL url = new URL(this.apiURL); //turn it into URL java can understand
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST"); //llm apis expect post
            connection.setRequestProperty("Content-Type", "application/json"); //im sending json
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);//authenticates using api
            connection.setDoOutput(true);

            JsonObject body = new JsonObject();
            body.addProperty("prompt", prompt); //what llm acc receives
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = body.toString().getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }

            int code = connection.getResponseCode();
            InputStream inputStream;
            if (code>=200 && code<300){ //succesfull codes
                inputStream = connection.getInputStream();
            }
            else {
                inputStream=connection.getErrorStream();
            }

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line.trim());
                }
                return response.toString();
            }
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }

}
