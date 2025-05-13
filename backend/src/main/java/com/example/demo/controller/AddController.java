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
import com.example.demo.request.AdminRequest;
import com.example.demo.request.SecretaryRequest;
import com.example.demo.request.StudentRequest;
import com.example.demo.response.JwtResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.demo.util.AESUtil.decrypt;
import static com.example.demo.util.AESUtil.encrypt;

@RestController
@RequestMapping("/api/add")
public class AddController {

    private final StudentRepository studentRepository;
    private final SecretaryRepository secretaryRepository;
    private final AdminRepository adminRepository;
    private static final Logger loggerAddController = LoggerFactory.getLogger(AddController.class);

    @Autowired
    public AddController(StudentRepository studentRepository,
                         SecretaryRepository secretaryRepository,
                         AdminRepository adminRepository){
        this.adminRepository = adminRepository;
        this.secretaryRepository = secretaryRepository;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/student")
    public ResponseEntity<JwtResponse> addStudent(@RequestBody StudentRequest studentRequest){
        if(!studentRepository.existsByRegNumber(studentRequest.getRegistrationNumber()))
        {
            Student newStudent = new Student(studentRequest.getRegistrationNumber(), studentRequest.getEmail());
            studentRepository.save(newStudent);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.STUDENT_ADDED));
        }
        else
            return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.REG_NUMBER_IN_USE));

    }

    @PostMapping("/secretary")
    public ResponseEntity<JwtResponse> addSecretary(@RequestBody SecretaryRequest secretaryRequest) throws DecryptionException, EncryptionException {
        if(!findSecretary(secretaryRequest.getAuthKey())){
            Secretary newSecretary = new Secretary(encrypt(secretaryRequest.getAuthKey()), secretaryRequest.getEmail());
            secretaryRepository.save(newSecretary);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.SECRETARY_ADDED));
        }
        else
            return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.AUTH_KEY_IN_USE));

    }

    @PostMapping("/admin")
    public ResponseEntity<JwtResponse> addAdmin(@RequestBody AdminRequest adminRequest) throws EncryptionException, DecryptionException {
        if(!findAdmin(adminRequest.getAuthKey()))
        {
            Admin newAdmin = new Admin(encrypt(adminRequest.getAuthKey()), adminRequest.getEmail());
            adminRepository.save(newAdmin);
            return ResponseEntity.ok(new JwtResponse(ValidationMessage.ADMIN_ADDED));
        }
        else
            return ResponseEntity.status(409).body(new JwtResponse(ErrorMessage.AUTH_KEY_IN_USE));


    }

    private boolean findSecretary(String decryptAuthKey) throws DecryptionException{
        for(Secretary secretary : secretaryRepository.findAll()){
            try{
                String tempAuthKey = decrypt(secretary.getAuthKey());
                if(tempAuthKey.equals(decryptAuthKey)){
                    return true;
                }
            } catch (DecryptionException e) {
                loggerAddController.error(e.getMessage());
            }
        }
        return false;
    }

    private boolean findAdmin(String decryptAuthKey) throws DecryptionException{
        for(Admin admin : adminRepository.findAll()){
            try{
                String tempAuthKey = decrypt(admin.getAuthKey());
                if(tempAuthKey.equals(decryptAuthKey)){
                    return true;
                }
            } catch (DecryptionException e) {
                loggerAddController.error(e.getMessage());
            }
        }
        return false;
    }
}
