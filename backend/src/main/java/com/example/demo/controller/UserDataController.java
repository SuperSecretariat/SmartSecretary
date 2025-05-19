package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.entity.Admin;
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

                UserProfileData profileData = new UserProfileData(
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
                User currentUser = userRepository.findByRegNumber(registrationNumber).get();
                currentUser.setCnp(encrypt(updateRequest.getCnp()));
                currentUser.setFaculty(updateRequest.getFaculty());
                currentUser.setUniversity(updateRequest.getUniversity());
                currentUser.setDateOfBirth(updateRequest.getDateOfBirth());
                userRepository.save(currentUser);
                return ResponseEntity.ok(new JwtResponse(ValidationMessage.UPDATE_SUCCESS));
            }
            else
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        } catch (EncryptionException ex){
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ENCRYPTION_ERROR));
        }

    }

    @PostMapping("/delete-me")
    public ResponseEntity<JwtResponse> deleteCurrentUser(@RequestHeader("Authorization") String headerAuth){
        try{
            String token = headerAuth.substring(7);
            if(jwtUtil.validateJwtToken(token)){
                String registrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
                User currentUser = null;
                if(validationService.findStudent(registrationNumber) != null){
                    currentUser = validationService.findUserByIdentifier(registrationNumber);
                }
                else
                    currentUser = validationService.findUserByIdentifier(decrypt(registrationNumber));
                if(currentUser != null)
                {
                    userRepository.delete(currentUser);
                    return ResponseEntity.ok(new JwtResponse(ValidationMessage.ACCOUNT_DELETED));
                }
                else
                    return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
            }
            else
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        }catch (DecryptionException ex){
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.DECRYPTION_ERROR));
        }

    }

    @GetMapping("/authkey")
    public ResponseEntity<JwtResponse> showAuthenticationKey(@RequestHeader("Authorization") String headerAuth,
                                                             String email) throws DecryptionException {
        try{
            String token = headerAuth.substring(7);
            if(jwtUtil.validateJwtToken(token)){
                String authKey = jwtUtil.getRegistrationNumberFromJwtToken(token);
                Admin admin = validationService.findAdmin(decrypt(authKey));
                if(admin != null && validationService.findUserByIdentifier(decrypt(admin.getAuthKey())) != null){
                    Optional<User> optUser = userRepository.findByEmail(email);
                    if(optUser.isEmpty())
                        return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
                    else {
                        User user = optUser.get();
                        if(validationService.findStudent(user.getRegNumber()) != null){
                            return ResponseEntity.ok(new JwtResponse(user.getRegNumber()));
                        }
                        String authKeyUser = decrypt(optUser.get().getRegNumber());
                        return ResponseEntity.ok(new JwtResponse(authKeyUser));
                    }
                }
                else
                    return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.ACCESS_FORBIDDEN));
            }
            else
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        }catch (DecryptionException ex){
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.DECRYPTION_ERROR));
        }

    }

    @PostMapping("/delete-user")
    public ResponseEntity<JwtResponse> deleteUser(@RequestHeader("Authorization") String headerAuth,
                                                  String identifier) throws DecryptionException {
        try{
            String token = headerAuth.substring(7);
            if(jwtUtil.validateJwtToken(token)){
                String authKey = jwtUtil.getRegistrationNumberFromJwtToken(token);
                Admin admin = validationService.findAdmin(decrypt(authKey));
                if(admin != null && validationService.findUserByIdentifier(decrypt(admin.getAuthKey())) != null){
                    User userToDelete = validationService.findUserByIdentifier(identifier);
                    if(userToDelete == null){
                        return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
                    }
                    else{
                        userRepository.delete(userToDelete);
                        return ResponseEntity.ok(new JwtResponse(ValidationMessage.ACCOUNT_DELETED));
                    }
                }
                else
                    return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.ACCESS_FORBIDDEN));
            }
            else
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        }catch (DecryptionException ex){
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.DECRYPTION_ERROR));
        }

    }

}
