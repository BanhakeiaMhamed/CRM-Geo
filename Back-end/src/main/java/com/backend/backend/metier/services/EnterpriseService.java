package com.backend.backend.metier.services;

import com.backend.backend.Enums.Role;
import com.backend.backend.entities.Enterprise;
import com.backend.backend.entities.Engineer;
import com.backend.backend.repositories.EnterpriseRepository;
import com.backend.backend.repositories.IEngineerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;
    private final IEngineerRepository engineerRepository;

    @Transactional
    public Enterprise createEnterprise(String name, String siret, String email) {
        Enterprise enterprise = Enterprise.builder()
                .name(name)
                .siret(siret)
                .email(email)
                .build();
        return enterpriseRepository.save(enterprise);
    }

    @Transactional
    public Engineer attachEngineer(Enterprise enterprise, String firstName, String lastName,
                                   String email, String userName) {
        Engineer engineer = Engineer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .userName(userName)
                .password("CHANGE_ME")
                .role(Role.INGENIEUR)
                .enterprise(enterprise)
                .build();
        engineer = engineerRepository.save(engineer);
        enterprise.getEngineers().add(engineer);
        enterpriseRepository.save(enterprise);
        return engineer;
    }

    public Enterprise findByName(String name) {
        return enterpriseRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Entreprise introuvable : " + name));
    }

    public Engineer findEngineerByEmail(String email) {
        return engineerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Ingénieur introuvable : " + email));
    }
}
