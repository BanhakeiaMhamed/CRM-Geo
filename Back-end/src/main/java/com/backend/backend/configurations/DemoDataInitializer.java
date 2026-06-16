package com.backend.backend.configurations;

import com.backend.backend.metier.services.DemoDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
        name = "crm.demo.enabled", havingValue = "true", matchIfMissing = true)
public class DemoDataInitializer implements CommandLineRunner {

    private final DemoDataService demoDataService;

    @Override
    public void run(String... args) {
        demoDataService.seedFullDemoDataset();
    }
}
