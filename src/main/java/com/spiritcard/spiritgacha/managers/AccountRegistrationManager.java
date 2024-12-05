package com.spiritcard.spiritgacha.managers;

import com.spiritcard.spiritgacha.services.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class AccountRegistrationManager {

    @Autowired
    @Lazy
    private RegisterService registerService;

    private final ConcurrentLinkedQueue<Map.Entry<String, String>> registrationQueue = new ConcurrentLinkedQueue<>();
    private final Map<String, Integer> registrationAttempts  = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;


    public void addToQueue(String email, String password) {
        registrationQueue.add(Map.entry(email, password));
        System.out.println("Account added to registration queue: " + email);
    }

    public void processRegistrationQueue() {
        while (!registrationQueue.isEmpty()) {
            Map.Entry<String, String> account = registrationQueue.poll();
            if (account != null) {
                String email = account.getKey();
                String password = account.getValue();
                int attempts = registrationAttempts.getOrDefault(email, 0);

                if (attempts >= MAX_ATTEMPTS) {
                    System.err.println("Exceeded max registration attempts for: " + email);
                    continue;
                }

                try {
                    registerService.register(email, password);
                    registrationAttempts.remove(email);
                    System.out.println("Successfully registered account: " +email);
                } catch (Exception e) {
                    registrationAttempts.put(email, attempts +1);
                    System.out.println("Failed to register account: " +email + ". Attempts:" + attempts);
                    scheduleRegistrationRetry(email, password);
                }
            }

        }
    }

    private void scheduleRegistrationRetry(String email, String password) {
        registrationQueue.add(Map.entry(email, password));
        System.out.println("Retry scheduled for: " + email);
    }

    public boolean isQueueEmpty(){
        return registrationQueue.isEmpty();
    }
}
