package com.spiritcard.spiritgacha.context;

import com.spiritcard.spiritgacha.models.Account;

public class AccountContext {
    private static final ThreadLocal<Account> accountHolder = new ThreadLocal<>();

    public static void setAccount(Account account) {
        accountHolder.set(account);
    }

    public static Account getAccount() {
        return accountHolder.get();
    }

    public static void clear() {
        accountHolder.remove();
    }
}
