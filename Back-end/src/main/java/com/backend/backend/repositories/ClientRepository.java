package com.backend.backend.repositories;

import com.backend.backend.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c JOIN c.engineer e WHERE e.enterprise.id = :enterpriseId")
    List<Client> findByEnterpriseId(@Param("enterpriseId") Long enterpriseId);
}
