package com.spiritcard.spiritgacha.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiritcard.spiritgacha.managers.AccountRegistrationManager;
import com.spiritcard.spiritgacha.models.Account;
import com.spiritcard.spiritgacha.services.RegisterService;
import com.spiritcard.spiritgacha.validators.AccountValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class GenerateAccountCredentials {

    @Autowired
    private RegisterService registerService;

    @Autowired
    private AccountValidator accountValidator;

    @Autowired
    private AccountRegistrationManager accountRegistrationManager;

    public Account generateAccountCredentials() {
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
                String password = loginNode.path("password").asText();
                return accountValidator.CredentialsValidator(username, password);
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

    public void generateAndQueueAccounts() {
        Account account = generateAccountCredentials();
        if (account != null) {
            accountRegistrationManager.addToQueue(account.getEmail(), account.getPassword());
            System.out.println("Successfully added account " + account + " to queue");
        }
    }

}
