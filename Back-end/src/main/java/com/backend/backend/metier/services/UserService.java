package com.backend.backend.metier.services;

import com.backend.backend.entities.User;
import com.backend.backend.exceptions.UserNotFoundException;
import com.backend.backend.repositories.IUserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {

    private IUserRepository userRepository;
    public List<User> getAllUsers() {
       return this.userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return this.userRepository.findUserById(id);
    }

    public User createUser(User user) {
        return this.userRepository.save(user);
    }


    public User updateUser(Long id, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);

        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            // Mettre à jour les propriétés du userToUpdate avec les valeurs de updatedUser
            userToUpdate.setUserName(updatedUser.getUserName());
            userToUpdate.setEmail(updatedUser.getEmail());
            userToUpdate.setFirstName(updatedUser.getFirstName());
            userToUpdate.setLastName(updatedUser.getLastName());
            return userRepository.save(userToUpdate);
        } else {
            throw new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + id);
        }
    }

    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + id);
        }
    }
}
