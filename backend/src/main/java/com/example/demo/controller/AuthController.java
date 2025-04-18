package com.example.demo.controller;

import com.example.demo.model.enums.ERole;
import com.example.demo.modelDB.Role;
import com.example.demo.modelDB.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.LoginRequest;
import com.example.demo.request.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByIdNumber(loginRequest.getIdNumber());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(loginRequest.getPassword()))
                return ResponseEntity.ok("Logarea s-a finalizat cu succes");
            else
                return ResponseEntity.status(401).body("Parola incorecta");
        } else {
            return ResponseEntity.status(404).body("Contul nu exista");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByIdNumber(registerRequest.getIdNumber());

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
        Optional<Role> studentRole = roleRepository.findByName(ERole.ROLE_STUDENT);
        Set<Role> roles = new HashSet<>();
        roles.add(studentRole.get());
        newUser.setRoles(roles);

        userRepository.save(newUser);
        return ResponseEntity.ok("Cont creat cu succes!");
    }
}
