package com.backend.backend.configurations;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Profile("!test")
public class FirebaseConfig {

    private static final Logger log = LogManager.getLogger(FirebaseConfig.class);

    private final FirebaseProperties firebaseProperties;

    public FirebaseConfig(FirebaseProperties firebaseProperties) {
        this.firebaseProperties = firebaseProperties;
    }

    @PostConstruct
    public void initializeFirebase() throws IOException {
        if (!firebaseProperties.isEnabled()) {
            log.warn("Firebase non configure : placez firebase-service-account.json dans src/main/resources "
                    + "ou definissez FIREBASE_CREDENTIALS_PATH. L'API demarre sans verification JWT.");
            return;
        }
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }
        try (InputStream stream = firebaseProperties.getCredentialsResource().getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(stream))
                    .build();
            FirebaseApp.initializeApp(options);
            log.info("Firebase Admin SDK initialise.");
        }
    }
}
