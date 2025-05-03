package com.example.demo.controller;

import com.example.demo.model.enums.ERole;
import com.example.demo.modelDB.*;
import com.example.demo.repository.*;
import com.example.demo.request.LoginRequest;
import com.example.demo.request.RegisterRequest;
import com.example.demo.response.JwtResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.demo.util.AESUtil.decrypt;
import static com.example.demo.util.AESUtil.encrypt;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SecretaryRepository secretaryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByRegNumber(loginRequest.getRegistrationNumber());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User doesn't exist");
        }
        Set<Role> userRole = optionalUser.get().getRoles();

        List<String> roleNames = userRole.stream().map(role -> role.getName().toString()).toList();
        if (passwordEncoder.matches(loginRequest.getPassword(), optionalUser.get().getPassword())) {
            User user = optionalUser.get();
            return ResponseEntity.ok(new JwtResponse("placeholder-token", user.getId(), user.getRegNumber(), roleNames));
        } else
            return ResponseEntity.status(401).body("Incorrect password");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        ERole newAccountRole = null;
        if(userRepository.existsByRegNumber(registerRequest.getRegistrationNumber()))
        {
            return ResponseEntity.status(409).body("An account with the the same registration number/authentication key has already been created");
        }
        else{
            String tempAuthKey = encryptException(registerRequest.getRegistrationNumber());
            if(studentRepository.existsByRegNumber(registerRequest.getRegistrationNumber()))
                newAccountRole = ERole.ROLE_STUDENT;
            else if(secretaryRepository.existsByAuthKey(tempAuthKey))
                newAccountRole = ERole.ROLE_SECRETARY;
            else if(adminRepository.existsByAuthKey(tempAuthKey))
                newAccountRole = ERole.ROLE_ADMIN;
            else
                return ResponseEntity.status(404).body("Registration number/authentication key are not valid");
        }
        if(validateData(registerRequest, newAccountRole)){
            Optional<Role> accountRole = roleRepository.findByName(newAccountRole);
            Set<Role> roles = new HashSet<>();
            roles.add(accountRole.get());
            String tempKey = registerRequest.getRegistrationNumber();
            String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
            if(newAccountRole.equals(ERole.ROLE_ADMIN) || newAccountRole.equals(ERole.ROLE_SECRETARY))
                encryptException(tempKey);
            User newUser = new User(
                    registerRequest.getLastName(),
                    registerRequest.getFirstName(),
                    tempKey,
                    null,
                    null,
                    registerRequest.getEmail(),
                    hashedPassword,
                    null,
                    null
            );
            newUser.setRoles(roles);
            userRepository.save(newUser);
            return ResponseEntity.ok("Account Created successfully");
        }
        else
            return ResponseEntity.status(400).body("Data provided is wrong");
    }

    private boolean validateData(RegisterRequest registerRequest, ERole registerRole){
        String tempAuthKey = encryptException(registerRequest.getRegistrationNumber());
        switch(registerRole){
            case ROLE_STUDENT:
                Student tempStudent = studentRepository.findByRegNumber(registerRequest.getRegistrationNumber()).get();
                if(tempStudent.getRegNumber().equals(registerRequest.getRegistrationNumber()) && tempStudent.getEmail().equals(registerRequest.getEmail())){
                    return true;
                }
                else
                    return false;

            case ROLE_SECRETARY:

                Secretary tempSecretary = secretaryRepository.findByAuthKey(tempAuthKey).get();
                String tempSecretaryKey = decryptException(tempSecretary.getAuthKey());
                if(tempSecretaryKey.equals(registerRequest.getRegistrationNumber()) && tempSecretary.getEmail().equals(registerRequest.getEmail()))
                {
                    return true;
                }
                else
                    return false;

            case ROLE_ADMIN:
                Admin tempAdmin = adminRepository.findByAuthKey(tempAuthKey).get();
                String tempAdminKey = decryptException(tempAdmin.getAuthKey());
                if(tempAdminKey.equals(registerRequest.getRegistrationNumber()) && tempAdmin.getEmail().equals(registerRequest.getEmail())){
                    return true;
                }
                else
                    return false;

            default:
                return false;
        }
    }

    private static String encryptException(String input){
        try{
            return encrypt(input);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static String decryptException(String input){
        try{
            return decrypt(input);
        } catch(Exception e){
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
