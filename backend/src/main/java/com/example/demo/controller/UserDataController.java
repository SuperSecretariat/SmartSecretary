package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.entity.User;
import com.example.demo.exceptions.DecryptionException;
import com.example.demo.exceptions.EncryptionException;
import com.example.demo.repository.StudentRepository;
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

import java.util.List;
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
            StudentRepository studentRepository,
            ValidationService validationService
    ){
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @GetMapping("/profile")
    public ResponseEntity<JwtResponse> getUserProfile(@RequestHeader("Authorization") String headerAuth){
        try{
            String token = headerAuth.substring(7);

            if(jwtUtil.validateJwtToken(token)){
                String registrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);

                UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(registrationNumber);
                List<String> roles = userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .toList();

                UserProfileData profileData;
                if(userDetails.getCnp() == null)
                {
                    profileData = new UserProfileData(
                            token,
                            userDetails.getUsername(),
                            userDetails.getFirstName(),
                            userDetails.getLastName(),
                            userDetails.getEmail(),
                            null,
                            userDetails.getDateOfBirth(),
                            userDetails.getUniversity(),
                            userDetails.getFaculty(),
                            roles
                    );
                }
                else
                {
                    profileData = new UserProfileData(
                            token,
                            userDetails.getUsername(),
                            userDetails.getFirstName(),
                            userDetails.getLastName(),
                            userDetails.getEmail(),
                            decrypt(userDetails.getCnp()),
                            userDetails.getDateOfBirth(),
                            userDetails.getUniversity(),
                            userDetails.getFaculty(),
                            roles
                    );
                }

                JwtResponse response = new JwtResponse(profileData);
                return ResponseEntity.ok(response);
            }
            else
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        }catch (DecryptionException ex){
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.DECRYPTION_ERROR));
        }

    }

    @PostMapping("/update")
    public ResponseEntity<JwtResponse> updateUserProfile(@RequestBody UpdateProfileRequest updateRequest, @RequestHeader("Authorization") String headerAuth) {
        try{
            String token = headerAuth.substring(7);
            if(jwtUtil.validateJwtToken(token)){

                String registrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
                Optional<User> userOptional = userRepository.findByRegNumber(registrationNumber);
                if (userOptional.isPresent()) {
                    User currentUser = userOptional.get();
                    currentUser.setCnp(encrypt(updateRequest.getCnp()));
                    currentUser.setFaculty(updateRequest.getFaculty());
                    currentUser.setUniversity(updateRequest.getUniversity());
                    currentUser.setDateOfBirth(updateRequest.getDateOfBirth());
                    userRepository.save(currentUser);
                }
                return ResponseEntity.ok(new JwtResponse(ValidationMessage.UPDATE_SUCCESS));
            }
            else
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        } catch (EncryptionException ex){
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ENCRYPTION_ERROR));
        }

    }

    @PostMapping("/delete-me")
    public ResponseEntity<JwtResponse> deleteCurrentUser(@RequestHeader("Authorization") String headerAuth) {
        try {
            String token = headerAuth.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            String regNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
            User currentUser = findCurrentUser(regNumber);
            if (currentUser == null) {
                return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
            }

            userRepository.delete(currentUser);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.ACCOUNT_DELETED));

        } catch (DecryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.DECRYPTION_ERROR));
        }
    }

    private User findCurrentUser(String regNumber) throws DecryptionException {
        return (validationService.findStudent(regNumber) != null)
                ? validationService.findUserByIdentifier(regNumber)
                : validationService.findUserByIdentifier(decrypt(regNumber));
    }

    @GetMapping("/authkey")
    public ResponseEntity<JwtResponse> showAuthenticationKey(@RequestHeader("Authorization") String headerAuth,
                                                             @RequestParam String email) {
        try {
            String token = headerAuth.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            if (!validationService.isRequestAuthorizedAdmin(token, jwtUtil)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.ACCESS_FORBIDDEN));
            }

            Optional<User> optUser = userRepository.findByEmail(email);
            if (optUser.isEmpty()) {
                return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
            }

            User user = optUser.get();
            String regNumber = user.getRegNumber();

            String responseKey = validationService.findStudent(regNumber) != null
                    ? regNumber
                    : decrypt(regNumber);

            return ResponseEntity.ok(new JwtResponse(responseKey));

        } catch (DecryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.DECRYPTION_ERROR));
        }
    }

    @PostMapping("/delete-user")
    public ResponseEntity<JwtResponse> deleteUser(@RequestHeader("Authorization") String headerAuth,
                                                  @RequestParam String identifier) {
        try {
            String token = headerAuth.substring(7);

            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            if (!validationService.isRequestAuthorizedAdmin(token, jwtUtil)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.ACCESS_FORBIDDEN));
            }

            User userToDelete = validationService.findUserByIdentifier(identifier);
            if (userToDelete == null) {
                return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
            }

            userRepository.delete(userToDelete);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.ACCOUNT_DELETED));

        } catch (DecryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.DECRYPTION_ERROR));
        }
    }

}
