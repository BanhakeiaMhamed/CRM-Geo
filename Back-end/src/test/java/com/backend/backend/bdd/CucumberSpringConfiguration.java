package com.backend.backend.bdd;

import com.backend.backend.BackEndApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = BackEndApplication.class)
@ActiveProfiles("test")
@Import({BddContextConfig.class, BddTestSecurityConfig.class})
public class CucumberSpringConfiguration {
}
