package com.spiritcard.spiritgacha.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoginService {

    @Autowired
    private PlayService playService;

    public List<String> getAuthCookies(String email, String password) {
        String url = "https://gacha.teamspirit.gg/api/login";
        String jsonBody = String.format("""
                {
                    "email": "%s",
                    "password": "%s",
                    "initial_card_id": 5
                }
                """, email, password);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json, text/plain, */*")
                .header("Referer", "https://gacha.teamspirit.gg/")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0")
                .header("Cookie", "ts_cards_session=eyJpdiI6ImFWZ25QdmsycStyQ2Q1TXNvbGJIMmc9PSIsInZhbHVlIjoieHNseDBGUUFrblZoc1UwVWVZc3pxUyt3NGgwTFNzcjVzTVNsSi9MeFhkRUQ2UHNrNGxvaXhCTmwwZGNNanl3VmRiMmZuQUc5alBoa0N5MDI4U2lJOS80SE9zcnlyV3ptU1FwTFRMYlVWWDBKdG1lVVc4OXZBRW0wYVdKM3k3OUwiLCJtYWMiOiJhNzg1ZDY5ZDZlN2FhY2M3MmMwYTQwZDJmMWRjYTU4YTJkYzAzNzM1YTAzZjQzNTc1Y2UwNzZkYzkzYzE5YzM5IiwidGFnIjoiIn0%3D")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode != 200) {
                throw new RuntimeException("Request failed with status code: " + statusCode
                        + ". Response: " + response.body());
            }

            List<String> cookies = response.headers()
                    .allValues("set-cookie")
                    .stream()
                    .map(cookie -> cookie.split(";")[0])
                    .collect(Collectors.toList());

//            System.out.println("Processed Cookies: " + cookies);
            playService.play(cookies);

            return cookies;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error while sending request: " + e.getMessage(), e);
        }
    }
}
