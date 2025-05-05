package com.example.demo.controller;

import com.example.demo.response.JwtResponse;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserDataController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

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
}
