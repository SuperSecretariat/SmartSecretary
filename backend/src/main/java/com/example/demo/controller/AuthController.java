package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.exceptions.DecryptionException;
import com.example.demo.exceptions.EmailSendingException;
import com.example.demo.exceptions.EncryptionException;
import com.example.demo.model.enums.ERole;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.dto.ForgotPasswordRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ResetPasswordRequest;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.service.ValidationService;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static com.example.demo.util.AESUtil.decrypt;
import static com.example.demo.util.AESUtil.encrypt;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ValidationService validationService;
    private static final Logger loggerAuthController = LoggerFactory.getLogger(AuthController.class);
    private EmailService emailService;

    @Autowired
    public AuthController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            EmailService emailService,
            ValidationService validationService,
            UserDetailsServiceImpl userDetailsService
    ){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.validationService = validationService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        User currentUser = validationService.findUserByIdentifier(loginRequest.getRegistrationNumber());
        if (currentUser == null) {
            return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), currentUser.getPassword())) {
            return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INCORRECT_PASSWORD));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(currentUser.getRegNumber());

        String token = jwtUtil.generateJwtToken(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        List<String> roleNames = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toList();

        return ResponseEntity.ok(new JwtResponse(token, userDetails.getId(), userDetails.getUsername(), roleNames));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest registerRequest) {
        try {
            String email = registerRequest.getEmail();
            String regNumber = registerRequest.getRegistrationNumber();

            if (userRepository.existsByEmail(email).booleanValue()) {
                return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.AUTH_KEY_IN_USE));
            }

            ERole newAccountRole = determineRole(regNumber);
            if (newAccountRole == null) {
                return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            if (!validateData(registerRequest, newAccountRole)) {
                return ResponseEntity.status(400).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
            String identifier = newAccountRole.equals(ERole.ROLE_STUDENT) ? regNumber : encrypt(regNumber);

            User newUser = new User(
                    registerRequest.getLastName(),
                    registerRequest.getFirstName(),
                    identifier,
                    null,
                    null,
                    email,
                    hashedPassword,
                    null,
                    null
            );

            Optional<Role> accountRole = roleRepository.findByName(newAccountRole);
            accountRole.ifPresent(role -> {
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                newUser.setRoles(roles);
            });

            userRepository.save(newUser);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.ACCOUNT_SUCCESS));

        } catch (EncryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ENCRYPTION_ERROR));
        } catch (DecryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.DECRYPTION_ERROR));
        }
    }

    private ERole determineRole(String regNumber) throws DecryptionException{
        if (validationService.findStudent(regNumber) != null) return ERole.ROLE_STUDENT;
        if (validationService.findSecretary(regNumber) != null) return ERole.ROLE_SECRETARY;
        if (validationService.findAdmin(regNumber) != null) return ERole.ROLE_ADMIN;
        return null;
    }

    @PostMapping("/logout")
    public ResponseEntity<JwtResponse> logout(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        String token = null;

        if(headerAuth != null && headerAuth.startsWith("Bearer ")){
            token = headerAuth.substring(7);

            jwtUtil.invalidateToken(token);

            return ResponseEntity.ok(new JwtResponse(ValidationMessage.LOGOUT_SUCCESS));
        }
        return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.UNKNOWN_ERROR));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<JwtResponse> forgotPassword(@RequestBody ForgotPasswordRequest request){
        String email = request.getEmail();
        Optional<User> temporaryUser = userRepository.findByEmail(email);
        if(temporaryUser.isEmpty()){
            return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
        }

        User user = temporaryUser.get();
        String resetPasswordToken = jwtUtil.generatePasswordResetToken(user.getEmail());
        String resetLink = "http://localhost:4200/reset-password?token=" + resetPasswordToken;

        String subject = "Reset your password";
        String content = "Link to reset your accounts password: " + resetLink;
        try{
            emailService.sendEmail(user.getEmail(), subject, content);
        } catch (EmailSendingException e) {
            loggerAuthController.error("Error while sending the email");
        }

        return ResponseEntity.ok(new JwtResponse(ValidationMessage.EMAIL_SUCCESS));

    }

    @PostMapping("/reset-password")
    public ResponseEntity<JwtResponse> resetPassword(@RequestBody ResetPasswordRequest request){
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        if(!jwtUtil.validateJwtToken(token)){
            return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        }

        String email = jwtUtil.getRegistrationNumberFromJwtToken(token);
        Optional<User> tempUser = userRepository.findByEmail(email);

        if(tempUser.isEmpty()){
            return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
        }

        User user = tempUser.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        jwtUtil.invalidateToken(token);

        return ResponseEntity.ok(new JwtResponse(ValidationMessage.UPDATE_SUCCESS));
    }

    @GetMapping("/me")
    public ResponseEntity<JwtResponse> getCurrentUser(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> rolesName = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList();

        JwtResponse response = new JwtResponse(null, userDetails.getId(), userDetails.getUsername(), rolesName);

        return ResponseEntity.ok(response);
    }


    private boolean validateData(RegisterRequest request, ERole role) {
        try {
            String regNumber = request.getRegistrationNumber();
            String email = request.getEmail();

            switch (role) {
                case ROLE_STUDENT:
                    return validateStudent(regNumber, email);
                case ROLE_SECRETARY:
                    return validateSecretary(regNumber, email);
                case ROLE_ADMIN:
                    return validateAdmin(regNumber, email);
                default:
                    return false;
            }
        } catch (DecryptionException ex) {
            loggerAuthController.error("Decryption failed: {}", ex.getMessage());
            return false;
        }
    }

    private boolean validateStudent(String regNumber, String email) {
        Student student = validationService.findStudent(regNumber);
        return student != null &&
                student.getRegNumber().equals(regNumber) &&
                student.getEmail().equals(email);
    }

    private boolean validateSecretary(String regNumber, String email) throws DecryptionException {
        Secretary secretary = validationService.findSecretary(regNumber);
        return secretary != null &&
                decrypt(secretary.getAuthKey()).equals(regNumber) &&
                secretary.getEmail().equals(email);
    }

    private boolean validateAdmin(String regNumber, String email) throws DecryptionException {
        Admin admin = validationService.findAdmin(regNumber);
        return admin != null &&
                decrypt(admin.getAuthKey()).equals(regNumber) &&
                admin.getEmail().equals(email);
    }



}
