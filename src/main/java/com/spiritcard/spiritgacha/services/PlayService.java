package com.spiritcard.spiritgacha.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spiritcard.spiritgacha.context.AccountContext;
import com.spiritcard.spiritgacha.models.Account;
import com.spiritcard.spiritgacha.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class PlayService {

    @Autowired
    private AccountRepository accountRepository;

    public void play(List<String> cookies) {
        String stringCookies = String.join("; ", cookies);
        String url = "https://gacha.teamspirit.gg/api/play";
        String jsonBody = "{}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("accept", "application/json")
                .header("referer", "https://gacha.teamspirit.gg/")
                .header("cookie", stringCookies)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        int tickets = 0;
        int openCount = 0;
        while (openCount < 10) {
            HttpResponse<String> response = null;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                System.err.println("Ошибка при выполнении запроса: " + e.getMessage());
                continue;
            }

            int statusCode = response.statusCode();
            if (statusCode != 200) {
                System.err.println("Ошибка статуса: " + statusCode);
                break;
            }

            String responseBody = response.body();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(responseBody);

                JsonNode cardNode = rootNode.path("card");
                if (!cardNode.isMissingNode()) {
                    int cardId = cardNode.path("id").asInt();
                    String cardName = cardNode.path("name").asText();
                    String cardRarity = cardNode.path("type").asText();
                    System.out.println( "CardName: " + cardName + ", Rarity: " + cardRarity);
                }

                JsonNode stateNode = rootNode.path("state");
                if (!stateNode.isMissingNode()) {
                    JsonNode userNode = stateNode.path("user");
                    if (!userNode.isMissingNode()) {
                        String userName = userNode.path("name").asText();
                        tickets = userNode.path("tickets").asInt();
                    }
                }

            } catch (Exception e) {
                System.err.println("Ошибка при обработке JSON: " + e.getMessage());
            }

            openCount++;

            if (openCount == 10) {
                System.out.println("Tickets: " + tickets);
                if (tickets >= 0) {
                    Account account = AccountContext.getAccount();
                    if (account != null) {
                        int totalOpenCount = account.getOpenCount();
                        totalOpenCount += openCount;

                        account.setTickets(tickets);
                        account.setOpenCount(totalOpenCount);
                        accountRepository.save(account);
                        if (tickets > 0){
                            System.out.println("ВЫПАЛА ЗОЛОТАЯ КАРТОЧКА!!! на аккаунт: " + account);
                        }
                        System.out.println("Аккаунт сохранён в базе данных: " + account);
                    }
                }
                break;
            }
        }
        AccountContext.clear();
    }
}
