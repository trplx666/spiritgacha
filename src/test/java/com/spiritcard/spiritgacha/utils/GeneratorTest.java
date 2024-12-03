package com.spiritcard.spiritgacha.utils;

import com.spiritcard.spiritgacha.services.RegisterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class GeneratorTest {

//    @Mock
//    private Response response;

    @InjectMocks
    private GenerateAccountCredentials generateAccountCredentials;

    @Test
    void testGenerator() {
        Map<String, String> account = generateAccountCredentials.generateAccountCredentials();

        for (Map.Entry<String, String> entry : account.entrySet()) {
            String email = entry.getKey();
            String password = entry.getValue();

            Assertions.assertNotNull(email, "Email cant be null");
            Assertions.assertTrue(email.contains("@gmail.com"), "Email should end @gmail.com");

            Assertions.assertNotNull(password, "Password cant be null");
            Assertions.assertTrue(password.length() == 8, "Password length must be 8 symbols");
        }
    }

}
