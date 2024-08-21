package com.example.demo.controllers;


import com.example.demo.config.JwtService;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.models.UserInput;
import com.example.demo.services.UserService;
import com.example.demo.specifications.TaskSpecifications;
import com.example.demo.specifications.UserSpecifications;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
//@RequestMapping("/api")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final HttpServletRequest request;
    public final PasswordEncoder encoder;

    @MutationMapping(value = "createUser")
    public ResponseEntity<User> createUser(@Argument User user) {
        User newUser = userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    @MutationMapping(value = "updateUser")
    public User updateUser(@Argument UserInput input) {
        UUID userId = jwtService.getUserIdFromRequest(request);
        if (userId != null) {
            Optional<User> existingUser = userService.getUserById(userId);
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                if (input.getUsername() != null) user.setUsername(input.getUsername());
                if (input.getEmail() != null) user.setEmail(input.getEmail());
                if (input.getFirstname() != null) user.setFirstname(input.getFirstname());
                if (input.getLastname() != null) user.setLastname(input.getLastname());
                if (input.getPassword() != null) user.setPassword(encoder.encode(input.getPassword()));
                User updatedUser = userService.updateUser(userId, user);
                return updatedUser;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @MutationMapping(value = "deleteUser")
    public ResponseEntity<Void> deleteUser(@Argument UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @QueryMapping(value = "userById")
    public Optional<User> getUserById() {
        UUID userId = jwtService.getUserIdFromRequest(request);
        Optional<User> user = userService.getUserById(userId);
        return user;
    }

    @QueryMapping(value = "getUserByEmail")
    public User getUserByEmail(@Argument String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.get();
    }

    @QueryMapping(value = "allUsers")
    public Page<User> getAllUsers(@Argument String filter, @Argument Integer offset, @Argument Integer limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Specification<User> spec = UserSpecifications.withFilter(filter);
        return userService.findAllUsers(spec, pageable);
    }
}
