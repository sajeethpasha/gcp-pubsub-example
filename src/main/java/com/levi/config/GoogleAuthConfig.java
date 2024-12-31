package com.levi.config;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

//@Configuration
public class GoogleAuthConfig {

//    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        return GoogleCredentials.fromStream(new FileInputStream("/path-to-your-service-account-key.json"))
                                .createScoped("https://www.googleapis.com/auth/cloud-platform");
    }
}
