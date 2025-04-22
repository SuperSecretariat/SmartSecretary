package com.example.demo.controller;

import com.example.demo.model.enums.ERole;
import com.example.demo.modelDB.Role;
import com.example.demo.modelDB.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/auth-admin")
public class AdminAuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/register-secretary")
    public ResponseEntity<?> registerSecretary(@Valid @RequestBody RegisterRequest registerRequest){
        Optional<User> optionalUser = userRepository.findByRegNumber(registerRequest.getIdNumber());

        if (optionalUser.isPresent()) {
            return ResponseEntity.status(409).body("Un cont cu acelasi numar matricol a fost creat deja");
        }

            User newUser = new User(
                    registerRequest.getLastName(),
                    registerRequest.getFirstName(),
                    registerRequest.getIdNumber(),
                    registerRequest.getUniversity(),
                    registerRequest.getFaculty(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword()
            );
        Optional<Role> secretaryRole = roleRepository.findByName(ERole.ROLE_SECRETARY);
        Set<Role> roles = new HashSet<>();
        roles.add(secretaryRole.get());
        newUser.setRoles(roles);
            userRepository.save(newUser);
            return ResponseEntity.ok("Cont creat cu succes!");

    }

}
