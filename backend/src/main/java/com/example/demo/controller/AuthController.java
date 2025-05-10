package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.exceptions.DecryptionException;
import com.example.demo.exceptions.EncryptionException;
import com.example.demo.model.enums.ERole;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.request.LoginRequest;
import com.example.demo.request.RegisterRequest;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
import java.util.stream.Collectors;

import static com.example.demo.util.AESUtil.decrypt;
import static com.example.demo.util.AESUtil.encrypt;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final SecretaryRepository secretaryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(
            UserRepository userRepository,
            RoleRepository roleRepository,
            StudentRepository studentRepository,
            SecretaryRepository secretaryRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.studentRepository = studentRepository;
        this.secretaryRepository = secretaryRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // determine whether user is admin/secretary (encrypted regNumber) or student (plain regNumber)
        String encryptedReg = safeEncrypt(loginRequest.getRegistrationNumber());
        Optional<User> userOpt = userRepository.findByRegNumber(encryptedReg);
        boolean isAdminOrSec = true;

        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByRegNumber(loginRequest.getRegistrationNumber());
            isAdminOrSec = false;
        }

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
        }
        User user = userOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INCORRECT_PASSWORD));
        }

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String token = jwtUtil.generateJwtToken(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().toString())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(token, user.getId(), user.getRegNumber(), roles));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest req) {
        // check email uniqueness
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.AUTH_KEY_IN_USE));
        }

        // figure out role based on existing records
        ERole role = resolveRole(req);
        if (role == null) {
            return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        }

        // validate data against existing record
        if (!validateData(req, role)) {
            return ResponseEntity.status(400).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        }

        // prepare user entity
        Optional<Role> optRole = roleRepository.findByName(role);
        if (optRole.isEmpty()) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.UNKNOWN_ERROR));
        }
        Set<Role> roles = Set.of(optRole.get());
        String regNumToStore = role == ERole.ROLE_STUDENT ? req.getRegistrationNumber() : safeEncrypt(req.getRegistrationNumber());
        String hashedPwd = passwordEncoder.encode(req.getPassword());

        User newUser = new User(
                req.getLastName(), req.getFirstName(),
                regNumToStore, null, null,
                req.getEmail(), hashedPwd, null, null
        );
        newUser.setRoles(roles);
        userRepository.save(newUser);

        return ResponseEntity.ok(new JwtResponse(ValidationMessage.ACCOUNT_SUCCESS));
    }

    @PostMapping("/logout")
    public ResponseEntity<JwtResponse> logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            jwtUtil.invalidateToken(token);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.LOGOUT_SUCCESS));
        }
        return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.UNKNOWN_ERROR));
    }

    @GetMapping("/me")
    public ResponseEntity<JwtResponse> getCurrentUser(Authentication auth) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(null, userDetails.getId(), userDetails.getUsername(), roles));
    }

    private ERole resolveRole(RegisterRequest req) {
        String encrypted = safeEncrypt(req.getRegistrationNumber());
        if (studentRepository.existsByRegNumber(req.getRegistrationNumber())) {
            return ERole.ROLE_STUDENT;
        } else if (secretaryRepository.existsByAuthKey(encrypted)) {
            return ERole.ROLE_SECRETARY;
        } else if (adminRepository.existsByAuthKey(encrypted)) {
            return ERole.ROLE_ADMIN;
        }
        return null;
    }

    private boolean validateData(RegisterRequest req, ERole role) {
        String rawReg = req.getRegistrationNumber();
        String encrypted = safeEncrypt(rawReg);
        switch (role) {
            case ROLE_STUDENT:
                return studentRepository.findByRegNumber(rawReg)
                        .map(s -> rawReg.equals(s.getRegNumber()) && req.getEmail().equals(s.getEmail()))
                        .orElse(false);
            case ROLE_SECRETARY:
                return secretaryRepository.findByAuthKey(encrypted)
                        .map(sec -> {
                            String storedKey = sec.getAuthKey();
                            if (storedKey == null) return false;
                            String decrypted = safeDecrypt(storedKey);
                            return rawReg.equals(decrypted) && req.getEmail().equals(sec.getEmail());
                        })
                        .orElse(false);
            case ROLE_ADMIN:
                return adminRepository.findByAuthKey(encrypted)
                        .map(adm -> {
                            String storedKey = adm.getAuthKey();
                            if (storedKey == null) return false;
                            String decrypted = safeDecrypt(storedKey);
                            return rawReg.equals(decrypted) && req.getEmail().equals(adm.getEmail());
                        })
                        .orElse(false);
            default:
                return false;
        }
    }

    /**
     * Wraps AES encrypt and returns empty on failure
     */
    private String safeEncrypt(String input) {
        try {
            return encrypt(input);
        } catch (EncryptionException ex) {
            logger.error("Encryption failed for {}: {}", input, ex.getMessage());
            return null;
        }
    }

    /**
     * Wraps AES decrypt and returns empty on failure
     */
    private String safeDecrypt(String input) {
        if (input == null) return null;
        try {
            return decrypt(input);
        } catch (DecryptionException ex) {
            logger.error("Decryption failed for {}: {}", input, ex.getMessage());
            return null;
        }
    }
}
