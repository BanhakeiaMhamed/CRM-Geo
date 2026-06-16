package com.backend.backend.metier.services;

import com.backend.backend.Enums.Role;
import com.backend.backend.entities.Engineer;
import com.backend.backend.entities.Enterprise;
import com.backend.backend.repositories.EnterpriseRepository;
import com.backend.backend.repositories.IEngineerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final IEngineerRepository engineerRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final DemoDataService demoDataService;

    public String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("Utilisateur non authentifie");
        }
        return auth.getPrincipal().toString();
    }

    @Transactional
    public Engineer getCurrentEngineer() {
        String email = getCurrentEmail();
        return engineerRepository.findByEmail(email)
                .orElseGet(() -> provisionEngineer(email));
    }

    private Engineer provisionEngineer(String email) {
        Enterprise enterprise = demoDataService.ensureEnterprise();

        String localPart = email.split("@")[0];
        String firstName = localPart.substring(0, 1).toUpperCase() + localPart.substring(1);

        Engineer engineer = Engineer.builder()
                .firstName(firstName)
                .lastName("Utilisateur")
                .email(email)
                .userName(localPart)
                .password("FIREBASE_AUTH")
                .role(Role.INGENIEUR)
                .enterprise(enterprise)
                .build();
        engineer = engineerRepository.save(engineer);
        demoDataService.seedUserDemoData(engineer, enterprise);
        return engineer;
    }
}
