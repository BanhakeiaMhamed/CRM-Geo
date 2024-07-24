package com.backend.backend.repositories;

import com.backend.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserById(Long id);
    Optional<List<User>> findByUserName(String userName);
    Optional<List<User>> findByFirstName(String userName);
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    void delete(User user);
    User save(User user);
}
