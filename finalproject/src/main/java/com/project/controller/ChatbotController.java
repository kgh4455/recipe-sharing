package com.project.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/chatbot")
@CrossOrigin(origins = "http://localhost:5173") 
public class ChatbotController {

    private static final String API_URL = "https://983ktt5pc1.apigw.ntruss.com/custom/v1/17172/809c2fb94fff223e96477a875bbc1b77e5b020262e8f959d4b49455100a7f9f7";
    private static final String SECRET_KEY = "cmdDU0tjRUNxTXVkYkpHU1lSYkxWYU1rV1FXdFdWRno=";

    @PostMapping("/ask")
    public String askChatbot(@RequestBody Map<String, String> requestBody) {
        String message = requestBody.get("message");

        System.out.println("클라이언트에서 받은 메시지: " + message);

        if (message == null || message.trim().isEmpty()) {
            return "메시지가 비어 있습니다.";
        }

        try {
            String requestBodyJson = getReqMessage(message);
            String signature = makeSignature(requestBodyJson, SECRET_KEY);

            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;UTF-8");
            connection.setRequestProperty("X-NCP-CHATBOT_SIGNATURE", signature);
            connection.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(requestBodyJson.getBytes("UTF-8"));
                wr.flush();
            }

            int responseCode = connection.getResponseCode();
            System.out.println("API 응답 코드: " + responseCode);

            BufferedReader br = (responseCode == 200) ?
                    new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8")) :
                    new BufferedReader(new InputStreamReader(connection.getErrorStream(), "UTF-8"));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            System.out.println("API 응답 데이터: " + response.toString());

            return extractChatbotReply(response.toString());

        } catch (Exception e) {
            System.err.println("오류 발생: " + e.getMessage());
            return "오류 발생: " + e.getMessage();
        }
    }

    private static String extractChatbotReply(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            if (!jsonObject.has("bubbles")) return "챗봇 응답이 없습니다.";

            JSONArray bubblesArray = jsonObject.getJSONArray("bubbles");
            if (bubblesArray.length() > 0) {
                JSONObject firstBubble = bubblesArray.getJSONObject(0);
                JSONObject data = firstBubble.getJSONObject("data");
                return data.getString("description");
            }
        } catch (Exception e) {
            return "응답 처리 중 오류 발생: " + e.getMessage();
        }
        return "응답 없음.";
    }

    private static String makeSignature(String message, String secretKey) throws Exception {
        byte[] secretKeyBytes = secretKey.getBytes("UTF-8");
        SecretKeySpec signingKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    private static String getReqMessage(String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("version", "v2");
        jsonObject.put("userId", UUID.randomUUID().toString());

        JSONArray bubblesArray = new JSONArray();
        JSONObject bubble = new JSONObject();
        bubble.put("type", "text");

        JSONObject data = new JSONObject();
        data.put("description", message);
        bubble.put("data", data);
        bubblesArray.put(bubble);

        jsonObject.put("bubbles", bubblesArray);
        jsonObject.put("event", "send");
        jsonObject.put("timestamp", new Date().getTime());

        return jsonObject.toString();
    }
}
