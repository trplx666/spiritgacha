package com.spiritcard.spiritgacha.services;

import com.spiritcard.spiritgacha.context.AccountContext;
import com.spiritcard.spiritgacha.managers.AccountRegistrationManager;
import com.spiritcard.spiritgacha.models.Account;
import com.spiritcard.spiritgacha.repository.AccountRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class RegisterService {

    @Autowired
    private LoginService loginService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    @Lazy
    private AccountRegistrationManager accountRegistrationManager;

    public Account register(String email, String password) {
        String url = "https://napi.teamspirit.gg/graphql";

        JSONObject credentials = new JSONObject();
        credentials.put("email", email);
        credentials.put("password", password);

        JSONObject requestBody = new JSONObject();
        requestBody.put("operationName", "DoSignUp");
        requestBody.put("query", "mutation DoSignUp($credentials: SignupInput!) {signup(credentials: $credentials) {accessToken}}");
        requestBody.put("variables", new JSONObject().put("credentials", credentials));

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/graphql-response+json, application/graphql+json, application/json, text/event-stream, multipart/mixed")
                .header("Content-Type", "application/json")
                .header("Referer", "https://shop.teamspirit.gg/")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .header("Cookie", "_fbp=fb.1.1733155578169.785159027600238116")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            int statusCode = response.statusCode();

            JSONObject jsonResponse = new JSONObject(responseBody);
            if (jsonResponse.has("data") && jsonResponse.get("data") != JSONObject.NULL) {
                JSONObject data = jsonResponse.getJSONObject("data");
                if (data.has("signup") && data.getJSONObject("signup").has("accessToken")) {
                    String accessToken = data.getJSONObject("signup").getString("accessToken");
                    Account account = new Account(email, password);
                    AccountContext.setAccount(account);
                    System.out.println("Регистрация прошла успешно для " + email);
                    loginService.getAuthCookies(email, password);
                    return account;
                } else {
                    throw new RuntimeException("Invalid response: accessToken not found in response.");
                }
            } else {
                throw new RuntimeException("Invalid response: 'data' is null or missing.");
            }

        } catch (Exception e) {
            System.err.println("Ошибка при обработке JSON: " + e.getMessage());
            throw new RuntimeException("Error during registration process", e);
        }
    }
}
