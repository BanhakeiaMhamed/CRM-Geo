package com.backend.backend.repositories;

import com.backend.backend.entities.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {
    Optional<Enterprise> findBySiret(String siret);
    Optional<Enterprise> findByName(String name);
}
