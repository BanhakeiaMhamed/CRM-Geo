package com.backend.backend.repositories;

import com.backend.backend.entities.Engineer;
import com.backend.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IEngineerRepository extends JpaRepository<Engineer, Long> {
}
