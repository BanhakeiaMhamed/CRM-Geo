package com.backend.backend.controllers;

import com.backend.backend.entities.User;
import com.backend.backend.metier.services.UserService;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

/**
 * @author M. BANHAKEIA
 *
 * CRUD Operations
 *
 * */
@RestController
@RequestMapping("/geo")
public class UserController {

    private UserService userService;
    private  static final Logger logger = LogManager.getLogger(UserController.class);
    @GetMapping("/test")

    public void test(){
        logger.info("Testing Api Users");
        System.out.println(" Your project has been executed successfully");
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List users = this.userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id) {
        Optional<User> user = this.userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        User createdUser = userService.createUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User updated = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
