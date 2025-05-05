package com.example.demo.controller;

import com.example.demo.modelDB.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.UpdateProfileRequest;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserDataController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String headerAuth){
        try{
            String token = headerAuth.substring(7);

            if(jwtUtil.validateJwtToken(token)){
                String registrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);

                UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(registrationNumber);
                List<String> roles = userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.toList());

                JwtResponse response = new JwtResponse(token, userDetails.getUsername(), userDetails.getFirstName(), userDetails.getLastName(), userDetails.getEmail(), userDetails.getCnp(), userDetails.getUniversity(), userDetails.getFaculty(), roles);
                return ResponseEntity.ok(response);
            }
            else
                return ResponseEntity.status(401).body("Invalid token");
        }
        catch(Exception e){
            return ResponseEntity.status(401).body("An error has occured:" + e.getMessage());
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateProfileRequest updateRequest, @RequestHeader("Authorization") String headerAuth){
        try{
            String token = headerAuth.substring(7);

            if(jwtUtil.validateJwtToken(token)){
                Optional<User> user = userRepository.findById(updateRequest.getId());
                if(user.isPresent())
                {
                    User currentUser = user.get();
                    currentUser.setCnp(updateRequest.getCnp());
                    currentUser.setFaculty(updateRequest.getFaculty());
                    currentUser.setUniversity(updateRequest.getUniversity());
                    currentUser.setDateOfBirth(updateRequest.getDateOfBirth());
                    userRepository.save(currentUser);
                    return ResponseEntity.ok("Profile information updated successfully");
                }
                else
                    return ResponseEntity.status(404).body("There isn't an user with this id in the database");
            }
            else
                return ResponseEntity.status(401).body("Invalid token");
        }catch(Exception e)
        {
           return ResponseEntity.status(401).body("An error has occured: " + e.getMessage());
        }

    }
}
