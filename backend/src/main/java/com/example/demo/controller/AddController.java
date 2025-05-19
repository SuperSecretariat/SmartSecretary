package com.example.demo.controller;


import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationMessage;
import com.example.demo.entity.Admin;
import com.example.demo.entity.Secretary;
import com.example.demo.entity.Student;
import com.example.demo.exceptions.DecryptionException;
import com.example.demo.exceptions.EncryptionException;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.SecretaryRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.dto.AdminRequest;
import com.example.demo.dto.SecretaryRequest;
import com.example.demo.dto.StudentRequest;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.ValidationService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.util.AESUtil.decrypt;
import static com.example.demo.util.AESUtil.encrypt;

@RestController
@RequestMapping("/api/add")
public class AddController {

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
                         JwtUtil jwtUtil) {
        this.adminRepository = adminRepository;
        this.secretaryRepository = secretaryRepository;
        this.studentRepository = studentRepository;
        this.validationService = validationService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/student")
    public ResponseEntity<JwtResponse> addStudent(@RequestHeader("Authorization") String headerAuth,
                                                  @RequestBody StudentRequest studentRequest) {
        try{
            String token = headerAuth.substring(7);

            if (jwtUtil.validateJwtToken(token)) {
                String authKey = jwtUtil.getRegistrationNumberFromJwtToken(token);
                Secretary secretary = validationService.findSecretary(decrypt(authKey));
                if (secretary != null && validationService.findUserByIdentifier(decrypt(secretary.getAuthKey())) != null) {
                    if (validationService.isEmailUsed(studentRequest.getEmail())) {
                        return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.EMAIL_IN_USE));
                    }
                    if (!validationService.isAuthKeyUsed(studentRequest.getRegistrationNumber())) {
                        Student newStudent = new Student(studentRequest.getRegistrationNumber(), studentRequest.getEmail());
                        studentRepository.save(newStudent);
                        return ResponseEntity.ok(new JwtResponse(ValidationMessage.STUDENT_ADDED));
                    } else
                        return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.REG_NUMBER_IN_USE));
                } else
                    return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.ACCESS_FORBIDDEN));

            } else
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        } catch (DecryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.DECRYPTION_ERROR));
        }

    }

    @PostMapping("/secretary")
    public ResponseEntity<JwtResponse> addSecretary(@RequestHeader("Authorization") String headerAuth,
                                                    @RequestBody SecretaryRequest secretaryRequest){
        try{
            String token = headerAuth.substring(7);

            if (jwtUtil.validateJwtToken(token)) {
                String authKey = jwtUtil.getRegistrationNumberFromJwtToken(token);
                Admin admin = validationService.findAdmin(decrypt(authKey));
                if (admin != null && validationService.findUserByIdentifier(decrypt(admin.getAuthKey())) != null) {
                    if (!validationService.isAuthKeyUsed(secretaryRequest.getAuthKey())) {
                        Secretary newSecretary = new Secretary(encrypt(secretaryRequest.getAuthKey()), secretaryRequest.getEmail());
                        secretaryRepository.save(newSecretary);
                        return ResponseEntity.ok(new JwtResponse(ValidationMessage.SECRETARY_ADDED));
                    } else
                        return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.AUTH_KEY_IN_USE));
                } else
                    return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.ACCESS_FORBIDDEN));
            } else
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        } catch (EncryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ENCRYPTION_ERROR));
        } catch (DecryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.DECRYPTION_ERROR));
        }

    }

    @PostMapping("/admin")
    public ResponseEntity<JwtResponse> addAdmin(@RequestHeader("Authorization") String headerAuth,
                                                @RequestBody AdminRequest adminRequest) {
        try {
            String token = headerAuth.substring(7);
            if (jwtUtil.validateJwtToken(token)) {
                String authKey = jwtUtil.getRegistrationNumberFromJwtToken(token);
                Admin admin = validationService.findAdmin(decrypt(authKey));
                if (admin != null && validationService.findUserByIdentifier(decrypt(admin.getAuthKey())) != null) {
                    if (!validationService.isAuthKeyUsed(adminRequest.getAuthKey())) {
                        Admin newAdmin = new Admin(encrypt(adminRequest.getAuthKey()), adminRequest.getEmail());
                        adminRepository.save(newAdmin);
                        return ResponseEntity.ok(new JwtResponse(ValidationMessage.ADMIN_ADDED));
                    } else
                        return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.AUTH_KEY_IN_USE));
                } else
                    return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.ACCESS_FORBIDDEN));
            } else
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));

        } catch (EncryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.ENCRYPTION_ERROR));
        } catch (DecryptionException ex) {
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.DECRYPTION_ERROR));
        }

    }

    @PostMapping("/admin/test")
    public ResponseEntity<JwtResponse> addAdminDirect(@RequestBody AdminRequest adminRequest) {
        try {
            System.out.println(adminRequest.getAuthKey() + " " + adminRequest.getEmail());

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
