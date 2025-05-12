package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.exceptions.EncryptionException;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.UpdateProfileRequest;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.demo.util.AESUtil.encrypt;

@RestController
@RequestMapping("/api/user")
public class UserDataController {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private static final Logger loggerUserDataController = LoggerFactory.getLogger(UserDataController.class);

    @Autowired
    public UserDataController(
            JwtUtil jwtUtil,
            UserDetailsServiceImpl userDetailsService,
            UserRepository userRepository
    ){
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<JwtResponse> getUserProfile(@RequestHeader("Authorization") String headerAuth){
        String token = headerAuth.substring(7);

        if(jwtUtil.validateJwtToken(token)){
            String registrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);

            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(registrationNumber);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            JwtResponse response = new JwtResponse(token, userDetails.getUsername(), userDetails.getFirstName(), userDetails.getLastName(), userDetails.getEmail(), userDetails.getCnp(), userDetails.getDateOfBirth(), userDetails.getUniversity(), userDetails.getFaculty(), roles);
            return ResponseEntity.ok(response);
        }
        else
            return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
    }

    @PostMapping("/update")
    public ResponseEntity<JwtResponse> updateUserProfile(@RequestBody UpdateProfileRequest updateRequest, @RequestHeader("Authorization") String headerAuth){
        String token = headerAuth.substring(7);
        if(jwtUtil.validateJwtToken(token)){
            User currentUser;
            String registrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
            String tempKey = encryptException(registrationNumber);
            Optional<User> userStudent = userRepository.findByRegNumber(registrationNumber);
            Optional<User> userCrypt = userRepository.findByRegNumber(tempKey);
            boolean isAdmin = false;

            if(userStudent.isEmpty() && userCrypt.isEmpty())
            {
                return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));
            }

            if(userCrypt.isPresent())
                isAdmin = true;

            if(isAdmin)
                currentUser = userCrypt.get();
            else
                currentUser = userStudent.get();
            currentUser.setCnp(updateRequest.getCnp());
            currentUser.setFaculty(updateRequest.getFaculty());
            currentUser.setUniversity(updateRequest.getUniversity());
            currentUser.setDateOfBirth(updateRequest.getDateOfBirth());
            userRepository.save(currentUser);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.UPDATE_SUCCESS));
        }
        else
            return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
    }
    @PostMapping("/delete-me")
    public ResponseEntity<JwtResponse> deleteCurrentUser(@RequestHeader("Authorization") String headerAuth){
        String token = headerAuth.substring(7);
        if(jwtUtil.validateJwtToken(token)){
            String registrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
            Optional<User> userStudent = userRepository.findByRegNumber(registrationNumber);
            if(userStudent.isPresent())
            {
                userRepository.delete(userStudent.get());
                return ResponseEntity.ok(new JwtResponse(ValidationMessage.ACCOUNT_DELETED));
            }


        }
        return ResponseEntity.status(404).body(new JwtResponse(ErrorMessage.NON_EXISTENT_USER));

    }
    private static String encryptException(String input){
        try{
            return encrypt(input);
        } catch (EncryptionException e) {
            loggerUserDataController.error(e.getMessage());
            return "";
        }
    }
}
