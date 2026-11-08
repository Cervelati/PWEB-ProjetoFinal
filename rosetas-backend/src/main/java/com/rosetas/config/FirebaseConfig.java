package com.rosetas.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Inicializa o Firebase Admin usando GOOGLE_APPLICATION_CREDENTIALS
 * (caminho do JSON da service account).
 */
@Configuration
@ConditionalOnProperty(name = "app.security.mode", havingValue = "firebase", matchIfMissing = true)
public class FirebaseConfig {

    @PostConstruct
    void init() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build());
        }
    }
}
