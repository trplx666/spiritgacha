package com.spiritcard.spiritgacha.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiritcard.spiritgacha.services.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class GenerateAccountCredentials {

    @Autowired
    private RegisterService registerService;

    @Scheduled(fixedDelay = 600000) // 10 min
    public Map<String, String> generateAccountCredentials() {
        String url = "https://randomuser.me/api/";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            if (responseBody == null || responseBody.isEmpty()) {
                System.out.println("Empty response body from API.");
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            JsonNode resultsNode = rootNode.path("results");
            if (resultsNode.isArray() && resultsNode.size() > 0) {
                JsonNode firstResultNode = resultsNode.get(0);
                JsonNode loginNode = firstResultNode.path("login");
                String username = loginNode.path("username").asText();
                String password = passwordValidator(loginNode.path("password").asText());
                String email = username + "@gmail.com";
                Map<String, String> account = new HashMap<>();
                account.put(email, password);

                registerService.register(email,password);

                return account;
            } else {
                System.out.println("No results found in the JSON.");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error while fetching or parsing the API response: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static String passwordValidator(String password) {
        Random random = new Random();
        if (password.length() < 8) {
            while (password.length() != 8) {
                password = password + random.nextInt(9);
            }
        }
        return password;
    }
}
