    package com.spiritcard.spiritgacha.validators;


    import com.spiritcard.spiritgacha.models.Account;
    import org.springframework.stereotype.Component;

    import java.util.Random;

    @Component
    public class AccountValidator {

        public Account CredentialsValidator(String username, String password) {
            String email = username + "@gmail.com";
            String validatedPassword = passwordValidator(password);

            return new Account(email, validatedPassword);
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
