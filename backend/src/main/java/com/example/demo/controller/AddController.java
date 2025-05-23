package com.example.demo.controller;


import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Secretary;
import com.example.demo.entity.Student;
import com.example.demo.exceptions.EncryptionException;
import com.example.demo.model.enums.ERole;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.SecretaryRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.dto.AdminRequest;
import com.example.demo.dto.SecretaryRequest;
import com.example.demo.dto.StudentRequest;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.service.ValidationService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.util.AESUtil.encrypt;

@RestController
@RequestMapping("/api/add")
public class AddController {

    private final UserDetailsServiceImpl userDetailsService;
    private final StudentRepository studentRepository;
    private final SecretaryRepository secretaryRepository;
    private final AdminRepository adminRepository;
    private final ValidationService validationService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AddController(StudentRepository studentRepository,
                         SecretaryRepository secretaryRepository,
                         AdminRepository adminRepository,
                         ValidationService validationService,
                         JwtUtil jwtUtil,
                         UserDetailsServiceImpl userDetailsService) {
        this.adminRepository = adminRepository;
        this.secretaryRepository = secretaryRepository;
        this.studentRepository = studentRepository;
        this.validationService = validationService;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/student")
    public ResponseEntity<JwtResponse> addStudent(@RequestHeader("Authorization") String headerAuth,
                                                  @RequestBody StudentRequest studentRequest) {
            String token = headerAuth.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            String regNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(regNumber);

            if (!userDetails.hasRole(ERole.ROLE_SECRETARY)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.ACCESS_FORBIDDEN));
            }

            if (validationService.isEmailUsed(studentRequest.getEmail())) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.EMAIL_IN_USE));
            }

            if (validationService.isAuthKeyUsed(studentRequest.getRegistrationNumber())) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.REG_NUMBER_IN_USE));
            }

            Student newStudent = new Student(studentRequest.getRegistrationNumber(), studentRequest.getEmail());
            studentRepository.save(newStudent);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.STUDENT_ADDED));
    }

    @PostMapping("/secretary")
    public ResponseEntity<JwtResponse> addSecretary(@RequestHeader("Authorization") String headerAuth,
                                                    @RequestBody SecretaryRequest secretaryRequest) {
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

            if (validationService.isAuthKeyUsed(secretaryRequest.getAuthKey())) {
                return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.AUTH_KEY_IN_USE));
            }

            Secretary newSecretary = new Secretary(encrypt(secretaryRequest.getAuthKey()), secretaryRequest.getEmail());
            secretaryRepository.save(newSecretary);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.SECRETARY_ADDED));

        } catch (EncryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ENCRYPTION_ERROR));
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<JwtResponse> addAdmin(@RequestHeader("Authorization") String headerAuth,
                                                @RequestBody AdminRequest adminRequest) {
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

            if (validationService.isAuthKeyUsed(adminRequest.getAuthKey())) {
                return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.AUTH_KEY_IN_USE));
            }

            Admin newAdmin = new Admin(encrypt(adminRequest.getAuthKey()), adminRequest.getEmail());
            adminRepository.save(newAdmin);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.ADMIN_ADDED));

        } catch (EncryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ENCRYPTION_ERROR));
        }
    }

    @PostMapping("/admin/test")
    public ResponseEntity<JwtResponse> addAdminDirect(@RequestBody AdminRequest adminRequest) {
        try {
            if (!validationService.isAuthKeyUsed(adminRequest.getAuthKey())) {
                Admin newAdmin = new Admin(encrypt(adminRequest.getAuthKey()), adminRequest.getEmail());
                adminRepository.save(newAdmin);
                return ResponseEntity.ok(new JwtResponse(ValidationMessage.ADMIN_ADDED));
            } else {
                return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.AUTH_KEY_IN_USE));
            }

        } catch (EncryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ENCRYPTION_ERROR));
        }
    }
}
