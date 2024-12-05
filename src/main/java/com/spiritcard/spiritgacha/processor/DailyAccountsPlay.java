package com.spiritcard.spiritgacha.processor;

import com.spiritcard.spiritgacha.models.Account;
import com.spiritcard.spiritgacha.repository.AccountRepository;
import com.spiritcard.spiritgacha.services.LoginService;
import com.spiritcard.spiritgacha.services.PlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailyAccountsPlay {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private PlayService playService;

    @Scheduled(cron = "0 1 21 * * *") // 00:01 msk
    public void ProcessDataBaseAccounts() {
        List<Account> accounts = accountRepository.findAll();
        System.out.println("Обработка всех аккаунтов из бд. Найдено: " + accounts.size() + " аккаунтов");

        for (Account account : accounts) {
            String email = account.getEmail();
            String password = account.getPassword();

            try {
                List<String> cookies = loginService.getAuthCookies(email,password);

                playService.play(cookies);
            } catch (Exception e) {
                System.err.println("Произошла ошибка при обработке аккаунта " +email);
            }
        }
    }
}
