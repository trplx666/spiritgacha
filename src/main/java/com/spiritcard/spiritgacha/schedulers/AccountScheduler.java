package com.spiritcard.spiritgacha.schedulers;

import com.spiritcard.spiritgacha.managers.AccountRegistrationManager;
import com.spiritcard.spiritgacha.utils.GenerateAccountCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AccountScheduler {

    @Autowired
    private GenerateAccountCredentials generateAccountCredentials;

    @Autowired
    AccountRegistrationManager accountRegistrationManager;

    @Scheduled(fixedRate = 300000) // 5m
    public void generateAndQueueAccounts() {
        generateAccountCredentials.generateAndQueueAccounts();
    }

    @Scheduled(fixedRate = 600000) // 10m
    public void processAccountFromQueue() {
        accountRegistrationManager.processRegistrationQueue();
    }
}
