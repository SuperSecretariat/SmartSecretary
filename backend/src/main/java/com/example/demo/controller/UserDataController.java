package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.entity.Student;
import com.example.demo.exceptions.DecryptionException;
import com.example.demo.entity.User;
import com.example.demo.repository.StudentRepository;
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

import static com.example.demo.util.AESUtil.decrypt;

@RestController
@RequestMapping("/api/user")
public class UserDataController {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;


    @Autowired
    public UserDataController(
            JwtUtil jwtUtil,
            UserDetailsServiceImpl userDetailsService,
            UserRepository userRepository,
            StudentRepository studentRepository
    ){
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
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
    public ResponseEntity<JwtResponse> updateUserProfile(@RequestBody UpdateProfileRequest updateRequest, @RequestHeader("Authorization") String headerAuth) throws DecryptionException {
        String token = headerAuth.substring(7);
        if(jwtUtil.validateJwtToken(token)){

            String registrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
            User currentUser = findUserByDecryptedAuthKey(registrationNumber);
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

    public User findUserByDecryptedAuthKey(String decryptedAuthKey) throws DecryptionException {
        for(User user : userRepository.findAll()){
            if(findStudent(user.getRegNumber())){
                if(user.getRegNumber().equals(decryptedAuthKey))
                    return user;
            }
            else{

                String decrypted = decrypt(user.getRegNumber());
                if(decrypted.equals(decryptedAuthKey))
                    return user;
            }
        }
        return null;
    }

    private boolean findStudent(String decryptAuthKey){
        for(Student student : studentRepository.findAll()){
            if(student.getRegNumber().equals(decryptAuthKey))
                return true;
        }
        return false;
    }

}
