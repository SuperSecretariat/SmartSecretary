package com.example.demo.controller;

import com.example.demo.model.enums.ERole;
import com.example.demo.modelDB.*;
import com.example.demo.repository.*;
import com.example.demo.request.LoginRequest;
import com.example.demo.request.RegisterRequest;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        String tempKey = encryptException(loginRequest.getRegistrationNumber());
        Optional<User> optionalUser = userRepository.findByRegNumber(tempKey);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User doesn't exist");
        }
        Set<Role> userRole = optionalUser.get().getRoles();

        List<String> roleNames = userRole.stream().map(role -> role.getName().toString()).toList();
        if (passwordEncoder.matches(loginRequest.getPassword(), optionalUser.get().getPassword())) {
            User user = optionalUser.get();
            UserDetailsImpl userDetails = UserDetailsImpl.build(user);

            String token = jwtUtil.generateJwtToken(
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
            );
            return ResponseEntity.ok(new JwtResponse(token, user.getId(), user.getRegNumber(), roleNames));
        } else
            return ResponseEntity.status(401).body("Incorrect password");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        ERole newAccountRole = null;
        String email = registerRequest.getEmail();
        if(userRepository.existsByEmail(email))
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
            {
                tempKey = encryptException(tempKey);
            }

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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        String token = null;
        System.out.println(headerAuth);

        if(headerAuth != null && headerAuth.startsWith("Bearer ")){
            token = headerAuth.substring(7);

            jwtUtil.invalidateToken(token);

            return ResponseEntity.ok("User logged out successfully!");
        }
        return ResponseEntity.status(401).body("Error when logging out the user");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> rolesName = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

        JwtResponse response = new JwtResponse(null, userDetails.getId(), userDetails.getUsername(), rolesName);

        return ResponseEntity.ok(response);
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
