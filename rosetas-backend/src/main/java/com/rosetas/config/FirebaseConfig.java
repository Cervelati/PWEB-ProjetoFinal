
package com.rosetas.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@ConditionalOnProperty(
    name = "app.security.mode",
    havingValue = "firebase",
    matchIfMissing = true
)
public class FirebaseConfig {

    @PostConstruct
    void init() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        String json = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");

        if (json == null || json.isBlank()) {
            throw new IllegalStateException(
                "FIREBASE_SERVICE_ACCOUNT_JSON nao configurada"
            );
        }

        try (ByteArrayInputStream stream = new ByteArrayInputStream(
                json.getBytes(StandardCharsets.UTF_8))) {

            GoogleCredentials credentials =
                    GoogleCredentials.fromStream(stream);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }
}

