package com.example.demo.services;


import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.models.Task;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class UserService {

    @Autowired
    public UserRepository repository;

    public User createUser(User user) {
        return repository.save(user);
    };

    public User updateUser(UUID id, User userDetails){

        Optional<User> userOptional = repository.findById(id);
        if(userOptional.isPresent()){
            User existingUser = userOptional.get();

            existingUser.setFirstname(userDetails.getFirstname());
            existingUser.setLastname(userDetails.getLastname());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setPassword(userDetails.getPassword());

            return repository.save(existingUser);
        } else {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }
    public void deleteUser(UUID id){
        repository.deleteById(id);
    }
    @Transactional
    public Optional<User> getUserById(UUID id){
try{
        return repository.findById(id);
    } catch (Exception e) {
        e.printStackTrace();
    }
return null;
}
    public List<User> getAllUsers(){
        return repository.findAll();
    }

    public Page<User> findAllUsers(Specification<User> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    public Optional<User> getUserByEmail(String email) {
        return repository.findByEmail(email);
    }
}
