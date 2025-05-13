package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.exceptions.DecryptionException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.example.demo.util.AESUtil.decrypt;

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
                    .toList();

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

            String registrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
            User currentUser = findUserByDecryptedAuthKeyPaginated(registrationNumber);
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

    public User findUserByDecryptedAuthKeyPaginated(String decryptedAuthKey) {
        final int PAGE_SIZE = 100;
        int pageNumber = 0;
        boolean hasMore = true;

        while (hasMore) {
            Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE);
            Page<User> userPage = userRepository.findAll(pageable);

            if (userPage.isEmpty()) {
                hasMore = false;
                continue;
            }
            for (User user : userPage.getContent()) {
                String regNumber = user.getRegNumber();
                try {
                    if (regNumber == null) {
                        continue;
                    }
                    String decryptedRegNumber = decrypt(regNumber);
                    if (decryptedRegNumber.equals(decryptedAuthKey)) {
                        return user;
                    }
                } catch (DecryptionException e) {
                    loggerUserDataController.debug("Failed to decrypt registration number for user ID {}: {}", user.getId(), e.getMessage());
                    if (regNumber.equals(decryptedAuthKey)) {
                        return user;
                    }
                }
            }
            pageNumber++;
            hasMore = userPage.hasNext();
        }

        pageNumber = 0;
        hasMore = true;

        while (hasMore) {
            Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE);
            Page<User> userPage = userRepository.findAll(pageable);

            if (userPage.isEmpty()) {
                hasMore = false;
                continue;
            }
            for (User user : userPage.getContent()) {
                String regNumber = user.getRegNumber();
                if(regNumber.equals(decryptedAuthKey)){
                    return user;
                }
            }
            pageNumber++;
            hasMore = userPage.hasNext();
        }
        return null;
    }
}
