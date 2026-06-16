package com.backend.backend.configurations;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Getter
public class FirebaseProperties {

    private final boolean enabled;
    private final Resource credentialsResource;

    public FirebaseProperties(@Value("${firebase.credentials.path}") Resource credentialsResource) {
        this.credentialsResource = credentialsResource;
        this.enabled = credentialsResource.exists();
    }
}
