package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.entity.User;
import com.example.demo.exceptions.DecryptionException;
import com.example.demo.exceptions.EncryptionException;
import com.example.demo.model.enums.ERole;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.response.JwtResponse;
import com.example.demo.dto.UserProfileData;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.service.ValidationService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.example.demo.util.AESUtil.decrypt;
import static com.example.demo.util.AESUtil.encrypt;

@RestController
@RequestMapping("/api/user")
public class UserDataController {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final ValidationService validationService;


    @Autowired
    public UserDataController(
            JwtUtil jwtUtil,
            UserDetailsServiceImpl userDetailsService,
            UserRepository userRepository,
            ValidationService validationService
    ){
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @GetMapping("/profile")
    public ResponseEntity<JwtResponse> getUserProfile(@RequestHeader("Authorization") String headerAuth) {
        try {
            String token = headerAuth.substring(7);

            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            String registrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(registrationNumber);

            String decryptedCnp = userDetails.getCnp() != null ? decrypt(userDetails.getCnp()) : null;

            UserProfileData profileData = new UserProfileData(
                    token,
                    userDetails.getUsername(),
                    userDetails.getFirstName(),
                    userDetails.getLastName(),
                    userDetails.getEmail(),
                    decryptedCnp,
                    userDetails.getDateOfBirth(),
                    userDetails.getUniversity(),
                    userDetails.getFaculty(),
                    userDetails.getRoleNames()
            );

            return ResponseEntity.ok(new JwtResponse(profileData));
        } catch (DecryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.DECRYPTION_ERROR));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<JwtResponse> updateUserProfile(@RequestBody UpdateProfileRequest updateRequest,
                                                         @RequestHeader("Authorization") String headerAuth) {
        try {
            String token = headerAuth.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            String regNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(regNumber);
            Optional<User> userOptional = userRepository.findById(userDetails.getId());

            if (userOptional.isPresent()) {
                User currentUser = userOptional.get();
                currentUser.setCnp(encrypt(updateRequest.getCnp()));
                currentUser.setFaculty(updateRequest.getFaculty());
                currentUser.setUniversity(updateRequest.getUniversity());
                currentUser.setDateOfBirth(updateRequest.getDateOfBirth());
                userRepository.save(currentUser);
            }

            return ResponseEntity.ok(new JwtResponse(ValidationMessage.UPDATE_SUCCESS));
        } catch (EncryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ENCRYPTION_ERROR));
        }
    }

    @PostMapping("/delete-me")
    public ResponseEntity<JwtResponse> deleteCurrentUser(@RequestHeader("Authorization") String headerAuth) {
            String token = headerAuth.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            String regNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(regNumber);

            User user = userRepository.findById(userDetails.getId()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
            }

            userRepository.delete(user);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.ACCOUNT_DELETED));
    }

    @GetMapping("/authkey")
    public ResponseEntity<JwtResponse> showAuthenticationKey(@RequestHeader("Authorization") String headerAuth,
                                                             @RequestParam String email) {
        try {
            String token = headerAuth.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            String regNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(regNumber);

            if (!userDetails.hasRole(ERole.ROLE_ADMIN)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.ACCESS_FORBIDDEN));
            }

            Optional<User> optUser = userRepository.findByEmail(email);
            if (optUser.isEmpty()) {
                return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
            }

            String regNum = optUser.get().getRegNumber();
            String responseKey = validationService.findStudent(regNum) != null
                    ? regNum
                    : decrypt(regNum);

            return ResponseEntity.ok(new JwtResponse(responseKey));

        } catch (DecryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.DECRYPTION_ERROR));
        }
    }

    @PostMapping("/delete-user")
    public ResponseEntity<JwtResponse> deleteUser(@RequestHeader("Authorization") String headerAuth,
                                                  @RequestParam String identifier) {
            String token = headerAuth.substring(7);

            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            String regNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(regNumber);

            if (!userDetails.hasRole(ERole.ROLE_ADMIN)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.ACCESS_FORBIDDEN));
            }

            User userToDelete = validationService.findUserByIdentifier(identifier);
            if (userToDelete == null) {
                return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
            }

            userRepository.delete(userToDelete);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.ACCOUNT_DELETED));

    }

}
